package com.project.gestao_sala.services;

import com.project.gestao_sala.model.espaco.Espaco;
import com.project.gestao_sala.model.reserva.Reserva;
import com.project.gestao_sala.repository.EspacoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class ReservaService {
    private final EspacoRepository espacoRepository;


    public ReservaService(EspacoRepository espacoRepository) {
        this.espacoRepository = espacoRepository;
    }

    public boolean criarNovaReserva(Espaco espaco, Reserva nova){
        String protocolo  =  gerarProtocolo(espaco,nova);
        nova.setProtocolo(protocolo);
        espaco.AdicionarReserva(nova);
        return espacoRepository.atualizar(espaco);
    }
    public  String gerarProtocolo(Espaco espaco, Reserva nova){

        return String.format("%c-%d", espaco.getCodigo(), System.currentTimeMillis());
    }

}
