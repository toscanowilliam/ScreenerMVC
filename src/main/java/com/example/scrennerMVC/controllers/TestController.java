package com.example.scrennerMVC.controllers;

import com.example.scrennerMVC.models.Question;
import com.example.scrennerMVC.models.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("test")
public class TestController {

    @RequestMapping(value= "/newtest", method=RequestMethod.GET)
    public String getTestForm(Model model) {

        model.addAttribute("title", "New Test");
        model.addAttribute(new Test());
        model.addAttribute(new Question());

        return "test/newTest";

    }

}
