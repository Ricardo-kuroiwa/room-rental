package com.project.gestao_sala.repository;

import com.project.gestao_sala.model.nivelAcesso.NivelAcesso;

public interface NivelAcessoRepository {
    NivelAcesso buscar(int nivel);
    NivelAcesso[] listar();
    boolean salvar(NivelAcesso n);
    boolean deletar(int nivel);
    boolean atualizar(NivelAcesso n);

}
