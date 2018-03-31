package com.example.scrennerMVC.controllers;

import com.example.scrennerMVC.models.Question;
import com.example.scrennerMVC.models.Test;
import com.example.scrennerMVC.models.data.QuestionDao;
import com.example.scrennerMVC.models.data.TestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("test")
public class TestController {


    @Autowired
    QuestionDao questionDao;

    @Autowired
    TestDao testDao;

    @RequestMapping(value= "/newtest", method=RequestMethod.GET)
    public String getTestForm(Model model) {

        model.addAttribute("title", "New Test");
        model.addAttribute(new Test());
        model.addAttribute(new Question());

        return "test/newTest";

    }


    @RequestMapping(value = "/newtest", method=RequestMethod.POST)
    public String processAddQuestion(Model model, @ModelAttribute @Valid Question question, @ModelAttribute @Valid Test test){


        questionDao.save(question);



        testDao.save(test);
        Test currentTest = testDao.findOne(test.getId());

        Question currentQuestionSet = questionDao.findOne(question.getId());
        currentQuestionSet.setTest(currentTest);

        questionDao.save(currentQuestionSet);

        List<Question> listOfQuestions = currentTest.getQuestions();

        listOfQuestions.add(question);

        currentTest.setQuestions(listOfQuestions);

        testDao.save(currentTest);



      //  List<Question> listOfQuestions = currentTest.getQuestions();

        //listOfQuestions.add(question);

        //questionDao.save(question);


        //listOfQuestions.add(question);

        //currentTest.setQuestions(listOfQuestions);

       // testDao.save(currentTest);


        return "index";

        }


}
