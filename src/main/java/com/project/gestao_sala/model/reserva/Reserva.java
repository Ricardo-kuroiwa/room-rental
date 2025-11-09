package com.project.gestao_sala.model.reserva;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class Reserva {
    private LocalDateTime data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private String emailUsuario;
    private boolean ativa;
    private String protocolo;
    private String codigoEspaco;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataCancelamento;

}
