package com.sriharyi.jwttoken.service;

public interface JwtService {
    public String getToken(String username);

    public String extractToken(String authorizationHeader);

    public boolean validateToken(String token);
}
