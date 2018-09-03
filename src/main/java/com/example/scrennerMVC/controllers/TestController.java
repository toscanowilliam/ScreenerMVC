package com.example.scrennerMVC.controllers;

import com.example.scrennerMVC.models.Answer;
import com.example.scrennerMVC.models.Question;
import com.example.scrennerMVC.models.Test;
import com.example.scrennerMVC.models.User;
import com.example.scrennerMVC.models.data.AnswerDao;
import com.example.scrennerMVC.models.data.QuestionDao;
import com.example.scrennerMVC.models.data.TestDao;
import com.example.scrennerMVC.models.data.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.Errors;

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

    @RequestMapping(value="", method = RequestMethod.GET)
    public String displayTests(Model model){

        model.addAttribute("title", "Available Tests!");
        model.addAttribute("tests",testDao.findAll());
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

        if (errors.hasErrors() || desiredAnswer1 == null || question2 == null || desiredAnswer2 == null || desiredAnswer2 != null ) {
            if (errors.hasErrors() || desiredAnswer1 == null){
                System.out.println("made it in to the first");
                model.addAttribute("title", "New Question");
                model.addAttribute("claimedError", "Please Select a Desired Answer");
                model.addAttribute("isError", "Try again!! Please look over possible mistakes in this question.");
                return "test/newQuestion";
            }

            if (question2.length() >= 3 && desiredAnswer2 == null){
                model.addAttribute("desiredAnswer2Error", "Please Select a Desired Answer");
                System.out.println("made it in to the second");
                model.addAttribute("title", "New Question");
                model.addAttribute(new Question());
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

            if ((question2.length() > 0 && question2.length() < 3) && desiredAnswer2 == null){
                System.out.println("4th!");
                model.addAttribute("title", "New Question");
                model.addAttribute("desiredAnswer2Error", "Please Select a Second Desired Answer");
                model.addAttribute("question2Error", "Please Write a Longer Question (at least 3 characters)");
                model.addAttribute("isError", "Try again!! Please look over possible mistakes in this question.");

                return "test/newQuestion";
            }

            if ((question2.length()>0 && question2.length() < 3) && desiredAnswer2 != null){
                System.out.println("5th!");

                model.addAttribute("title", "New Question");
                model.addAttribute("question2Error", "Please Write a Longer Question (at least 3 characters)");
                model.addAttribute("isError", "Try again!! Please look over possible mistakes in this question.");

                return "test/newQuestion";
            }
        }

        System.out.println("made it out?");

        if (question2 == "" && desiredAnswer2 == null){
            question2 = null;
            desiredAnswer2 = null;
            matchingOpposite = null;
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


        User currentUser = (User) session.getAttribute("loggedInUser");
        Test currentTest = testDao.findOne(testId);

        List<Question> currentMatchingTestQuestions1 = currentTest.getQuestions();

        int totalElements = currentMatchingTestQuestions1.size();
        // initialize random number generator
        Random random = new Random();
        for (int loopCounter = 0; loopCounter < totalElements; loopCounter++) {
            // get the list element at current index
            Question currentElement = currentMatchingTestQuestions1.get(loopCounter);
            // generate a random index within the range of list size
            int randomIndex = loopCounter + random.nextInt(totalElements - loopCounter);
            // set the element at current index with the element at random
            // generated index
            currentMatchingTestQuestions1.set(loopCounter, currentMatchingTestQuestions1.get(randomIndex));
            // set the element at random index with the element at current loop
            // index
            currentMatchingTestQuestions1.set(randomIndex, currentElement);
        }

        List<Question> newList = new ArrayList<>(currentMatchingTestQuestions1);

        Collections.shuffle(newList);

        model.addAttribute("title", "Take The Test!");
        model.addAttribute("currentMatchingTestQuestions", currentMatchingTestQuestions1);
        model.addAttribute("newList", newList);

        model.addAttribute("test",currentTest);

        return "test/takeTest";

    }


    @RequestMapping(value="taketest/{testId}", method=RequestMethod.POST)
    public String processTakeTest(Model model, @PathVariable int testId, HttpSession session, @RequestParam(name="allAnswers") String allAnswers[], @RequestParam(name="questionIds") String questionIds[]){

        User currentUser = (User) session.getAttribute("loggedInUser");
        Test currentTest = testDao.findOne(testId);

        int aPosition = 0;

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

//        Map<Integer, Integer> desiredAnswers1 = new HashMap<>();
//        Map<Integer, Integer> desiredAnswers2 = new HashMap<>();

        //Will soon refactor us of getters with variable names to make IF statements look cleaner.


        int numberOfQuestionsWithMatch = 0;
        int numberOfQuestionsWithoutMatch = 0;
        //int numberOfQuestionSets = currentTest.getQuestions().size();
        int totalNumberOfQuestions = numberOfQuestionsWithMatch + numberOfQuestionsWithoutMatch;


        int eachQuestionIsWorth = 5;


        for (Map.Entry<Question, Answer> entry : userAnswers.entrySet()) {

            if (currentTestId != entry.getValue().getCurrentTest().getId()) {
                System.out.println("Checking if Question is for This Test!");
                continue;
            }


            Answer currentAnswer = entry.getValue();
            Question currentQuestion = currentAnswer.getQuestion();

            Boolean questionsMatch = currentQuestion.getMatchingOpposite();
            Boolean hasMatch = false;
            Boolean consistencyFound = false;


            Integer currentDesiredAnswer1 = currentQuestion.getDesiredAnswer1();
            Integer currentDesiredAnswer2 = currentQuestion.getDesiredAnswer2();

            if (currentAnswer.getMatchingAnswer() != null) {
                System.out.println("If current answer has a match");
                hasMatch = true;
                //     numberOfQuestionsWithMatch += 1;
            }

            System.out.println("Number of Questions is " + totalNumberOfQuestions);


            System.out.println("possible score of " + possiblePersonalityScore);


            System.out.println("Each question is Worth " + eachQuestionIsWorth);

            if (currentDesiredAnswer1.equals(currentAnswer.getAnswer())) {
                personalityScore += eachQuestionIsWorth;
                possiblePersonalityScore += eachQuestionIsWorth;
                System.out.println("Correct! 1");
                System.out.println("Consistency score of " + consistencyScore + " out of " + possibleConsistencyScore);
                System.out.println("Personality Score of " + personalityScore + " out of " + possiblePersonalityScore);

            } else {
                possiblePersonalityScore += eachQuestionIsWorth;
            }

            if (hasMatch) {


                if (currentQuestion.getDesiredAnswer2().equals(currentAnswer.getMatchingAnswer())) { //possible null
                    System.out.println("Correct! 2");

                    personalityScore += eachQuestionIsWorth;
                    possiblePersonalityScore += eachQuestionIsWorth;

                    System.out.println("Consistency score of " + consistencyScore + " out of " + possibleConsistencyScore);
                    System.out.println("Personality Score of " + personalityScore + " out of " + possiblePersonalityScore);
                } else {
                    possiblePersonalityScore += eachQuestionIsWorth;
                }

                System.out.println("Now checking for Consistency!");

                if (questionsMatch && currentAnswer.getAnswer().equals(currentAnswer.getMatchingAnswer())) { //possible null
                    consistencyScore += eachQuestionIsWorth;
                    possibleConsistencyScore += eachQuestionIsWorth;

                    System.out.println("Is Consistent! 1");
                    System.out.println("Consistency score of " + consistencyScore + " out of " + possibleConsistencyScore);
                    System.out.println("Personality Score of " + personalityScore + " out of " + possiblePersonalityScore);


                } else if (questionsMatch && !currentAnswer.getAnswer().equals(currentAnswer.getMatchingAnswer())) {
                    possibleConsistencyScore += eachQuestionIsWorth;
                    System.out.println("Else NOT consistent! 1");
                    System.out.println("Consistency score of " + consistencyScore + " out of " + possibleConsistencyScore);
                    System.out.println("Personality Score of " + personalityScore + " out of " + possiblePersonalityScore);
                }


                System.out.println("Now checking for furthur Consistency!");


                if (!questionsMatch) {


                    if (currentAnswer.getAnswer() == 1 && currentAnswer.getMatchingAnswer() == 5) {
                        consistencyScore += eachQuestionIsWorth;
                        possibleConsistencyScore += eachQuestionIsWorth;
                        consistencyFound = true;
                        System.out.println("Is Consistent! 2");
                    }

                    if (currentAnswer.getAnswer() == 2 && currentAnswer.getMatchingAnswer() == 4) {
                        consistencyScore += eachQuestionIsWorth;
                        possibleConsistencyScore += eachQuestionIsWorth;
                        consistencyFound = true;
                        System.out.println("Is Consistent! 2");
                    }

                    if (currentAnswer.getAnswer() == 3 && currentAnswer.getMatchingAnswer() == 3) {
                        consistencyScore += eachQuestionIsWorth;
                        possibleConsistencyScore += eachQuestionIsWorth;
                        consistencyFound = true;
                        System.out.println("Is Consistent! 2");
                    }

                    if (currentAnswer.getAnswer() == 4 && currentAnswer.getMatchingAnswer() == 2) {
                        consistencyScore += eachQuestionIsWorth;
                        possibleConsistencyScore += eachQuestionIsWorth;
                        consistencyFound = true;
                        System.out.println("Is Consistent! 2");
                    }

                    if (currentAnswer.getAnswer() == 5 && currentAnswer.getMatchingAnswer() == 1) {
                        consistencyScore += eachQuestionIsWorth;
                        possibleConsistencyScore += eachQuestionIsWorth;
                        consistencyFound = true;
                        System.out.println("Is Consistent! 2");
                    }

                    if (!consistencyFound) {
                        possibleConsistencyScore += eachQuestionIsWorth;
                        System.out.println("ELSE Consistent! 2 FALSE" );
                        System.out.println("Final Score:");
                        System.out.println("Consistency score of " + consistencyScore + " Out of " + possibleConsistencyScore);
                        System.out.println("Personality Score of " + personalityScore + " out of " + possiblePersonalityScore);



                    } else if (consistencyFound) { //when intelliJ says it's always true or false for a Boolean, just ignore it.
                        System.out.println("ELSE Consistent! 2 TRUE");
                        System.out.println("Final Score:");
                        System.out.println("Consistency score of " + consistencyScore + " Out of " + possibleConsistencyScore);
                        System.out.println("Personality Score of " + personalityScore + " out of " + possiblePersonalityScore);

                    }
                }


            }

        }

            Map<Test,Integer> finalPersonalityScore = new HashMap<>();
            Map<Test,Integer> finalConsitencyScore = new HashMap<>();

            finalPersonalityScore.put(currentTest,personalityScore);
            finalConsitencyScore.put(currentTest,consistencyScore);

            currentUser.setPersonalityScores(finalPersonalityScore);
            currentUser.setConsistencyScores(finalConsitencyScore);

            userDao.save(currentUser);



            return consistencyScore + personalityScore;


        }






}
