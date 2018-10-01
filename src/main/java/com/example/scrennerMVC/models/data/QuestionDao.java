package com.example.scrennerMVC.models.data;

import com.example.scrennerMVC.models.Question;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;



@Repository
@Transactional
public interface QuestionDao extends CrudRepository<Question, Integer> {

}
