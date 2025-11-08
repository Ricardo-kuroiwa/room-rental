package com.project.gestao_sala.repository;

import com.project.gestao_sala.model.espaco.Espaco;
import com.project.gestao_sala.model.nivelAcesso.NivelAcesso;
import com.project.gestao_sala.model.reserva.Reserva;

public interface EspacoRepository {
    Espaco buscar(char codigo);
    Espaco[] listar();
    boolean salvar(Espaco e);
    boolean deletar(char codigo);
    boolean atualizar(Espaco e);

    boolean criarNovaReserva(char codigoEspaco, Reserva novaReserva);
}
