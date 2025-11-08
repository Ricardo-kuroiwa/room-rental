package com.project.gestao_sala.model.espaco;

import com.project.gestao_sala.model.categoria.CategoriaDTO;

public record EspacoResponseDTO(
        String codigo,
        String nome,
        String tipo,
        String predio,
        int capacidade,
        String acesso,
        boolean ativo,
        CategoriaDTO categoria,
        int totalReservas,
        int totalChaves
) {}