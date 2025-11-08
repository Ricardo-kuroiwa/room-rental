package com.project.gestao_sala.controller;

import com.project.gestao_sala.model.reserva.ReservaDTO;
import com.project.gestao_sala.services.ReservaAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reserva")
public class ReservaController {
    private final ReservaAppService reservaAppService;

    public ReservaController(ReservaAppService reservaAppService) {
        this.reservaAppService = reservaAppService;
    }
    @PostMapping("/create")
    public ResponseEntity<String> fazerReserva(ReservaDTO dto) {
        boolean sucesso = reservaAppService.fazerReserva(dto);
        if (sucesso) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Reserva criada com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao criar reserva devido a um erro interno.");
        }

    }
}
