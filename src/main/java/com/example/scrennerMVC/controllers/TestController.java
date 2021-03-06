package com.example.scrennerMVC.controllers;

import com.example.scrennerMVC.models.*;
import com.example.scrennerMVC.models.data.*;
import org.hibernate.annotations.MapKeyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.persistence.MapKeyColumn;
import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Table;
import com.google.common.collect.HashBasedTable;


@Controller
@RequestMapping("test")
public class TestController {


    @Autowired
    QuestionDao questionDao;

    @Autowired
    TestDao testDao;

    @Autowired
    AnswerDao answerDao;

    @Autowired
    UserDao userDao;

    @Autowired
    ScoreDao scoreDao;

    @RequestMapping(value="", method = RequestMethod.GET)
    public String displayTests(Model model, HttpSession session){

        User currentUser = (User) session.getAttribute("loggedInUser");

        List<Test> tests = new ArrayList<>();

        for (Test test : testDao.findAll()){
            if  (!test.getTestTakers().contains(userDao.findOne(currentUser.getId()))){ //if the current logged in user did not take the test?
                tests.add(test);
            }
        }


        model.addAttribute("title", "Available Tests!");
        model.addAttribute("tests",tests);
        return "test/allTests";

    }


    @RequestMapping(value = "/newtest", method = RequestMethod.GET)
    public String displayAddTest(Model model) {

        model.addAttribute("title", "New Test");
        model.addAttribute(new Test());

        return "test/newTest";
    }

