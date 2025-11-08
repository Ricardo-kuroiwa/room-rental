package com.project.gestao_sala.model.usuario;

public record ResetPasswordDTO(
        String token,
        String email,
        String password
) {
}
