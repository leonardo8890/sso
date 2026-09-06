package com.leonardo.sso;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
//Essa classe é o "porteiro"
public class FilterPerRequest extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;
    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response, //Inicialmente em branco
            FilterChain filterChain //Regras da portaria
    ) throws ServletException, IOException {
            var token = recoverToken(request);
            if(token != null){
                String login = tokenService.verify(token);
                UserDetails user = userRepository.findByLogin(login);

                //Crio um "carteira" que concentra algumas informações do usuário
                var concentredUser = new UsernamePasswordAuthenticationToken(login, null, user.getAuthorities());
                //Preciso informar ao contexto que 'fulano' com role 'tal' está credenciado
                SecurityContextHolder.getContext().setAuthentication(concentredUser);
            }
            //'Passando pra frente' a requisição
            filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authorization = request.getHeader("Authorization");
        if(authorization == null) return null;

        String token = authorization.replace("Bearer ","");
        return token;
    }
}
