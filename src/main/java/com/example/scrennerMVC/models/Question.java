package com.example.scrennerMVC.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question {

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    @Size(min=3, message = "Please add a question")
    private String question1;

    @NotNull
    @Size(min=3, message = "Please add a question")
    private String question2;

//    private int answer1;
//
//    private int answer2;


    private Integer desiredAnswer1;


    private Integer desiredAnswer2;

//    private int matchingAnswer;

    private Boolean matchingOpposite;



    @ManyToOne
    private Test test;


    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }




//    public List<Test> getTests() {
//        return tests;
//    }
//
//    public void setTests(List<Test> tests) {
//        this.tests = tests;
//    }



    public Question(String question1, String question2){
        this.question1 = question1;
        this.question2 = question2;
    }

    public Question(){}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion1() {
        return question1;
    }

    public void setQuestion1(String question1) {
        this.question1 = question1;
    }

    public String getQuestion2() {
        return question2;
    }

    public void setQuestion2(String question2) {
        this.question2 = question2;
    }

//    public int getAnswer1() {
//        return answer1;
//    }
//
//    public void setAnswer1(int answer1) {
//        this.answer1 = answer1;
//    }
//
//    public int getAnswer2() {
//        return answer2;
//    }
//
//    public void setAnswer2(int answer2) {
//        this.answer2 = answer2;
//    }

    public Integer getDesiredAnswer1() {
        return desiredAnswer1;
    }

    public void setDesiredAnswer1(Integer desiredAnswer1) {
        this.desiredAnswer1 = desiredAnswer1;
    }

    public Integer getDesiredAnswer2() {
        return desiredAnswer2;
    }

    public void setDesiredAnswer2(Integer desiredAnswer2) {
        this.desiredAnswer2 = desiredAnswer2;
    }

//    public int getMatchingAnswer() {
//        return matchingAnswer;
//    }
//
//    public void setMatchingAnswer(int matchingAnswer) {
//        this.matchingAnswer = matchingAnswer;
//    }

    public Boolean getMatchingOpposite() {
        return matchingOpposite;
    }

    public void setMatchingOpposite(Boolean matchingOpposite) {
        this.matchingOpposite = matchingOpposite;
    }




}
