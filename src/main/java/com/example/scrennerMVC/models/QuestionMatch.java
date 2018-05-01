//package com.example.scrennerMVC.models;
//
//
//import javax.persistence.*;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//public class QuestionMatch {
//
//    @Id
//    @GeneratedValue
//    private int id;
//
//    @NotNull
//    @Size(min=3, message = "Please add a question")
//    private String questionMatch;
//
//
//    public int desiredAnswerForQuestionMatch;
//
//
//    private Boolean matchingOpposite;
//
//    @ManyToOne
//    private Test test;
//
//
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getQuestionMatch() {
//        return questionMatch;
//    }
//
//    public void setQuestionMatch(String questionMatch) {
//        this.questionMatch = questionMatch;
//    }
//
//    public Integer getDesiredAnswerForQuestionMatch() {
//        return desiredAnswerForQuestionMatch;
//    }
//
//    public void setDesiredAnswerForQuestionMatch(Integer desiredAnswerForQuestionMatch) {
//        this.desiredAnswerForQuestionMatch = desiredAnswerForQuestionMatch;
//    }
//
//    public Boolean getMatchingOpposite() {
//        return matchingOpposite;
//    }
//
//    public void setMatchingOpposite(Boolean matchingOpposite) {
//        this.matchingOpposite = matchingOpposite;
//    }
//
//    public Test getTest() {
//        return test;
//    }
//
//    public void setTest(Test test) {
//        this.test = test;
//    }
//}
