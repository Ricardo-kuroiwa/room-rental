package com.project.gestao_sala.services;

import com.project.gestao_sala.model.espaco.Espaco;
import com.project.gestao_sala.model.reserva.*;
import com.project.gestao_sala.repository.implementacao.EspacoFileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservaAppService {

    private final EspacoFileRepository espacoFileRepository;
    private final ReservaService reservaService;

    public ReservaAppService(EspacoFileRepository espacoFileRepository,
                             ReservaService reservaService) {
        this.espacoFileRepository = espacoFileRepository;
        this.reservaService = reservaService;
    }

    public List<ReservaUsuarioDTO> listarReservasPorUsuario(String email, ReservaFiltroUsuarioDTO filtros) {

        if (filtros != null && filtros.codigoEspaco() != null) {
            Espaco espaco = espacoFileRepository.buscar(filtros.codigoEspaco());
            if (espaco == null) {
                return Collections.emptyList();
            }
            return Optional.ofNullable(espaco.getReservas()).orElse(Collections.emptyList())
                    .stream()
                    .filter(reserva -> email.equalsIgnoreCase(reserva.getEmailUsuario()))
                    .filter(reserva -> aplicarFiltros(reserva, filtros))
                    .map(reserva -> toDTO(reserva, espaco))
                    .collect(Collectors.toList());
        } else {
            Espaco[] todosEspacos = Optional.ofNullable(espacoFileRepository.listar()).orElse(new Espaco[0]);
            return Arrays.stream(todosEspacos)
                    .flatMap(espaco ->
                            Optional.ofNullable(espaco.getReservas()).orElse(Collections.emptyList())
                                    .stream()
                                    .filter(reserva -> email.equalsIgnoreCase(reserva.getEmailUsuario()))
                                    .filter(reserva -> aplicarFiltros(reserva, filtros))
                                    .map(reserva -> toDTO(reserva, espaco))
                    )
                    .collect(Collectors.toList());
        }
    }

    private boolean aplicarFiltros(Reserva reserva, ReservaFiltroUsuarioDTO filtros) {
        if (filtros == null) {
            return true;
        }

        if (filtros.ativa() != null && reserva.isAtiva() != filtros.ativa()) {
            return false;
        }

        if (filtros.protocolo() != null && !filtros.protocolo().isBlank()) {
            if (!reserva.getProtocolo().equalsIgnoreCase(filtros.protocolo().trim())) {
                return false;
            }
        }

        LocalDate dataReserva = reserva.getData().toLocalDate();

        if (filtros.dataInicio() != null && dataReserva.isBefore(filtros.dataInicio())) {
            return false;
        }

        if (filtros.dataFim() != null && dataReserva.isAfter(filtros.dataFim())) {
            return false;
        }

        return true;
    }
    public boolean cancelar(CancelamentoDTO dto) {
        if (dto == null || dto.protocolo() == null || dto.protocolo().isBlank()) {
            return false;
        }

        Espaco espaco = espacoFileRepository.buscar(dto.codigoEspaco());
        if (espaco == null) {
            return false;
        }

        List<Reserva> reservas = Optional.ofNullable(espaco.getReservas()).orElse(Collections.emptyList());

        for (Reserva reserva : reservas) {
            if (dto.protocolo().equalsIgnoreCase(reserva.getProtocolo()) &&
                    dto.email().equalsIgnoreCase(reserva.getEmailUsuario())) {

                if (!reserva.isAtiva()) {
                    return false;
                }

                reserva.setAtiva(false);
                reserva.setDataCancelamento(LocalDateTime.now());

                espacoFileRepository.salvar(espaco);
                return true;
            }
        }

        return false;
    }
    private ReservaUsuarioDTO toDTO(Reserva r, Espaco e) {
        return new ReservaUsuarioDTO(
                r.getProtocolo(),
                e.getCodigo(),
                r.getData(),
                r.getHoraInicio(),
                r.getHoraFim(),
                r.getDataCriacao(),
                r.getDataCancelamento(),
                r.isAtiva()
        );
    }

    public boolean fazerReserva(ReservaDTO dto) {
        Espaco espacoTarget = espacoFileRepository.buscar(dto.espaco());
        if (espacoTarget == null) return false;
        if (!espacoTarget.isAtivo()) return false;

        // Validação de horário
        if (dto.horaFim().isBefore(dto.horaInicio()) || dto.horaFim().equals(dto.horaInicio())) {
            return false;
        }

        Reserva nova = new Reserva();
        nova.setData(dto.data());
        nova.setHoraInicio(dto.horaInicio());
        nova.setHoraFim(dto.horaFim());
        nova.setEmailUsuario(dto.email());
        nova.setDataCriacao(LocalDateTime.now());
        nova.setAtiva(true);
        return reservaService.criarNovaReserva(espacoTarget, nova);
    }
}