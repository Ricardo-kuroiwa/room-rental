package com.project.gestao_sala.model.usuario;

import com.project.gestao_sala.model.nivelAcesso.NivelAcesso;

// Interface base para polimorfismo
public interface UsuarioDTO {
    String nome();
    String email();
    int nivel();
}