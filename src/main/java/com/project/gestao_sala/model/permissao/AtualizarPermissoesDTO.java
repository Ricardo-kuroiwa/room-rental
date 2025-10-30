package com.project.gestao_sala.model.permissao;

import com.project.gestao_sala.enums.Permissao;

public record AtualizarPermissoesDTO(
        Permissao[] permissoes
) {}