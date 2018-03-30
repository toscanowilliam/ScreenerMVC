package com.example.scrennerMVC.models.data;

import com.example.scrennerMVC.models.Test;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface TestDao extends CrudRepository<Test, Integer> {
}
