package com.project.gestao_sala.model.reserva;

import java.sql.Time;
import java.util.Date;

public record ReservaDTO(
        Date data,
        Time horaInicio,
        Time horaFim,
        char espaco
) {
}
