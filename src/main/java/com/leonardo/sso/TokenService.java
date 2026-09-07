package com.leonardo.sso;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final Algorithm algorithm;

    @Autowired
    UserRepository userRepository;

    public TokenService(@Value("${jwt.secret}") String secret){
        algorithm = Algorithm.HMAC256(secret);
    }

    //retorna o token
    public String generate(UserDto dto){
        UserDetails userDetails = userRepository.findByLogin(dto.login());

        List<String> roleNames = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return JWT.create()
                .withIssuer("sso")
                .withSubject(dto.login())
                .withClaim("roles", roleNames)
                .withExpiresAt(Instant.now().plus(120, ChronoUnit.MINUTES))
                .sign(algorithm);
    }

    //Essa verificação só está servindo para controlar quem acessa '/auth/signup'
    public String verify(String token){
        return JWT.require(algorithm)
                .withIssuer("sso")
                .build()
                .verify(token)
                .getSubject();
    }

}
