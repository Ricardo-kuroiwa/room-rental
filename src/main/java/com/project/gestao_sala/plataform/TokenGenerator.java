package com.project.gestao_sala.plataform;

import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
public class TokenGenerator {
    public String generateToke(){
        UUID token = UUID.randomUUID();
        return token.toString();
    }
}
