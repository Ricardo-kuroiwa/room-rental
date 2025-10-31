package com.project.gestao_sala.repository;

import com.project.gestao_sala.model.categoria.Categoria;
import com.project.gestao_sala.model.espaco.Espaco;

public interface CategoriaRepository {
    Categoria buscar(String nome );
    Categoria[] listar();
    boolean salvar(Categoria c);
    boolean deletar(String nome);
    boolean atualizar(Categoria c);
}
