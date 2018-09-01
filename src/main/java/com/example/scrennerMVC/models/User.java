package com.example.scrennerMVC.models;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class User {

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    @Size(min=5, max=30)
    private String email;

    @Size(min=3, max=15)
    @Transient
    private String password;

    @NotNull
    private String pwHash;

    @OneToMany(mappedBy = "user")
    @MapKeyJoinColumn(name="QUESTION_ID")
    private Map<Question,Answer> answers = new HashMap<>(); //One User to many answers of questions

    @OneToMany
    @JoinColumn(name="test_creator_id")
    private List<Test> createdTests = new ArrayList<>();






    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPwHash() {
        return pwHash;
    }

    public void setPwHash(String pwHash) {
        this.pwHash = pwHash;
    }

    public Map<Question, Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Question, Answer> answers) {
        this.answers = answers;
    }

    public List<Test> getTest() {
        return createdTests;
    }

    public void setTest(List<Test> test) {
        this.createdTests = test;
    }






}
