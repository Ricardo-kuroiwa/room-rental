package com.project.gestao_sala.model.usuario;

public record UsuarioDTO(
        String nome,
        String email,
        String senha,
        int nivel,
        int telefone
) {
    //Vereficar consistencia , no diagrama de classe é dito que é uma interface que no meu ver nao tem muito sentido
}
