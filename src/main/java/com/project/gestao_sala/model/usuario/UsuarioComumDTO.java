package com.project.gestao_sala.model.usuario;

import com.project.gestao_sala.model.nivelAcesso.NivelAcesso;

public record UsuarioComumDTO(
        String nome,
        String email,
        String senhaHash,
        int nivel,
        int telefone
) implements UsuarioDTO {}