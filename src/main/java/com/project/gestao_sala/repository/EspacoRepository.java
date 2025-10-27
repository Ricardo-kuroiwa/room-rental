package com.project.gestao_sala.repository;

import com.project.gestao_sala.model.espaco.Espaco;

public interface EspacoRepository {
    Espaco buscar(char codigo);
    Espaco[] listar();
    void salvar(Espaco e);
}
