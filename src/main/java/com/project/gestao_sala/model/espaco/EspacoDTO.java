package com.project.gestao_sala.model.espaco;

public record EspacoDTO(
        char codigo,
        String nome,
        String tipo ,
        String predio,
        int capacidade,
        String acesso,
        boolean ativo)
{
}
