package com.sriharyi.security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sriharyi.security.token.Token;



@Repository
public interface TokenRepository extends MongoRepository<Token , String> {

    @Query("{ 'user.id' : ?0, 'expired' : false, 'revoked' : false }")
    List<Token> findActiveTokensByUserId(String userId);

    // List<Token> findByUserAndExpiredFalseAndRevokedFalse(User user);

    // List<Token> findAllByUser(User user);

    Optional<Token> findByToken(String token);
}
