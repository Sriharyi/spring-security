package com.sriharyi.security.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping(path = "/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    //Register Method call
    @PostMapping("/register")
    public ResponseEntity<AuthenticationRespose> register(@RequestBody RegisterRequest request) {
        
        
        return ResponseEntity.ok(service.register(request));
    }
    

    //Validation Method call or authentication method call
      @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationRespose> authentication(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
    
}
