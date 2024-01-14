package com.sriharyi.security.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sriharyi.security.user.User;



@Repository
public interface UserRepository extends MongoRepository<User,String> {
    public Optional<User>  findByEmail(String email);
    public boolean existsByEmail(String email);
}
