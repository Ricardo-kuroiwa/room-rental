package com.project.gestao_sala.repository;

import com.project.gestao_sala.model.categoria.Categoria;
import com.project.gestao_sala.model.espaco.Espaco;

public interface CategoriaRepository {
    Categoria buscar(String nome );
    Categoria[] listar();
    void salvar(Categoria c);
}
