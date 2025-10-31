package com.project.gestao_sala.model.usuario;

import com.project.gestao_sala.model.nivelAcesso.NivelAcessoDTO;

public record UsuarioDeptoResponseDTO(
        String nome,
        String email,
        int telefone,
        int ramal,
        NivelAcessoDTO nivel  // Dados completos do nível
) {}