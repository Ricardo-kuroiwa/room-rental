package com.project.gestao_sala.repository;

import com.project.gestao_sala.model.categoria.Categoria;

public interface UsuarioRepository {
    Categoria buscar(String nome);
    Categoria[] listar();
    void salvar(Categoria c);

}
