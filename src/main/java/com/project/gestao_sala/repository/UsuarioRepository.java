package com.project.gestao_sala.repository;

import com.project.gestao_sala.model.usuario.Usuario;

public interface UsuarioRepository {
    Usuario buscar(String nome);
    Usuario[] listar();
    void salvar(Usuario u);

}
