package com.project.gestao_sala.model.nivelAcesso;

import com.project.gestao_sala.enums.Permissao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class NivelAcesso {
    private int nivel;
    private Permissao permissoes;
}
