package com.project.gestao_sala.controller;

import com.project.gestao_sala.model.reserva.CancelamentoDTO;
import com.project.gestao_sala.model.reserva.ReservaDTO;
import com.project.gestao_sala.model.reserva.ReservaFiltroUsuarioDTO;
import com.project.gestao_sala.model.reserva.ReservaUsuarioDTO;
import com.project.gestao_sala.services.ReservaAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reserva")
public class ReservaController {
    private final ReservaAppService reservaAppService;

    public ReservaController(ReservaAppService reservaAppService) {
        this.reservaAppService = reservaAppService;
    }
    @PostMapping("/create")
    public ResponseEntity<String> fazerReserva(@RequestBody ReservaDTO dto) {
        boolean sucesso = reservaAppService.fazerReserva(dto);
        if (sucesso) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Reserva criada com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao criar reserva devido a um erro interno.");
        }
    }
    @GetMapping("/usuario/{email}")
    public ResponseEntity<List<ReservaUsuarioDTO>> buscarReservasPorUsuario(
            @PathVariable String email,
            @ModelAttribute ReservaFiltroUsuarioDTO filtros) {
        List<ReservaUsuarioDTO> reservasDoUsuario = reservaAppService.listarReservasPorUsuario(email,filtros);
        return ResponseEntity.ok(reservasDoUsuario);
    }
    @PutMapping("/cancelar")
    public ResponseEntity<String> cancelarReserva(@RequestBody CancelamentoDTO dto) {
        boolean sucesso = reservaAppService.cancelar(dto);

        if (sucesso) {
            return ResponseEntity.ok("Reserva cancelada com sucesso.");
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Não foi possível cancelar a reserva. Verifique se o protocolo, espaço e e-mail estão corretos.");
        }
    }
}
