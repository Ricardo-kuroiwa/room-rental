package com.project.gestao_sala.controller;

import com.project.gestao_sala.model.token.RequestTokenDTO;
import com.project.gestao_sala.model.usuario.LoginRequestDTO;
import com.project.gestao_sala.model.usuario.ResetPasswordDTO;
import com.project.gestao_sala.services.AuthAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth/")
public class AuthController {
    private final AuthAppService authAppService;

    public AuthController(AuthAppService authAppService) {
        this.authAppService = authAppService;
    }
    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO dto){
        boolean result  = authAppService.autenticar(dto.email(), dto.senha());
        if (result) {
            return ResponseEntity.status(HttpStatus.OK).body("Login com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao autenticar devido a um erro interno.");
        }
    }
    @PostMapping("/password/recovery/")
    public ResponseEntity<String> solicitarRecuperacao(@RequestBody RequestTokenDTO dto){
        boolean result  = authAppService.enviarTokenRecuperacao(dto.email());
        if (result) {
            return ResponseEntity.status(HttpStatus.OK).body("Solicitacao de recuperacao com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao solicitar devido a um erro interno.");
        }
    }
    @PostMapping("/password/reset")
    public ResponseEntity<String> redefinirSenha(@RequestBody ResetPasswordDTO dto){
        boolean result  = authAppService.recuperarSenha(
                dto.token(),
                dto.email(),
                dto.password()
        );
        if (result) {
            return ResponseEntity.status(HttpStatus.OK).body("Reset da senha com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao solicitar devido a um erro interno.");
        }
    }
}
