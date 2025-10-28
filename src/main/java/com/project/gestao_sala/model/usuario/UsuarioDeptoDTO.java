package com.project.gestao_sala.model.usuario;

public record UsuarioDeptoDTO(
        String nome,
        String email,
        String senha,
        int nivel,
        int telefone,
        int ramal
) {

}
