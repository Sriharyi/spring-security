package com.sriharyi.security.token;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.sriharyi.security.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection =  "tokens")
public class Token {

    @Id
    private String id;

    private String token;

    @Field(targetType = FieldType.STRING)
    private TokenType tokenType;

    private Boolean expired;

    private Boolean revoked;

    @DBRef()
    private User user;
    

}
