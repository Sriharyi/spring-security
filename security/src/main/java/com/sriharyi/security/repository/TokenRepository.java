package com.sriharyi.security.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sriharyi.security.token.Token;
import java.util.List;
import java.util.Optional;
import com.sriharyi.security.user.User;



@Repository
public interface TokenRepository extends MongoRepository<Token , String> {

    @Query("{'user._id': ?0, 'isExpired': false, 'isRevoked': false}")
    List<Token> findActiveTokensByUserId(String userId);

    List<Token> findByUserAndExpiredFalseAndRevokedFalse(User user);

    List<Token> findAllByUser(User user);

    Optional<Token> findByToken(String token);
}
