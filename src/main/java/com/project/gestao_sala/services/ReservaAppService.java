package com.project.gestao_sala.services;

import com.project.gestao_sala.model.espaco.Espaco;
import com.project.gestao_sala.model.reserva.Reserva;
import com.project.gestao_sala.model.reserva.ReservaDTO;
import com.project.gestao_sala.repository.implementacao.EspacoFileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class ReservaAppService {
    private final EspacoFileRepository espacoFileRepository;
    private final ReservaService reservaService;

    public ReservaAppService(EspacoFileRepository espacoFileRepository, ReservaService reservaService) {
        this.espacoFileRepository = espacoFileRepository;
        this.reservaService = reservaService;
    }

    public boolean fazerReserva(ReservaDTO dto) {
        // 1. Buscar o espaço alvo
        Espaco espacoTarget = espacoFileRepository.buscar(dto.espaco());
        if (espacoTarget == null) {
            System.err.println("Espaço inexistente: " + dto.espaco());
            return false;
        }

        // 2. Verificar se o espaço está disponível para reservas
        if (!espacoTarget.isAtivo()) {
            System.err.println("Espaço inativo: " + dto.espaco());
            return false;
        }

        // 3. Validar a consistência do horário
        if (dto.horaFim().isBefore(dto.horaInicio()) || dto.horaFim().equals(dto.horaInicio())) {
            System.err.println("A hora de fim deve ser posterior à hora de início.");
            return false;
        }

        Reserva novaReserva = new Reserva();
        novaReserva.setData(LocalTime.from(dto.data()));
        novaReserva.setHoraInicio(dto.horaInicio().toLocalTime());
        novaReserva.setHoraFim(LocalTime.from(dto.horaFim()));
        novaReserva.setEmailUsuario(dto.email());
        novaReserva.setDataCriacao(LocalDateTime.now());
        // 5. Delegar a criação para o ReservaService
        boolean sucesso = reservaService.criarNovaReserva(espacoTarget, novaReserva);

        if (!sucesso) {
            System.err.println("Não foi possível completar a reserva para o espaço: " + dto.espaco());
            return false;

        }

        return sucesso;
    }
}