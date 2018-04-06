package com.example.scrennerMVC.models.data;

import com.example.scrennerMVC.models.Answer;
import com.example.scrennerMVC.models.Question;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

public interface AnswerDao extends CrudRepository<Answer, Integer> {
}
