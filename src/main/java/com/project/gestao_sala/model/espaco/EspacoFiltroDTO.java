package com.project.gestao_sala.model.espaco;

public record EspacoFiltroDTO(
        Character codigo,
        String nome,
        String tipo,
        String predio,
        Boolean ativo,
        Integer capacidade
) {}