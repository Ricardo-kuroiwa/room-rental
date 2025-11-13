package com.project.gestao_sala.services;

import org.springframework.stereotype.Service;

@Service
public class PasswordPolicyService {
    public boolean validarSenha(String senha){
        if (senha == null || senha.length() < 10) {
            return false;
        }
        boolean temMaiuscula = senha.matches(".*[A-Z].*");
        boolean temMinuscula = senha.matches(".*[a-z].*");
        boolean temCaractereEspecial = senha.matches(".*[^a-zA-Z0-9].*");

        return temMaiuscula && temMinuscula && temCaractereEspecial;
    }
}
