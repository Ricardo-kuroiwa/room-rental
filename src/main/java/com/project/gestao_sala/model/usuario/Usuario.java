package com.project.gestao_sala.model.usuario;

import com.project.gestao_sala.model.nivelAcesso.NivelAcesso;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Usuario{
    String nome;
    String email;
    String senhaHash;
    NivelAcesso nivel;
    int telefone;
}
