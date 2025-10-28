package com.project.gestao_sala.model.usuario;

import com.project.gestao_sala.model.nivelAcesso.NivelAcesso;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDepto extends Usuario{
    int ramal;

    public UsuarioDepto(String nome, String email, String senhaHash, NivelAcesso nivel, int telefone) {
        super(nome, email, senhaHash, nivel, telefone);
    }
}
