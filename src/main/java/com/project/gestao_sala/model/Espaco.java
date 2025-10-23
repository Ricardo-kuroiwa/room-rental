package com.project.gestao_sala.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor

public class Espaco {
    private char codigo;
    private String nome;
    private String tipo;
    private String predio;
    private int capacidade;
    private String acesso;
    private boolean ativo;
    private List<Reserva> reservas =  new ArrayList<>();
    private List<Chave> chaves = new ArrayList<>();
    private Categoria categoria;
    public  void AdicionarReserva( Reserva r){

    }
    public boolean vereficarDisponivbilidade(LocalDate data, LocalTime inicio,LocalTime fim){
        return true;
    }
    public void cancelarReserva(String protocolo,String email,LocalTime gora){

    }
    public Reserva obterReserva(String protocolo){
        return null;
    }
}
