package com.example.scrennerMVC.models;


import javax.persistence.*;


@Entity
public class Answer {


    @Id
    @GeneratedValue
    private long id;

    private int answer;


    @ManyToOne
    private User user;

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @ManyToOne
    @JoinColumn(name="QUESTION_ID")
    Question question;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
