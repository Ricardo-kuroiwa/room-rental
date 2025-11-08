package com.project.gestao_sala.model.espaco;

import com.project.gestao_sala.enums.TipoAcesso;

public record EspacoDTO(
        char codigo,
        String nome,
        String tipo,
        String predio,
        int capacidade,
        TipoAcesso acesso,
        boolean ativo,
        String categoriaNome  ){

}
