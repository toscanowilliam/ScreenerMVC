package com.example.scrennerMVC.models;


import org.hibernate.annotations.Type;

import javax.persistence.*;


@Entity
public class Answer {


    @Id
    @GeneratedValue
    private long id;

    private int answer;

    private int matchingAnswer;

    @ManyToOne
    private User user; //user who answers questions

    @ManyToOne
    @JoinColumn(name="QUESTION_ID")
    Question question; //question of the current answer

//    @ManyToOne
//    @JoinColumn(name="QUESTION_MATCH_ID")
//    QuestionMatch questionMatch;





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

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public int getMatchingAnswer() {
        return matchingAnswer;
    }

    public void setMatchingAnswer(int matchingAnswer) {
        this.matchingAnswer = matchingAnswer;
    }

}
