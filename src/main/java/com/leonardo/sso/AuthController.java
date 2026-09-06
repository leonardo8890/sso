package com.leonardo.sso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    UserService userService;
    @Autowired
    TokenService tokensService;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/signin")
    public ResponseEntity signin(@RequestBody UserDto dto){
        if(userService.hasUser(dto.login())){
            var loginPassword = new UsernamePasswordAuthenticationToken(
                    dto.login(), dto.password()
            ); //"pacote" com login e senha
            var autenticacao = authenticationManager.authenticate(loginPassword);
            return ResponseEntity.ok(tokensService.generate(dto.login()));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody UserDto dto){
        if(!userService.hasUser(dto.login())){
            UserModel user = new UserModel(
                    dto.login(),
                    passwordEncoder.encode(dto.password()), //Hash
                    dto.role());
            userService.saveUser(user);
            return ResponseEntity.ok(tokensService.generate(dto.login()));
        }
        return ResponseEntity.badRequest().build();
    }

}
