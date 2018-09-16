package com.example.scrennerMVC.controllers;

import com.example.scrennerMVC.models.*;
import com.example.scrennerMVC.models.data.*;
import org.hibernate.annotations.MapKeyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.MapKeyColumn;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;


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
            if  (!test.getTestTakers().contains(userDao.findOne(currentUser.getId()))){
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

        List<Question> listOfQuestions = currentTest.getQuestions();
        listOfQuestions.add(question);
        currentTest.setQuestions(listOfQuestions);

        testDao.save(currentTest);


        String stringTestId = Integer.toString(testId);

        return "redirect:/test/newquestion/" + stringTestId;

    }


    @RequestMapping(value = "taketest/{testId}", method = RequestMethod.GET)
    public String displayTakeTest(Model model, @PathVariable int testId, HttpSession session) {

        Test currentTest = testDao.findOne(testId);

        List<Question> questions = currentTest.getQuestions();

        Map<String,Integer> questionMap = new HashMap<>(); //If I use a Question String, then the user cannot have duplicate questions.
                                                            // Well I could, I'd just have to make it so it adds an extra space at the end of the user String.

        for(Question question : questions){
            questionMap.put(question.getQuestion1(),question.getId());
            if (question.getQuestion2() != null){ questionMap.put(question.getQuestion2(), question.getId());}
        }

        List<String> list = new ArrayList<>(questionMap.keySet());
        Collections.shuffle(list);

        Map<String, Integer> shuffleMap = new LinkedHashMap<>();
        list.forEach(k->shuffleMap.put(k, questionMap.get(k)));


        model.addAttribute("title", "Take The Test!");
//        model.addAttribute("currentMatchingTestQuestions", questions);
        model.addAttribute("newList", shuffleMap);

        model.addAttribute("test",currentTest);

        return "test/takeTest";

    }


    @RequestMapping(value="taketest/{testId}", method=RequestMethod.POST)
    public String processTakeTest(Model model, @PathVariable int testId, HttpSession session, @RequestParam(name="allAnswers") String allAnswers[], @RequestParam(name="questionIds") String questionIds[]){

        User currentUser = (User) session.getAttribute("loggedInUser");
        Test currentTest = testDao.findOne(testId);

        int aPosition = 0;

        //This whole damn function could use some refactoring.
            eachAnswer:
            for (String answer : allAnswers) {

                Answer currentAnswer = new Answer();
                int currentAnswerInt = Integer.parseInt(answer); //answer is a string of a number and here it converts to int

                String currentQuestionId = questionIds[aPosition]; //finds the first questionId by the current position
                int currentQuestionIdInt = Integer.parseInt(currentQuestionId); //converts the questionId to an int

                Question currentQuestion = questionDao.findOne(currentQuestionIdInt); //assign "currentQuestion" by the currentQuestionId in the list

                currentAnswer.setUser(currentUser);
                currentAnswer.setQuestion(currentQuestion);
                currentAnswer.setCurrentTest(currentTest);
                currentAnswer.setAnswer(currentAnswerInt); //sets currentAnswer with all CURRENTLY KNOWN attributes
                currentAnswer.setMatchingAnswer(null);

                Map<Integer, Integer> positionOfMatchingQuestionIds = new HashMap<Integer, Integer>();

                mapper:
                for (int i = 0; i < questionIds.length; i++){
                    if (questionDao.findOne(Integer.parseInt(questionIds[i])).getMatchingOpposite() == null){
                      //  numberOfNonMatchingQuestions +=1;
                        continue mapper;
                    }
                    positionOfMatchingQuestionIds.put(Integer.parseInt(questionIds[i]), i);
                }

                label:
                for (Map.Entry<Integer,Integer> entry : positionOfMatchingQuestionIds.entrySet()){
                    Integer matchingQuestionId = entry.getKey();
                    Integer value = entry.getValue(); //position

                    if (matchingQuestionId == currentQuestionIdInt){
                        currentAnswer.setMatchingAnswer(Integer.parseInt(allAnswers[value]));
                        if ((questionIds.length - (questionIds.length/2) == aPosition)){ //Not sure why but this is the only way I can map answers into the database without duplicating non matching answers.
                            break eachAnswer;
                        }

                        break label;
                    }
                 }

                aPosition += 1;

                answerDao.save(currentAnswer);
                userDao.save(currentUser);

            }

            //Pass in number of Questions as questionsIds.length?

            List<User> currentTestTakers = currentTest.getTestTakers();
            currentTestTakers.add(currentUser);

            currentTest.setTestTakers(currentTestTakers);

            testDao.save(currentTest);
            userDao.save(currentUser);

            processScore(currentUser, currentTest);

            return "redirect:/home";
    }

    public int processScore(User user, Test test) {

        User currentUser = userDao.findOne(user.getId());
        Test currentTest = testDao.findOne(test.getId());
        int currentTestId = currentTest.getId();


        int personalityScore = 0;
        int consistencyScore = 0; // why do these get updated in the for loop but not the one's below?


        int possiblePersonalityScore = 0; //I can refactor this by doing some math first especially with knowledge that Intellij makes false suggestions at times.
        int possibleConsistencyScore = 0;


        Map<Question, Answer> userAnswers = currentUser.getAnswers(); // i dont know how this only gets the user answers to this test but if it works it works



        int totalNumberOfQuestions = currentTest.getQuestions().size();


        int eachQuestionIsWorth = 5; //might let user pick this.


        for (Map.Entry<Question, Answer> entry : userAnswers.entrySet()) {

            if (currentTestId != entry.getValue().getCurrentTest().getId()) {
                System.out.println("Checking if Question is for This Test!");
                continue;
            }


            Answer currentAnswer = entry.getValue();
            Question currentQuestion = currentAnswer.getQuestion();

            Boolean questionsMatch = currentQuestion.getMatchingOpposite();
            Boolean hasMatch = false;


            Integer currentDesiredAnswer1 = currentQuestion.getDesiredAnswer1();
            Integer currentDesiredAnswer2 = currentQuestion.getDesiredAnswer2(); //might use these variables for readablility

            if (currentAnswer.getMatchingAnswer() != null) {
                System.out.println("If current answer has a match");
                hasMatch = true;
            }

            System.out.println("Number of Questions is " + totalNumberOfQuestions);


            System.out.println("possible score of " + possiblePersonalityScore);


            System.out.println("Each question is Worth " + eachQuestionIsWorth);

            personalityScore += checkConsisitencyOrPersonality(currentDesiredAnswer1,currentAnswer.getAnswer(),true); //when it comes to personality, match is always true.
            possiblePersonalityScore+=eachQuestionIsWorth;

            if (hasMatch) {

                personalityScore += checkConsisitencyOrPersonality(currentQuestion.getDesiredAnswer2(),currentAnswer.getMatchingAnswer(),true);
                consistencyScore += checkConsisitencyOrPersonality(currentAnswer.getAnswer(),currentAnswer.getMatchingAnswer(),questionsMatch); //might need to pass in what each question is worth
                possibleConsistencyScore += eachQuestionIsWorth;
                possiblePersonalityScore+=eachQuestionIsWorth;

                System.out.println("Current Consistency Score is: " + consistencyScore + " out of " + possibleConsistencyScore);


            }

        }

            currentTest.setPossibleConsistencyScore(possibleConsistencyScore);
            currentTest.setPossiblePersonalityScore(possiblePersonalityScore);

            testDao.save(currentTest);

            Score finalScore = new Score();
            finalScore.setConsistencyScore(consistencyScore);
            finalScore.setPersonalityScore(personalityScore);
            finalScore.setUser(currentUser);


            Map<Test,Score> scores = new HashMap<>();



            scores.put(currentTest,finalScore);
            scoreDao.save(finalScore);

            currentUser.setScores(scores);

            //scoreDao.save(finalScore);
            userDao.save(currentUser);


            return 0;


        }

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


            //TODO: Fix issue where it shuffles previous scores instead of simply displaying the most recent score by user.
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

        public int checkConsisitencyOrPersonality(int answer1, int answer2, Boolean doesMatch){




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

                Map<Integer,Integer> consistency = new HashMap<>();

                consistency.put(1,5);
                consistency.put(2,4);
                consistency.put(3,3);// this map is being created twice. Should refactor map into separate function to be only used once?


                int distanceBetweenAnswers = Math.abs(answer1-answer2);


                for (Map.Entry<Integer,Integer> pair : consistency.entrySet()){
                    Integer key = pair.getKey();
                    Integer val = pair.getValue();

                    int maxDifference = Math.abs(key - val);

                    if (answer1 == key || answer1 == val || answer2 == key || answer2 == val) {

                        if (Math.abs(distanceBetweenAnswers - maxDifference) == 0) {
                            score += 5;
                            return score;
                        }
                        if (Math.abs(distanceBetweenAnswers - maxDifference) == 1) {
                            score += 3;
                            return score;
                        }
                        if (Math.abs(distanceBetweenAnswers - maxDifference) == 2) {
                            score += 1;
                            return score;
                        }

                    }


                }


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
