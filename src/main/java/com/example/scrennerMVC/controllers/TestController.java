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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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



    @RequestMapping(value= "/newtest", method=RequestMethod.GET)
    public String displayAddTest(Model model){

        model.addAttribute("title", "New Test");
        model.addAttribute(new Test());

        return "test/newTest";
    }

    @RequestMapping(value="/newtest", method=RequestMethod.POST)
    public String processAddTest(Model model, @ModelAttribute @Valid Test test, BindingResult errors){


        if (errors.hasErrors()){
            model.addAttribute("title", "New Test");
            return "test/newTest";
        }

        testDao.save(test);
        int testId = test.getId();
        String stringTestId = Integer.toString(testId);

        return "redirect:/test/newquestion/" + stringTestId;
    }



    @RequestMapping(value= "/newquestion/{testId}", method=RequestMethod.GET)
    public String displayAddQuestion(Model model, @PathVariable int testId) {


        //not sure if I need the next two lines
        Test currentTest = testDao.findOne(testId);
        model.addAttribute(currentTest);
        model.addAttribute("title", "New Question");
        model.addAttribute(new Question());

        return "test/newQuestion";



    }


    @RequestMapping(value = "/newquestion/{testId}", method=RequestMethod.POST)
    public String processAddQuestion(Model model, @ModelAttribute @Valid Question question, BindingResult errors, @PathVariable int testId,
                                     @RequestParam(required = false) Integer desiredAnswer1, @RequestParam(required = false) Integer desiredAnswer2, HttpSession session){

        if (errors.hasErrors() || (desiredAnswer1 == null) || (desiredAnswer2 == null) ) {
            System.out.println("made it in");
            if (errors.hasErrors() || ((desiredAnswer1 == null) || (desiredAnswer2 == null)) ){
                System.out.println("made it in to the second");
                model.addAttribute("title","New Question");
                model.addAttribute("claimedError","Please Select a Desired Answer");

                return "test/newQuestion";
            }

            if (errors.hasErrors()){
                System.out.println("made it in to the third");
                model.addAttribute("title","New Question");
                model.addAttribute(new Question());
            return "test/newQuestion";
            }

        }
        System.out.println("made it out?");

        questionDao.save(question);
        Test currentTest = testDao.findOne(testId);
        Question currentQuestionSet = questionDao.findOne(question.getId());
        questionDao.save(currentQuestionSet);
        currentQuestionSet.setTest(currentTest);

        List<Question> listOfQuestions = currentTest.getQuestions();
        listOfQuestions.add(question);
        currentTest.setQuestions(listOfQuestions);

        testDao.save(currentTest);


        String stringTestId = Integer.toString(testId);



        User currentUser = (User) session.getAttribute("loggedInUser");

        Answer anAnswer = new Answer();

        int aNumber = 1;

        anAnswer.setAnswer(aNumber);
        anAnswer.setUser(currentUser);

        HashMap<Question,Answer> answerMap = new HashMap<>();

        answerMap.put(currentQuestionSet, anAnswer);

        currentUser.setAnswers(answerMap);

        answerDao.save(anAnswer);

        userDao.save(currentUser);








        return "redirect:/test/newquestion/" + stringTestId;

        }


        @RequestMapping(value="taketest/{testId}", method=RequestMethod.GET)
        public String displayTest(Model model, @PathVariable int testId ){

        Test currentTest = testDao.findOne(testId);

        //List<Question> currentTestQuestions = currentTest.getQuestions();

        model.addAttribute("title","Take The Test!");
        model.addAttribute("test", currentTest);
        //model.addAttribute("questions",currentTestQuestions);

        return "test/takeTest";



        }





}