    @RequestMapping(value = "/newtest", method = RequestMethod.POST)
    public String processAddTest(Model model, @ModelAttribute @Valid Test test, BindingResult errors, HttpSession session) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "New Test");
            return "test/newTest";
        }

        User currentUser = (User) session.getAttribute("loggedInUser");
        test.setTestCreator(currentUser);
        testDao.save(test);
        int testId = test.getId();
        String stringTestId = Integer.toString(testId);

        return "redirect:/test/newquestion/" + stringTestId;
    }


    @RequestMapping(value = "/newquestion/{testId}", method = RequestMethod.GET)
    public String displayAddQuestion(Model model, @PathVariable int testId) {


        //not sure if I need the next two lines
        Test currentTest = testDao.findOne(testId);
        model.addAttribute(currentTest);
        model.addAttribute("title", "New Question");
        model.addAttribute(new Question());

        return "test/newQuestion";

    }


    @RequestMapping(value = "/newquestion/{testId}", method = RequestMethod.POST)
    public String processAddQuestion(Model model, @ModelAttribute @Valid Question question, BindingResult errors, @PathVariable int testId,
                                     @RequestParam(required = false) Integer desiredAnswer1, @RequestParam(required = false) Integer desiredAnswer2, HttpSession session, @RequestParam String question2, @RequestParam Boolean matchingOpposite) {
        //TODO: Rewrite error functionality to display more specific messages and accurate messages.
        //
        //the if statement below fixes issue where it redirects to error code page when there is a second question
        // but no first question AND now first answer

        if (question.getQuestion1() == "" || question.getQuestion1() == null || errors.hasErrors()){
            model.addAttribute("title", "New Question");
            model.addAttribute("isError", "Try again!! Please look over possible mistakes in this question.");
            return "test/newQuestion";
        }

        if (question2.equals("")){
            question2 = null;
            desiredAnswer2 = null;
            matchingOpposite = null; //rename this to "matches"
        }
        else if (question2.length() > 3){
            desiredAnswer2 = createMatchingAnswer(desiredAnswer1, matchingOpposite);
        }

        if (errors.hasErrors() || desiredAnswer1 == null || (question2 != null && (question2.length() > 0 && question2.length() < 3))) { //could just say q2 != null && < 3
            if (errors.hasErrors() || desiredAnswer1 == null){
                System.out.println("made it in to the first");
                model.addAttribute("title", "New Question");
                model.addAttribute("claimedError", "Please Select a Desired Answer");
                model.addAttribute("isError", "Try again!! Please look over possible mistakes in this question.");
                return "test/newQuestion";
            }

            if (errors.hasErrors()) {
                System.out.println("made it in to the third");
                model.addAttribute("title", "New Question");
                model.addAttribute(new Question());
                model.addAttribute("isError", "Try again!! Please look over possible mistakes in this question.");

                return "test/newQuestion";
            }

            if (question2.length()>0 && question2.length() < 3){
                System.out.println("5th!");

                model.addAttribute("title", "New Question");
                model.addAttribute("question2Error", "Please Write a Longer Question (at least 3 characters)");
                model.addAttribute("isError", "Try again!! Please look over possible mistakes in this question.");

                return "test/newQuestion";
            } //Could maybe refactor this to a javascript form
        }

        questionDao.save(question);
        Test currentTest = testDao.findOne(testId);
        Question currentQuestionSet = questionDao.findOne(question.getId());
        currentQuestionSet.setQuestion2(question2);
        currentQuestionSet.setDesiredAnswer2(desiredAnswer2);
        currentQuestionSet.setMatchingOpposite(matchingOpposite);
        questionDao.save(currentQuestionSet);
        currentQuestionSet.setTest(currentTest);

        testDao.save(currentTest);

        List<Question> listOfQuestions = currentTest.getQuestions();
        listOfQuestions.add(question);
        currentTest.setQuestions(listOfQuestions);
        testDao.save(currentTest);

        int possibleConsistencyScore = 0;
        int possiblePersonalityScore = 0;

        for (Question currentQuestionInThisList : listOfQuestions){

            if (currentQuestionInThisList.getDesiredAnswer2() == null){ //checking if the question has a pair
                possiblePersonalityScore += 5;  // if question has no pair, then it's worth only up to 5 personality points
            }
            else{
                possiblePersonalityScore += 10;
                possibleConsistencyScore += 5;
            }
        }

        currentTest.setPossibleConsistencyScore(possibleConsistencyScore);
        currentTest.setPossiblePersonalityScore(possiblePersonalityScore);
        testDao.save(currentTest);


        String stringTestId = Integer.toString(testId);

        return "redirect:/test/newquestion/" + stringTestId;

    }


    @RequestMapping(value = "taketest/{testId}", method = RequestMethod.GET)
    public String displayTakeTest(Model model, @PathVariable int testId, HttpSession session, boolean hasErrors) {


        //TODO: Fix issue where question doesn't display if it's a test with only one question after it was
        //submitted with the "finish test" instead of the "add question" and then "finish test".
        // Also,
        //fix issue where Possible scores get set as NULL if it's only one question

        Test currentTest = testDao.findOne(testId);

        List<Question> questions = currentTest.getQuestions();

        Map<String,Integer> questionMap = new HashMap<>(); //If I use a Question String, then the user cannot have duplicate questions.


//        String[] question1s = new String[questions.size()];
//        String[] question2s = new String[questions.size()];

//        List<String> question1s = new ArrayList<>();
//
//        List<String> question2s = new ArrayList<>();



        // Well I could, I'd just have to make it so it adds an extra space at the end of the user String.



//        for(int i =0; i < questions.size(); i++){
//
//            question1s[i] = questions.get(i).getQuestion1();
//
//            questionMap.put(questions.get(i).getQuestion1(),questions.get(i).getId());
//            if (questions.get(i).getQuestion2() != null) {
//                questionMap.put(questions.get(i).getQuestion2(),questions.get(i).getId());
//                question2s[i] = questions.get(i).getQuestion2();
//
//            }
//            else{question2s[i] = null;}
//
//        }

        for (Question question : questions){
            questionMap.put(question.getQuestion1(),question.getId());
//            question1s.add(question.getQuestion1());
            if (question.getQuestion2() != null){
                questionMap.put(question.getQuestion2(), question.getId());
//                question2s.add(question.getQuestion2());
            }
//            else{
//                question2s.add(null);
//            }
        }

        List<String> list = new ArrayList<>(questionMap.keySet());
        Collections.shuffle(list);

        Map<String, Integer> shuffleMap = new LinkedHashMap<>();
        list.forEach(k->shuffleMap.put(k, questionMap.get(k)));


        model.addAttribute("title", "Take The Test!");
//        model.addAttribute("currentMatchingTestQuestions", questions);
        model.addAttribute("newList", shuffleMap);

        model.addAttribute("test",currentTest);

        model.addAttribute("count", currentTest.getQuestions().size());

        if (hasErrors == true){
            model.addAttribute("error", "Please answer every question");
        }

//        model.addAttribute("question1s",question1s);
//        model.addAttribute("question2s",question2s);


        return "test/takeTest";

    }


    @RequestMapping(value="taketest/{testId}", method=RequestMethod.POST)
    public String processTakeTest(Model model, @PathVariable int testId, HttpSession session, @RequestParam(name="allAnswers") int allAnswers[],
                                  @RequestParam(name="questionIds") int questionIds[], @RequestParam(name="questionKeys") String questionKeys[]){



        User currentUser = (User) session.getAttribute("loggedInUser");
        Test currentTest = testDao.findOne(testId);

        if (allAnswers.length < currentTest.getQuestions().size()){



            return displayTakeTest(model, testId, session, true );
        }

        List<User> testTakers = currentTest.getTestTakers();
        testTakers.add(currentUser);


        List<Integer> answerIntList = Arrays.stream(allAnswers).boxed().collect(Collectors.toList());

        List<Integer> questionIdList  = Arrays.stream(questionIds).boxed().collect(Collectors.toList());

        Map<Question,Answer> answerMap = new HashMap<>();

        Map<Integer, Integer> questionIdIndex = new HashMap<>(); //questionId, index

        Map<Integer,Integer> consistency = new HashMap<>();

        consistency.put(1,5);
        consistency.put(2,4);
        consistency.put(3,3);
        consistency.put(4,2);
        consistency.put(5,1);

        int personalityScore = 0;
        int consistencyScore = 0;

        int returnedScore = 0;


        for (int i = 0; i < answerIntList.size(); i++){

            int currentQuestionId = questionIdList.get(i);
            Question currentQuestion = questionDao.findOne(currentQuestionId);

            String question1 = currentQuestion.getQuestion1();
            String question2 = "";
            String questionKey = questionKeys[i];

            int currentAnswerInt = answerIntList.get(i);
            int firstAnswer;
            int secondAnswer;
            int desiredAnswer1 = currentQuestion.getDesiredAnswer1();
            int desiredAnswer2 = 0;

            Boolean hasMatch = false;
            Boolean doesMatch = null;

            if(currentQuestion.getQuestion2() != null) {
                question2 = currentQuestion.getQuestion2();
                hasMatch = true;
                desiredAnswer2 = currentQuestion.getDesiredAnswer2();

                doesMatch = currentQuestion.getMatchingOpposite();

//                    positionOfDuplicateQuestionId = questionIdList.lastIndexOf(currentQuestionId);
            }

            if (!questionIdIndex.containsKey(currentQuestionId)){

                questionIdIndex.put(currentQuestionId,i);

                Answer answer = new Answer();

                answer.setQuestion(currentQuestion);
                answer.setCurrentTest(currentTest);
                answer.setUser(currentUser);

                if (question1.equals(questionKey)){

                    answer.setAnswer(currentAnswerInt);
                    answerDao.save(answer);
                    answerMap.put(currentQuestion,answer);

                    if (!hasMatch){ personalityScore += checkConsisitencyOrPersonality(currentAnswerInt,desiredAnswer1,true, consistency); }
                }
                else if (question2.equals(questionKey)){

                    answer.setMatchingAnswer(currentAnswerInt);
                    answerDao.save(answer);
                    answerMap.put(currentQuestion,answer);
                }
            }
            else {

                Answer currentAnswer = answerMap.get(currentQuestion);

                if (currentAnswer.getAnswer() == null){

                    System.out.println("First IF");

                    currentAnswer.setAnswer(currentAnswerInt);
                    answerDao.save(currentAnswer);

                    firstAnswer = currentAnswerInt;
                    secondAnswer = currentAnswer.getMatchingAnswer();

                    if (doesMatch){

                        System.out.println("First IF Does Match");

                        returnedScore = checkConsisitencyOrPersonality(firstAnswer, secondAnswer, doesMatch, consistency);
                        personalityScore += checkConsisitencyOrPersonality(firstAnswer,desiredAnswer1,doesMatch, consistency);
                        personalityScore += checkConsisitencyOrPersonality(secondAnswer,desiredAnswer2,doesMatch, consistency);

                        consistencyScore += returnedScore;
                    }

                    else if (!doesMatch){
                        System.out.println("First IF Else");

                        consistencyScore += checkConsisitencyOrPersonality(firstAnswer, secondAnswer, doesMatch, consistency);
                        personalityScore += checkConsisitencyOrPersonality(firstAnswer,desiredAnswer1, true, consistency);
                        personalityScore += checkConsisitencyOrPersonality(secondAnswer,desiredAnswer2, true, consistency);

                    }


                }
                else if (currentAnswer.getMatchingAnswer() == null && hasMatch){

                    System.out.println("Second IF");
                    currentAnswer.setMatchingAnswer(currentAnswerInt);
                    answerDao.save(currentAnswer);


                    firstAnswer = currentAnswerInt;
                    secondAnswer = currentAnswer.getAnswer();

                    if (doesMatch){

                        System.out.println("Second IF Does Match");

                        returnedScore = checkConsisitencyOrPersonality(firstAnswer, secondAnswer, doesMatch, consistency);
                        personalityScore += checkConsisitencyOrPersonality(secondAnswer,desiredAnswer1,doesMatch, consistency);
                        personalityScore += checkConsisitencyOrPersonality(firstAnswer,desiredAnswer2,doesMatch, consistency);

                        consistencyScore += returnedScore;
                    }

                    else if (!doesMatch){

                        System.out.println("Second IF Does Not Match");

                        consistencyScore += checkConsisitencyOrPersonality(firstAnswer, secondAnswer, doesMatch, consistency);
                        personalityScore += checkConsisitencyOrPersonality(secondAnswer,desiredAnswer1, true, consistency);
                        personalityScore += checkConsisitencyOrPersonality(firstAnswer,desiredAnswer2, true, consistency);

                    }

                }

//                else if (currentAnswer.getMatchingAnswer() == null && !hasMatch){
//
//                    System.out.println("Second IF HAS NO MATCH");
//
//                    personalityScore += checkConsisitencyOrPersonality(currentAnswerInt,desiredAnswer1,true);
//
//                }
            }



//            if (!answerMap.containsKey(currentQuestion)){
//
//                String question1 = currentQuestion.getQuestion1();
//                String question2 = "";
//                String questionKey = questionKeys[i];
//                int currentAnswerInt = answerIntList.get(i);
//                int positionOfDuplicateQuestionId = -1;
//                Boolean hasMatch = false;
//
//                if(questionIdIndex.containsKey(currentQuestionId)){
//                    positionOfDuplicateQuestionId = i;
//                }
//                else {
//                    questionIdIndex.put(questionIdList.get(i), i);
//                }
//
//
//
//                if(currentQuestion.getQuestion2() != null) {
//                    question2 = currentQuestion.getQuestion2();
//                    hasMatch = true;
////                    positionOfDuplicateQuestionId = questionIdList.lastIndexOf(currentQuestionId);
//
////                    for (int j = i + 1; i < questionIdList.size(); i++){
////                        if (questionIdList.get(j) == currentQuestionId ){
////                            //This is probably faster than lastIndexOf
////                        }
////                    }
//                }
//
//                Answer answer = new Answer();
//
//                answer.setQuestion(currentQuestion);
//                answer.setCurrentTest(currentTest);
//                answer.setUser(currentUser);
//
//                if (question1.equals(questionKey)){
//                    answer.setAnswer(currentAnswerInt);
//
//                    if (hasMatch){
//                        answer.setMatchingAnswer(answerIntList.get(positionOfDuplicateQuestionId));
//                    }
//
//                    answerMap.put(currentQuestion,answer);
//                    answerDao.save(answer);
//                }
//                else if (question2.equals(questionKey)){
//
//                    answer.setMatchingAnswer(currentAnswerInt);
//
//                    if (hasMatch){ answer.setAnswer(answerIntList.get(positionOfDuplicateQuestionId));}
//
//                    answerMap.put(currentQuestion,answer);
//                    answerDao.save(answer);
//
//                }
//            }
        }
        //

        testDao.save(currentTest);
        currentUser.setAnswers(answerMap);

        Score score = new Score();

        score.setConsistencyScore(consistencyScore);
        score.setPersonalityScore(personalityScore);


        score.setTest(currentTest);
        score.setUser(currentUser);

        scoreDao.save(score);


        Map<Test,Score> userScore = new HashMap<>();

        userScore.put(currentTest,score);

        currentUser.setScores(userScore);

        userDao.save(currentUser);



        //processScore(currentUser, currentTest, answerMap);

        return "redirect:/home";
    }

