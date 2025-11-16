package com.project.gestao_sala.model.reserva;

import java.time.LocalDate;

public record ReservaFiltroUsuarioDTO(
        Character codigoEspaco,
        LocalDate dataInicio,
        LocalDate dataFim,
        Boolean ativa,
        String protocolo
) {}