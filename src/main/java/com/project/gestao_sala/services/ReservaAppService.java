package com.project.gestao_sala.services;

import com.project.gestao_sala.model.espaco.Espaco;
import com.project.gestao_sala.model.reserva.Reserva;
import com.project.gestao_sala.model.reserva.ReservaDTO;
import com.project.gestao_sala.model.reserva.ReservaFiltroUsuarioDTO;
import com.project.gestao_sala.model.reserva.ReservaUsuarioDTO;
import com.project.gestao_sala.repository.implementacao.EspacoFileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
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

    public List<ReservaUsuarioDTO> listarReservasPorUsuario(String email,
                                                            ReservaFiltroUsuarioDTO filtros) {
        return Arrays.stream(espacoFileRepository.listar())
                .filter(e -> filtros.codigoEspaco() == null ||
                        e.getCodigo() == filtros.codigoEspaco())
                .flatMap(e -> e.getReservas().stream()
                        .filter(r -> email.equalsIgnoreCase(r.getEmailUsuario()))
                        .filter(r -> aplicarFiltros(r, filtros))
                        .map(r -> toDTO(r, e)))
                .collect(Collectors.toList());
    }

    private boolean aplicarFiltros(Reserva r, ReservaFiltroUsuarioDTO f) {
        if (f == null) {
            System.out.println("Aqui aaaaaaaaaaaaaaaaa");
            return true;
        }
        if (f.ativa() != null && r.isAtiva() != f.ativa())
            return false;
        if (f.protocolo() != null &&
                !r.getProtocolo().contains(f.protocolo())) return false;
        if (f.dataInicio() != null) {
            LocalDate data = r.getData().toLocalDate();
            if (data.isBefore(f.dataInicio())) return false;
        }
        return true;
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
        if (dto.horaFim().isBefore(dto.horaInicio()) ||
                dto.horaFim().equals(dto.horaInicio())) return false;

        Reserva nova = new Reserva();
        nova.setData(dto.data());
        nova.setHoraInicio(dto.horaInicio());
        nova.setHoraFim(LocalTime.from(dto.horaFim()));
        nova.setEmailUsuario(dto.email());
        nova.setDataCriacao(LocalDateTime.now());

        return reservaService.criarNovaReserva(espacoTarget, nova);
    }
}