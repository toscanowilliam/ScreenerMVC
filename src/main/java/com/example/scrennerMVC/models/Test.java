package com.example.scrennerMVC.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;



@Entity
public class Test {


    @Id
    @GeneratedValue
    private int id;

    @OneToMany
    private List<Question> questions = new ArrayList<>();

    @NotNull
    @Size(min=3, message = "Please add a name for the test")
    private String testName;

    @NotNull
    @Size(min=3, message = "Please add a description for the test")
    private String description;

    @ManyToOne
    private User testCreator;

    @ManyToMany
    private List<User> testTakers;




    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getTestCreator() {
        return testCreator;
    }

    public void setTestCreator(User testCreator) {
        this.testCreator = testCreator;
    }

    public List<User> getTestTakers() {
        return testTakers;
    }

    public void setTestTakers(List<User> testTakers) {
        this.testTakers = testTakers;
    }


}
