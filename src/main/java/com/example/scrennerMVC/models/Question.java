package com.example.scrennerMVC.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Question {

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    @Size(min = 3, message = "Please add a question")
    private String question1;

    private String question2;

    //comment

    private Integer desiredAnswer1;

    private Integer desiredAnswer2;

    private Boolean matchingOpposite;

//    private Boolean hasMatch;

    @ManyToOne
    private Test test;

    public Question(String question1, String question2) {
        this.question1 = question1;
        this.question2 = question2;
    }

    public Question() {
    }

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

    public Integer getDesiredAnswer1() {
        return desiredAnswer1;
    }

    public void setDesiredAnswer1(Integer desiredAnswer1) {
        this.desiredAnswer1 = desiredAnswer1;
    }

    public Integer getDesiredAnswer2() {
        return desiredAnswer2;
    }

    public void setDesiredAnswer2(Integer desiredAnswer2) { this.desiredAnswer2 = desiredAnswer2; }

    public Boolean getMatchingOpposite() {
        return matchingOpposite;
    }

    public void setMatchingOpposite(Boolean matchingOpposite) {
        this.matchingOpposite = matchingOpposite;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

//    public Boolean getHasMatch() { return hasMatch; }
//
//    public void setHasMatch(Boolean hasMatch) { this.hasMatch = hasMatch; }


}
