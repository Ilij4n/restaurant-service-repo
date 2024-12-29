package org.example.security;

import org.example.security.service.TokenService;
import org.example.security.service.impl.TokenServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "org.example.security") // ne verujem da ovo treba, jer ce spring svakako skenirati sve od direktorijuma ove kompoennte rekurzivno
public class SecurityConfig {

    @Bean
    public TokenService tokenService() {
        return new TokenServiceImpl();
    }
}