package com.example.scrennerMVC.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Question {

    @Id
    @GeneratedValue
    private int id;

    private String question1;

    private String question2;

//    private int answer1;
//
//    private int answer2;

    private int desiredAnswer1;

    private int desiredAnswer2;

//    private int matchingAnswer;

    private Boolean matchingOpposite;






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

    public int getDesiredAnswer1() {
        return desiredAnswer1;
    }

    public void setDesiredAnswer1(int desiredAnswer1) {
        this.desiredAnswer1 = desiredAnswer1;
    }

    public int getDesiredAnswer2() {
        return desiredAnswer2;
    }

    public void setDesiredAnswer2(int desiredAnswer2) {
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
