package com.project.gestao_sala.model.nivelAcesso;

import com.project.gestao_sala.enums.Permissao;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class NivelAcesso implements Serializable {
    private int nivel;
    private Permissao[] permissoes;

}
