package com.example.scrennerMVC.models.data;

import com.example.scrennerMVC.models.Score;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;


@Repository
@Transactional
public interface ScoreDao extends CrudRepository<Score, Integer> {
}
