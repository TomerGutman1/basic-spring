package com.handson.basic.repo;


import com.handson.basic.model.User;
import org.springframework.data.repository.CrudRepository;


import java.util.Optional;


public interface UserRepository extends CrudRepository<User,Long> {
    Optional<User> findByUsername(String username);
}
