package com.project.gestao_sala.controller;

import com.project.gestao_sala.model.token.RequestTokenDTO;
import com.project.gestao_sala.model.usuario.LoginRequestDTO;
import com.project.gestao_sala.model.usuario.ResetPasswordDTO;
import com.project.gestao_sala.services.AuthAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthAppService authAppService;

    public AuthController(AuthAppService authAppService) {
        this.authAppService = authAppService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        String jwt = authAppService.autenticar(dto.email(), dto.senha());
        System.out.println(jwt);
        if (jwt != null) {

            return ResponseEntity.ok(
                    Map.of(
                            "token", jwt,
                            "message", "Login bem-sucedido!"
                    )
            );
        } else {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciais inválidas."));
        }
    }

    @PostMapping("/password/recovery")
    public ResponseEntity<?> solicitarRecuperacao(@RequestBody RequestTokenDTO dto) {
        boolean result = authAppService.enviarTokenRecuperacao(dto.email());
        if (result) {
            return ResponseEntity.ok(Map.of("message", "Se o e-mail estiver cadastrado, um link de recuperação foi enviado."));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Falha ao processar a solicitação."));
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> redefinirSenha(@RequestBody ResetPasswordDTO dto) {
        boolean result = authAppService.recuperarSenha(
                dto.token(),
                dto.email(),
                dto.password()
        );
        if (result) {
            return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Falha ao redefinir a senha. O token pode ser inválido ou expirado."));
        }
    }
}
