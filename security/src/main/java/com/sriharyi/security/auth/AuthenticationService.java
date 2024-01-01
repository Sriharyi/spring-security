package com.sriharyi.security.auth;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sriharyi.security.repository.TokenRepository;
import com.sriharyi.security.repository.UserRepository;
import com.sriharyi.security.service.JwtService;
import com.sriharyi.security.token.Token;
import com.sriharyi.security.token.TokenType;
import com.sriharyi.security.user.Role;
import com.sriharyi.security.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepo;

    private final TokenRepository tokenRepo;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationRespose register(RegisterRequest request) {
        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastnaem())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        var savedUser = userRepo.save(user);
        var jwt = jwtService.generateToken(user);
        saveUserToken(savedUser, jwt);
        return AuthenticationRespose.builder()
                .token(jwt)
                .build();
    }

    private void revokeAllUserTokens(User user)
    {
        List<Token> validUserTokens = tokenRepo.findActiveTokensByUserId(user.getId());
        
        if(validUserTokens.isEmpty())
        {
                return;
        }

        validUserTokens.forEach(
                t -> {
                        t.setExpired(true);
                        t.setRevoked(true);
                }
        );
        tokenRepo.saveAll(validUserTokens); 
    }

    private void saveUserToken(User user, String token) {
        var usertoken = Token.builder()
                .user(user)
                .token(token)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepo.save(usertoken);
    }

    public AuthenticationRespose authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var user = userRepo.findByEmail(request.getEmail()).orElseThrow();

        var jwt = jwtService.generateToken(user);

        revokeAllUserTokens(user);

        saveUserToken(user, jwt);

        return AuthenticationRespose.builder()
                .token(jwt)
                .build();
    }

}
