package com.project.gestao_sala.model.reserva;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReservaUsuarioDTO(
        String protocolo,
        char codigoEspaco,
        LocalDateTime data,
        LocalTime horaInicio,
        LocalTime horaFim,
        LocalDateTime dataCriacao,
        LocalDateTime dataCancelamento,
        boolean ativa

) {
}
