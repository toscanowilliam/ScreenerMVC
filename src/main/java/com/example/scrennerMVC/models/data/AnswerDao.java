package com.example.scrennerMVC.models.data;

import com.example.scrennerMVC.models.Answer;
import org.springframework.data.repository.CrudRepository;


public interface AnswerDao extends CrudRepository<Answer, Integer> {
}
