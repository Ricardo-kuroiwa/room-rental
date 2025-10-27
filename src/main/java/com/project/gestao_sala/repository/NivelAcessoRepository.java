package com.project.gestao_sala.repository;

import com.project.gestao_sala.model.nivelAcesso.NivelAcesso;

public interface NivelAcessoRepository {
    NivelAcesso buscar(int nivel);
    NivelAcesso[] listar();
    void salvar(NivelAcesso n);
}
