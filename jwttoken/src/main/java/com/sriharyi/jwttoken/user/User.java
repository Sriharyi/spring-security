package com.sriharyi.jwttoken.user;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String _id;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    
}
