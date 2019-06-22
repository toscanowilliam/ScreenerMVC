package com.example.scrennerMVC.controllers;

import com.example.scrennerMVC.models.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("profile")
public class ProfileController {

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

    @RequestMapping(value = "/{userId}")
    String displayProfile(Model model, @PathVariable int userId, HttpSession session){

        model.addAttribute("user", userDao.findOne(userId));

        return "profile/profile";
    }




}
