package com.sriharyi.jwttoken.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Service
public class JwtServiceImpl implements JwtService {

    // public String extractUsername(){
    // return null;
    // }

    public String getToken(String username) {
        Map <String ,Object> map = new HashMap<>();
        String role = "admin";
        map.put("Role", role);
        Claims claim = Jwts.claims().setSubject(username);
        claim.putAll(map);
        String token = Jwts.builder().setClaims(claim).signWith(SignatureAlgorithm.HS256, "secretkey").compact();
        Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(token).getBody();
       String rolefromtoken =  claims.get("Role").toString();
        System.out.println(rolefromtoken);
        return token;
    }

    public String extractToken(String authorizationHeader) {

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey("secretkey").parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
        } catch (UnsupportedJwtException e) {
            e.printStackTrace();
        } catch (MalformedJwtException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }

}
