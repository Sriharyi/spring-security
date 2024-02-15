package com.sriharyi.security.auth;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public AuthenticationResponse register(RegisterRequest request) {
        User user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .build();
        if (userRepo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        var savedUser = userRepo.save(user);
        var jwt = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwt);
        return AuthenticationResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken)
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

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var user = userRepo.findByEmail(request.getEmail()).orElseThrow();

        var jwt = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);

        saveUserToken(user, jwt);

        return AuthenticationResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken)
                .build();
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if(authHeader == null || !authHeader.startsWith("Bearer "))
        {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUserName(refreshToken);    //todo  extract the UserEmail from jwt token
        if(userEmail != null)
        {
            User user = this.userRepo.findByEmail(userEmail).orElseThrow(()-> new UsernameNotFoundException("User not found"));

            if(jwtService.isTokenvalid(refreshToken, user) )
            {
                String accessToken = jwtService.generateToken(user);

                revokeAllUserTokens(user);

                saveUserToken(user, accessToken);

                AuthenticationResponse authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();

                // Manually construct the JSON string
                String json = String.format("{\"accessToken\":\"%s\", \"refreshToken\":\"%s\"}",
                        authResponse.getAccessToken(),
                        authResponse.getRefreshToken());

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(json);


            }
        }
    }
}
