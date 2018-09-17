package com.example.scrennerMVC.models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


@Entity
public class Score {


    @Id
    @GeneratedValue
    private int id;

    private Integer personalityScore;

    private Integer consistencyScore;

    @ManyToOne
    Test test;

    @ManyToOne
    private User user; //user who scores

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public Integer getPersonalityScore() { return personalityScore; }

    public void setPersonalityScore(Integer personalityScore) { this.personalityScore = personalityScore; }

    public Integer getConsistencyScore() { return consistencyScore; }

    public void setConsistencyScore(Integer consistencyScore) { this.consistencyScore = consistencyScore; }

    public Test getTest() { return test; }

    public void setTest(Test test) { this.test = test; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }






}
