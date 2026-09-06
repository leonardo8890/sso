package com.leonardo.sso;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    private final Algorithm algorithm;

    public TokenService(@Value("${jwt.secret}") String secret){
        algorithm = Algorithm.HMAC256("abc123");
    }

    //retorna o token
    public String generate(String login){
        return JWT.create()
                .withIssuer("sso")
                .withSubject(login)
                .withExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))
                .sign(algorithm);
    }

    //retorna o login
    public String verify(String token){
        return JWT.require(algorithm)
                .withIssuer("sso")
                .build()
                .verify(token)
                .getSubject();
    }

}
