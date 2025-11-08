package com.project.gestao_sala.plataform;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHasher {
    private  final BCryptPasswordEncoder passwordEncoder;

    public PasswordHasher() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    public String hash(String senha){
        return passwordEncoder.encode(senha);
    }
    public boolean verify(String senha, String hash){
        return passwordEncoder.matches(senha,hash);
    }
}
