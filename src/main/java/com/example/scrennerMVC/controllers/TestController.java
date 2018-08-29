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


        //Boolean hasMatch = true;

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
//
//
//        User currentUser = (User) session.getAttribute("loggedInUser");
//
//        Answer anAnswer = new Answer();
//
//        int aNumber = 1;
//
//        anAnswer.setAnswer(aNumber);
//        anAnswer.setMatchingAnswer(aNumber);
//        anAnswer.setQuestion(currentQuestionSet);
//        anAnswer.setUser(currentUser);
//
//        HashMap<Question, Answer> answerMap = new HashMap<>();
//
//        answerMap.put(currentQuestionSet, anAnswer);
//
//        answerDao.save(anAnswer);
//
//        userDao.save(currentUser);

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
      //  List<Question> currentTestQuestions = currentTest.getQuestions();

        Boolean foundMatch = false;


       // int arraySizeOfAllAnswers = allAnswers.length;

        int aPosition = 0;

//        List<Integer> listOfNonDuplicates = new ArrayList<>();





//        for(int i =0; i<questionIds.length; i++){
//            int currentQuestionId = Integer.parseInt(questionIds[i]);
//            for(int j = i +1; j < questionIds.length; i++){
//                if (currentQuestionId != Integer.parseInt(questionIds[j])){
//                    continue;
//                }
//                if (){
//
//                }
//            }
//        }
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

//                List<Integer> list = new ArrayList<>();
//                Integer num = new Integer(Arrays.asList(questionIds).indexOf(currentQuestionId));

                Map<Integer, Integer> positionOfMatchingQuestionIds = new HashMap<Integer, Integer>();

                //int numberOfNonMatchingQuestions = 0;

                mapper:
                for (int i = 0; i < questionIds.length; i++){

                    //Question checkQuestion = questionDao.findOne(i);
                    if (questionDao.findOne(Integer.parseInt(questionIds[i])).getMatchingOpposite() == null){
                      //  numberOfNonMatchingQuestions +=1;
                        continue mapper;
                    }
                    positionOfMatchingQuestionIds.put(Integer.parseInt(questionIds[i]), i);
                }

                label:
                for (Map.Entry<Integer,Integer> entry : positionOfMatchingQuestionIds.entrySet()){
                    Integer matchingQuestionId = entry.getKey();
                    Integer value = entry.getValue();

                    if (matchingQuestionId == currentQuestionIdInt){
                        currentAnswer.setMatchingAnswer(Integer.parseInt(allAnswers[value]));
                        if ((questionIds.length - (questionIds.length/2) == aPosition)){
                            break eachAnswer;
                        }

                        break label;
                    }
                 }



               // System.out.print(positionOfMatchingQuestionIds);



//                for (int i = 0; i < questionIds.length; i++){
//                    if (currentQuestionIdInt == Integer.parseInt(questionIds[i])){
//                        currentAnswer.setMatchingAnswer(Integer.parseInt(allAnswers[i]));
//                        foundMatch = true;
//                        break;
//                    }
//                }
//                if (foundMatch = false){currentAnswer.setMatchingAnswer(null);}
//                foundMatch = false;

                aPosition += 1;


//                Map<Question, Answer> answerMap = new HashMap<>();
//                answerMap.put(currentQuestion,currentAnswer);

//                currentUser.setAnswers(answerMap);

                answerDao.save(currentAnswer);
                userDao.save(currentUser);

            }

//                if (arraySizeOfAllAnswers % 2 == 0) {
//
//
//                    if (aPosition < (arraySizeOfAllAnswers +1)) { //can be refactored to top //this was written assuming every question was going to have a matching question.
//
//
//                        System.out.println("yay!");
//                        for (int i = 0; i < questionIds.length; i++) { // i is the position in questionIds after halfway
//                            System.out.println("yay! for loop!");
//
//                            if (Integer.parseInt(questionIds[i]) == Integer.parseInt(currentQuestionId)) { //if the current position in questionIDs is equal to the currentQuestionID
//                                System.out.println("yay! for loop! and second If statement!");
//
//                                String matchingAnswer = allAnswers[i];
//                                int matchingAnswerInt = Integer.parseInt(matchingAnswer);
//
//                                currentAnswer.setMatchingAnswer(matchingAnswerInt);
//                            }
//                        }
//                    }
//                    else    { return "redirect:/home"; }
//
//                    Map<Question, Answer> answerMap = new HashMap<>();
//                    currentUser.setAnswers(answerMap);
//
//                    answerDao.save(currentAnswer);
//                    userDao.save(currentUser);
//
//                    aPosition += 1;
//                    }
//                }

            processScore(allAnswers, questionIds);

            return "redirect:/home";
    }

    public static boolean moreThanOnce(ArrayList<Integer> list, int searched)
    {
        int numCount = 0;

        for (int thisNum : list) {
            if (thisNum == searched) numCount++;
        }

        return numCount > 1;
    }



    public int processScore(String allAnswers[], String questionIds[]){

        //Answer currentAnswer = answerDao.findOne(answerId);

        /*

            The second half of allAnswers are question2s, the second half of questionIds are for question 2s.
            The point is to find the answers that match by checking which has the same questionId by looking at positions.
            Example: Answer in allAnswers[0] has the questionId of questionsIds[0]. To find the matching answer, we
                     we find the position in the second half of questionIds that has the same questionId.

                     When we get the position of the duplicate questionId, we use that same position number to get the
                     answer in allAnswers.

         */
        int score = 0;


        return score;



        }




}
