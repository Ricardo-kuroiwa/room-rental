package com.project.gestao_sala.model.nivelAcesso;

import com.project.gestao_sala.enums.Permissao;

public record NivelAcessoDTO(
        int nivel,
        Permissao[] permissaos
) {
}