package com.project.gestao_sala.model.reserva;

import java.time.LocalDateTime;

public record ReservaDTO(
        LocalDateTime data,
        LocalDateTime horaInicio,
        LocalDateTime horaFim,
        char espaco,
        String email
) {
}
