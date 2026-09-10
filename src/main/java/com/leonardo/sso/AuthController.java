package com.leonardo.sso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/autenticacao")
public class AuthController {
    @Autowired
    UserService userService;
    @Autowired
    TokenService tokensService;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/entrar")
    public ResponseEntity entrar(@RequestBody UserDto dto){
        UserModel userModel = new UserModel(dto.login(),null, dto.role());

        // Se o usuário estiver cadastrado
        if(userService.hasUser(dto.login())){
            var loginPassword = new UsernamePasswordAuthenticationToken(dto.login(), dto.password()); // "Pacote" com login e senha
            var autenticacao = authenticationManager.authenticate(loginPassword);
            String token = tokensService.generate(dto);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/cadastrar")
    public ResponseEntity cadastrar(@RequestBody UserDto dto){
        if(!userService.hasUser(dto.login())){
            UserModel user = new UserModel(
                    dto.login(),
                    passwordEncoder.encode(dto.password()), //Hash
                    dto.role());
            userService.saveUser(user);
            return ResponseEntity.ok(tokensService.generate(dto));
        }
        return ResponseEntity.badRequest().build();
    }

}
