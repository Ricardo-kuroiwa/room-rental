package com.project.gestao_sala.repository;

import com.project.gestao_sala.model.usuario.Usuario;

public interface UsuarioRepository {
    Usuario buscar(String email);
    Usuario[] listar();
    boolean salvar(Usuario usuario);
    boolean deletar(String email);
    boolean atualizar(Usuario usuario);
}