//    public int processScore(User currentUser, Test currentTest, Map<Question,Answer> answerMap) {
//
//
//
//
//
//       // User currentUser = userDao.findOne(user.getId());
//      //  Test currentTest = testDao.findOne(test.getId());
//        int currentTestId = currentTest.getId();
//
//
//        int personalityScore = 0;
//        int consistencyScore = 0; // why do these get updated in the for loop but not the one's below?
//
//
//        int possiblePersonalityScore = 0; //I can refactor this by doing some math first especially with knowledge that Intellij makes false suggestions at times.
//        int possibleConsistencyScore = 0;
//
//
//        Map<Question, Answer> userAnswers = currentUser.getAnswers(); // i dont know how this only gets the user answers to this test but if it works it works
//
//
//
//        int totalNumberOfQuestions = currentTest.getQuestions().size();
//
//
//        int eachQuestionIsWorth = 5; //might let user pick this.
//
//
//
//
//
//        for (Map.Entry<Question, Answer> entry : userAnswers.entrySet()) {
//
//            if (currentTestId != entry.getValue().getCurrentTest().getId()) {
//                System.out.println("Checking if Question is for This Test!");
//                continue;
//            }
//
//
//            Answer currentAnswer = entry.getValue();
//            Question currentQuestion = currentAnswer.getQuestion();
//
//            Boolean questionsMatch = currentQuestion.getMatchingOpposite();
//            Boolean hasMatch = false;
//
//
//            Integer currentDesiredAnswer1 = currentQuestion.getDesiredAnswer1();
//            Integer currentDesiredAnswer2 = currentQuestion.getDesiredAnswer2(); //might use these variables for readablility
//
//            if (currentAnswer.getMatchingAnswer() != null) {
//                System.out.println("If current answer has a match");
//                hasMatch = true;
//            }
//
//            System.out.println("Number of Questions is " + totalNumberOfQuestions);
//
//
//            System.out.println("possible score of " + possiblePersonalityScore);
//
//
//            System.out.println("Each question is Worth " + eachQuestionIsWorth);
//
//            personalityScore += checkConsisitencyOrPersonality(currentDesiredAnswer1,currentAnswer.getAnswer(),true); //when it comes to personality, match is always true.
//            possiblePersonalityScore+=eachQuestionIsWorth;
//
//            if (hasMatch) {
//
//                personalityScore += checkConsisitencyOrPersonality(currentQuestion.getDesiredAnswer2(),currentAnswer.getMatchingAnswer(),true);
//                consistencyScore += checkConsisitencyOrPersonality(currentAnswer.getAnswer(),currentAnswer.getMatchingAnswer(),questionsMatch); //might need to pass in what each question is worth
//                possibleConsistencyScore += eachQuestionIsWorth;
//                possiblePersonalityScore+=eachQuestionIsWorth;
//
//                System.out.println("Current Consistency Score is: " + consistencyScore + " out of " + possibleConsistencyScore);
//
//
//            }
//
//        }
//
//            currentTest.setPossibleConsistencyScore(possibleConsistencyScore);
//            currentTest.setPossiblePersonalityScore(possiblePersonalityScore);
//
//            testDao.save(currentTest);
//
//            Score finalScore = new Score();
//            finalScore.setConsistencyScore(consistencyScore);
//            finalScore.setPersonalityScore(personalityScore);
//            finalScore.setUser(currentUser);
//
//
//            Map<Test,Score> scores = new HashMap<>();
//
//
//
//            scores.put(currentTest,finalScore);
//            scoreDao.save(finalScore);
//
//            currentUser.setScores(scores);
//
//            //scoreDao.save(finalScore);
//            userDao.save(currentUser);
//
//
//            return 0;
//
//
//        }

        @RequestMapping(value="/manage", method = RequestMethod.GET)
        public String displayCreatedTests(Model model, HttpSession session){

            User currentUser = (User) session.getAttribute("loggedInUser");

            Iterable<Test> allTests = testDao.findAll();

            List<Test> allTestsList = toList(allTests);

            List<Test> myTests = new ArrayList<>();




            for(Test test : allTestsList){
                if (test.getTestCreator().getId() == currentUser.getId()){
                    myTests.add(test);
                }
            }

            model.addAttribute("tests",myTests);
            model.addAttribute("user", currentUser);

            return "test/myTests";
        }




        @RequestMapping(value = "/manage/{testId}", method = RequestMethod.GET)
        public String manageEmployees(Model model, HttpSession session, @PathVariable int testId){

            User currentUser = (User) session.getAttribute("loggedInUser");
            Test currentTest = testDao.findOne(testId);


            List<User> testTakers = currentTest.getTestTakers();

            Map<Test,Score> currentTestScores = new HashMap<>();
            Map<User, Score> userScores = new HashMap<>();




            // Since it's a HASHMAP, shouldn't there not be duplicate keys?


            for (User testTaker : testTakers){
                for(Score score : testTaker.getScores().values()){
                    userScores.put(testTaker,score);
                }
            }


            model.addAttribute("title","Manage Test Takers!");
            model.addAttribute("testTakers",testTakers);
            model.addAttribute("scores",userScores);
            model.addAttribute("test",currentTest);

            return "test/manage";

        }


        @RequestMapping(value= "/manage/{testId}/{testTakerId}", method = RequestMethod.GET)
        public String displayTestResult(Model model, @PathVariable int testId, @PathVariable int testTakerId, HttpSession session){

            Test currentTest = testDao.findOne(testId);
            User testTaker = userDao.findOne(testTakerId);



            Map<Question,Answer> userAnswers = testTaker.getAnswers();

            List<Answer> userCurrentTestAnswers = new ArrayList<>();

            for(Map.Entry<Question,Answer> answer : userAnswers.entrySet()) {
                if (answer.getKey().getTest() == currentTest) {
                    userCurrentTestAnswers.add(answer.getValue());
                }
            }

            model.addAttribute("userAnswers",userCurrentTestAnswers);
            model.addAttribute("user", testTaker);

            return "test/viewTestResult";

        }

        public static <E> List<E> toList(Iterable<E> iterable) {
            if(iterable instanceof List) {
                return (List<E>) iterable;
            }
            ArrayList<E> list = new ArrayList<E>();
            if(iterable != null) {
                for(E e: iterable) {
                    list.add(e);
                }
            }
            return list;

        }

        public int createMatchingAnswer(int answer, Boolean doesMatch){ //this function could be done in JavaScript?

            int answer2;

            if (doesMatch){
                answer2 = answer;
                return answer2;
            }

            Map<Integer,Integer> scores = new HashMap<>();

            scores.put(1,5);
            scores.put(2,4);
            scores.put(3,3);

            for (Map.Entry<Integer,Integer> set : scores.entrySet()){
                if (answer == set.getKey()){
                    answer2 = set.getValue();
                    return answer2;
                }

                if (answer == set.getValue()) {

                    answer2 = set.getKey();
                    return answer2;
                }

            }

                return 0;
        }

        public int checkConsisitencyOrPersonality(int answer1, int answer2, Boolean doesMatch, Map<Integer,Integer> consistency){




            int score = 0;

            if (doesMatch){ //This checks for consistency when doesMatch, also checks for personality score as the process follows the same logic.
                if (Math.abs(answer1-answer2) == 0){
                    score+=5;
                    return score;
                }
                else if (Math.abs(answer1-answer2) == 1){
                    score+=3;
                    return score;
                }
                else if (Math.abs(answer1-answer2) == 2){
                    score+=1;
                    return score;

                }
                else {return score;}
            }

            else{

//                Map<Integer,Integer> consistency = new HashMap<>();
//
//                consistency.put(1,5);
//                consistency.put(2,4);
//                consistency.put(3,3);// this map is being created twice. Should refactor map into separate function to be only used once?
//                consistency.put(4,2);
//                consistency.put(5,1);

                int distanceBetweenAnswers = Math.abs(answer1-answer2);

                int whatAnswerTwoShouldBe = consistency.get(answer1);

                int maxDistance = Math.abs(whatAnswerTwoShouldBe - answer1);

                if (Math.abs(distanceBetweenAnswers - maxDistance) == 0){
                    score += 5;
                    return score;
                }
                else if (Math.abs(distanceBetweenAnswers - maxDistance) == 1){
                    score += 3;
                    return score;
                }
                else if (Math.abs(distanceBetweenAnswers - maxDistance) == 2){
                    score += 1;
                    return score;
                }

//                for (Map.Entry<Integer,Integer> pair : consistency.entrySet()){
//                    Integer key = pair.getKey();
//                    Integer val = pair.getValue();
//
//                    int maxDifference = Math.abs(key - val);
//
//                    if (answer1 == key || answer1 == val || answer2 == key || answer2 == val) {
//
//                        if (Math.abs(distanceBetweenAnswers - maxDifference) == 0) {
//                            score += 5;
//                            return score;
//                        }
//                        if (Math.abs(distanceBetweenAnswers - maxDifference) == 1) {
//                            score += 3;
//                            return score;
//                        }
//                        if (Math.abs(distanceBetweenAnswers - maxDifference) == 2) {
//                            score += 1;
//                            return score;
//                        }
//
//                    }
//
//
//                }


//                consistency.put(4,2);
//                consistency.put(5,1);
//
//                if (Math.abs(consistency.get(answer1) - answer1) == 0){
//                    score+=5;
//                    return score;
//                }
//                else if(Math.abs(consistency.get(answer1) - answer1) == 1) {
//                    score += 3;
//                    return score;
//                }
//                else if(Math.abs(consistency.get(answer1) - answer1) == 2) {
//                    score += 1;
//                    return score;
//                }

            }
            return score;
        }

}
