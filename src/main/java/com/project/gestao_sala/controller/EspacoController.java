package com.project.gestao_sala.controller;

import com.project.gestao_sala.model.espaco.Espaco;
import com.project.gestao_sala.model.espaco.EspacoDTO;
import com.project.gestao_sala.model.espaco.EspacoFiltroDTO;
import com.project.gestao_sala.model.permissao.AtualizarPermissoesDTO;
import com.project.gestao_sala.services.AdminEspacosAppServices;
import com.project.gestao_sala.services.PesquisarAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/espaco")
public class EspacoController {
    private final AdminEspacosAppServices adminEspacosAppServices;
    private final  PesquisarAppService pesquisarAppService;

    public EspacoController(AdminEspacosAppServices adminEspacosAppServices, PesquisarAppService pesquisarAppService) {
        this.adminEspacosAppServices = adminEspacosAppServices;
        this.pesquisarAppService = pesquisarAppService;
    }
    @PostMapping("/create")
    public ResponseEntity<String> criarEspaco(@RequestBody  EspacoDTO dto){
        boolean sucesso = adminEspacosAppServices.criarEspaco(dto);
        if (sucesso) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Espaco criada com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao criar espaco devido a um erro interno.");
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<Object>> listarEspacos(){
        Object[] espacos = adminEspacosAppServices.listarEspaco();
        return ResponseEntity.ok(Arrays.asList(espacos));
    }
    @DeleteMapping("/delete/{codigo}")
    public ResponseEntity<String> deletarEspaco(@PathVariable char codigo){
        boolean sucesso = adminEspacosAppServices.excluirEspaco(codigo);
        if (sucesso) {
            return ResponseEntity.status(HttpStatus.OK).body("Espaco deletado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao deletar espaco devido a um erro interno.");
        }
    }
    @PutMapping("/update")
    public ResponseEntity<String> atualizarEspaco(@RequestBody EspacoDTO dto) {
        boolean sucesso = adminEspacosAppServices.atualizarEspaco(dto);
        if (sucesso) {
            return ResponseEntity.ok("Espaco atualizado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao atualizar espaco. Verifique se o espaco existe.");
        }
    }
    @GetMapping
    public ResponseEntity<List<Espaco>> pesquisarEspacos(@ModelAttribute EspacoFiltroDTO filtros) {
        List<Espaco> resultado = pesquisarAppService.pesquisar(filtros);
        return ResponseEntity.ok(resultado);
    }
}
