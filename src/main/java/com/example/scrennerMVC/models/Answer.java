package com.example.scrennerMVC.models;


import javax.persistence.*;


@Entity
public class Answer {


    //Thinking about removing the Answer Object all together. It's not efficient assuming 5000 users would use it.


    @Id
    @GeneratedValue
    private int id;

    private Integer answer;

    private Integer matchingAnswer;

    @ManyToOne
    private User user; //user who answers questions

    @ManyToOne
    @JoinColumn(name="QUESTION_ID")
    private Question question; //question of the current answer


    @ManyToOne
    private Test currentTest;








    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getAnswer() {
        return answer;
    }

    public void setAnswer(Integer answer) {
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

    public Integer getMatchingAnswer() {
        return matchingAnswer;
    }

    public void setMatchingAnswer(Integer matchingAnswer) {
        this.matchingAnswer = matchingAnswer;
    }

    public Test getCurrentTest() {
        return currentTest;
    }

    public void setCurrentTest(Test current) {
        this.currentTest = current;
    }


}
