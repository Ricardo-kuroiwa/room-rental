package com.project.gestao_sala.controller;

import com.project.gestao_sala.enums.Permissao;
import com.project.gestao_sala.model.nivelAcesso.NivelAcessoDTO;
import com.project.gestao_sala.model.permissao.AtualizarPermissoesDTO;
import com.project.gestao_sala.services.AdminNiveisAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/adm")
public class AdminController {
    private final AdminNiveisAppService adminNiveisAppService;

    public AdminController(AdminNiveisAppService adminNiveisAppService) {
        this.adminNiveisAppService = adminNiveisAppService;
    }
    @PutMapping("/niveis/update/{nivel}")
    public ResponseEntity<String> atualizarNivelAcesso(@PathVariable int nivel,
                                                       @RequestBody AtualizarPermissoesDTO atualizarPermissoesDTO){
        try {
            boolean sucesso = adminNiveisAppService.atualizarNivelAcesso(nivel,atualizarPermissoesDTO.permissoes());
            if (sucesso) {
                return ResponseEntity.status(HttpStatus.OK).body("Nível de acesso atualizado com sucesso!");
            } else {
                // Retorna um código 400 se falhou ao criar o nível de acesso
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falha ao criar nível de acesso.");
            }
        } catch (Exception e) {
            // Retorna código 500 em caso de erro no servidor
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar nível de acesso: " + e.getMessage());
        }
    }
    @PostMapping("/niveis/create")
    public ResponseEntity<String> criarNivelAcesso(@RequestBody NivelAcessoDTO nivelAcessoDTO) {
        try {
            boolean sucesso = adminNiveisAppService.criarNivelAcesso(nivelAcessoDTO);
            if (sucesso) {
                // Retorna um código 201 em caso de sucesso
                return ResponseEntity.status(HttpStatus.CREATED).body("Nível de acesso criado com sucesso!");
            } else {
                // Retorna um código 400 se falhou ao criar o nível de acesso
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falha ao criar nível de acesso.");
            }
        } catch (Exception e) {
            // Retorna código 500 em caso de erro no servidor
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar nível de acesso: " + e.getMessage());
        }
    }

    @DeleteMapping("/niveis/delete/{nivel}")
    public ResponseEntity<String> excluirNivelAcesso(@PathVariable int nivel ){
        try {
            boolean sucesso = adminNiveisAppService.excluirNivelAcesso(nivel);
            if (sucesso) {
                return ResponseEntity.status(HttpStatus.OK).body("Nível de acesso deletado com sucesso!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falha ao deletar nível de acesso.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar nível de acesso: " + e.getMessage());
        }
    }

    @GetMapping("/niveis")
    public  ResponseEntity<List<NivelAcessoDTO>> listarNiveisAcesso(){
        try {
            NivelAcessoDTO[] niveis = adminNiveisAppService.listarNivelAcesso();
            if (niveis.length == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(List.of());
            }
            return ResponseEntity.status(HttpStatus.OK).body(Arrays.asList(niveis));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
