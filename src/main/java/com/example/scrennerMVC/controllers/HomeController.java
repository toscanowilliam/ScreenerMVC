package com.example.scrennerMVC.controllers;

import com.example.scrennerMVC.models.Question;
import com.example.scrennerMVC.models.data.QuestionDao;
import com.example.scrennerMVC.models.data.TestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("home")
public class HomeController {



    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("title", "Home");
        return "index";

    }





}
