    package com.project.gestao_sala.model.espaco;

    import com.project.gestao_sala.enums.TipoAcesso;
    import com.project.gestao_sala.model.reserva.Reserva;
    import com.project.gestao_sala.model.categoria.Categoria;
    import com.project.gestao_sala.model.chave.Chave;
    import lombok.*;

    import java.util.ArrayList;
    import java.util.List;

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor

    public class Espaco {
        private char codigo;
        private String nome;
        private String tipo;
        private String predio;
        private int capacidade;
        private TipoAcesso acesso;
        private boolean ativo;
        private List<Reserva> reservas =  new ArrayList<>();
        private List<Chave> chaves = new ArrayList<>();
        private Categoria categoria;

        public  void AdicionarReserva( Reserva r){
            if(r != null){
                reservas.add(r);
            }
        }

    }
