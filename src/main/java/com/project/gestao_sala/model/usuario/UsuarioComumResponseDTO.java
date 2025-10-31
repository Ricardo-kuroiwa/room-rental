package com.project.gestao_sala.model.usuario;

import com.project.gestao_sala.model.nivelAcesso.NivelAcessoDTO;

public record UsuarioComumResponseDTO(
        String nome,
        String email,
        int telefone,
        NivelAcessoDTO nivel  // Dados completos do nível
) {}