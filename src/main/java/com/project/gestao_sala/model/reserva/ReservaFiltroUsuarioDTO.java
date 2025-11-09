package com.project.gestao_sala.model.reserva;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaFiltroUsuarioDTO(
        Character codigoEspaco,
        LocalDate dataInicio,
        Boolean ativa,
        String protocolo
) {}