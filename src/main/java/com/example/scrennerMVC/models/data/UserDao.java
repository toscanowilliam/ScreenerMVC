package com.example.scrennerMVC.models.data;

import com.example.scrennerMVC.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;



import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface UserDao extends CrudRepository<User, Integer> {

    List<User> findByEmail(String email);

    List<User> findByEmailContains(String email);
}















