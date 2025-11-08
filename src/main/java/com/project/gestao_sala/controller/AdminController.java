package com.project.gestao_sala.controller;

import com.project.gestao_sala.model.categoria.CategoriaDTO;
import com.project.gestao_sala.model.nivelAcesso.NivelAcessoDTO;
import com.project.gestao_sala.model.permissao.AtualizarPermissoesDTO;
import com.project.gestao_sala.model.usuario.UsuarioComumDTO;
import com.project.gestao_sala.model.usuario.UsuarioDeptoDTO;
import com.project.gestao_sala.services.AdminCategoriasAppService;
import com.project.gestao_sala.services.AdminNiveisAppService;
import com.project.gestao_sala.services.AdminUsuariosAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/adm")
public class AdminController {

    private final AdminNiveisAppService adminNiveisAppService;
    private final AdminCategoriasAppService adminCategoriasAppService;
    private final AdminUsuariosAppService adminUsuariosAppService;
    public AdminController(AdminNiveisAppService adminNiveisAppService,
                           AdminCategoriasAppService adminCategoriasAppService,
                           AdminUsuariosAppService adminUsuariosAppService) {
        this.adminNiveisAppService = adminNiveisAppService;
        this.adminCategoriasAppService = adminCategoriasAppService;
        this.adminUsuariosAppService = adminUsuariosAppService;
    }

    // ========== NÍVEIS DE ACESSO ==========

    @PostMapping("/niveis/create")
    public ResponseEntity<String> criarNivelAcesso(@RequestBody NivelAcessoDTO nivelAcessoDTO) {
        boolean sucesso = adminNiveisAppService.criarNivelAcesso(nivelAcessoDTO);
        if (sucesso) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Nível de acesso criado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao criar nível de acesso devido a um erro interno.");
        }
    }

    @PutMapping("/niveis/update/{nivel}")
    public ResponseEntity<String> atualizarNivelAcesso(@PathVariable int nivel,
                                                       @RequestBody AtualizarPermissoesDTO dto) {
        boolean sucesso = adminNiveisAppService.atualizarNivelAcesso(nivel, dto.permissoes());
        if (sucesso) {
            return ResponseEntity.ok("Nível de acesso atualizado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao atualizar nível de acesso. Verifique se o nível existe.");
        }
    }

    @DeleteMapping("/niveis/delete/{nivel}")
    public ResponseEntity<String> excluirNivelAcesso(@PathVariable int nivel) {
        boolean sucesso = adminNiveisAppService.excluirNivelAcesso(nivel);
        if (sucesso) {
            return ResponseEntity.ok("Nível de acesso deletado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao deletar nível de acesso. Verifique se o nível existe.");
        }
    }

    @GetMapping("/niveis")
    public ResponseEntity<List<NivelAcessoDTO>> listarNiveisAcesso() {
        NivelAcessoDTO[] niveis = adminNiveisAppService.listarNivelAcesso();
        return ResponseEntity.ok(Arrays.asList(niveis));
    }

    // ========== CATEGORIAS ==========

    @PostMapping("/categoria/create")
    public ResponseEntity<String> criarCategoria(@RequestBody CategoriaDTO categoriaDTO) {
        boolean sucesso = adminCategoriasAppService.criarCategoria(categoriaDTO);
        if (sucesso) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Categoria criada com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao criar categoria devido a um erro interno.");
        }
    }

    @PutMapping("/categoria/update/{nome}")
    public ResponseEntity<String> atualizarCategoria(@PathVariable String nome,
                                                     @RequestBody CategoriaDTO categoriaDTO) {
        boolean sucesso = adminCategoriasAppService.atualizarCategoria(nome, categoriaDTO.descricao());
        if (sucesso) {
            return ResponseEntity.ok("Categoria atualizada com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao atualizar categoria. Verifique se a categoria existe.");
        }
    }

    @DeleteMapping("/categoria/delete/{nome}")
    public ResponseEntity<String> deletarCategoria(@PathVariable String nome) {
        boolean sucesso = adminCategoriasAppService.deletarCategoria(nome);
        if (sucesso) {
            return ResponseEntity.ok("Categoria deletada com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao deletar categoria. Verifique se a categoria existe.");
        }
    }

    @GetMapping("/categoria")
    public ResponseEntity<List<CategoriaDTO>> listarCategorias() {
        CategoriaDTO[] categorias = adminCategoriasAppService.listarCategorias();
        return ResponseEntity.ok(Arrays.asList(categorias));
    }

    // ========== USUÁRIOS ==========

    @PostMapping("/usuarios/comum/create")
    public ResponseEntity<String> criarUsuarioComum(@RequestBody UsuarioComumDTO dto) {
        boolean sucesso = adminUsuariosAppService.criarUsuarioComum(dto);
        if (sucesso) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuário comum criado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Falha ao criar usuário. Verifique se o nível de acesso " + dto.nivel() + " existe.");
        }
    }

    @PostMapping("/usuarios/depto/create")
    public ResponseEntity<String> criarUsuarioDepto(@RequestBody UsuarioDeptoDTO dto) {
        boolean sucesso = adminUsuariosAppService.criarUsuarioDepto(dto);
        if (sucesso) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuário de departamento criado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Falha ao criar usuário. Verifique se o nível de acesso " + dto.nivel() + " existe.");
        }
    }

    @PutMapping("/usuarios/comum/update/{email}")
    public ResponseEntity<String> atualizarUsuarioComum(@PathVariable String email,
                                                        @RequestBody UsuarioComumDTO dto) {
        boolean sucesso = adminUsuariosAppService.atualizarUsuarioComum(email, dto);
        if (sucesso) {
            return ResponseEntity.ok("Usuário comum atualizado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Falha ao atualizar. Verifique se o usuário existe, é do tipo comum e o nível "
                            + dto.nivel() + " existe.");
        }
    }

    @PutMapping("/usuarios/depto/update/{email}")
    public ResponseEntity<String> atualizarUsuarioDepto(@PathVariable String email,
                                                        @RequestBody UsuarioDeptoDTO dto) {
        boolean sucesso = adminUsuariosAppService.atualizarUsuarioDepto(email, dto);
        if (sucesso) {
            return ResponseEntity.ok("Usuário de departamento atualizado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Falha ao atualizar. Verifique se o usuário existe, é do tipo departamento e o nível "
                            + dto.nivel() + " existe.");
        }
    }

    @DeleteMapping("/usuarios/delete/{email}")
    public ResponseEntity<String> deletarUsuario(@PathVariable String email) {
        boolean sucesso = adminUsuariosAppService.deletarUsuario(email);
        if (sucesso) {
            return ResponseEntity.ok("Usuário deletado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuário não encontrado.");
        }
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Object>> listarUsuarios() {
        Object[] usuarios = adminUsuariosAppService.listarUsuarios();
        return ResponseEntity.ok(Arrays.asList(usuarios));
    }

    @GetMapping("/usuarios/{email}")
    public ResponseEntity<Object> buscarUsuario(@PathVariable String email) {
        Object usuario = adminUsuariosAppService.buscarUsuario(email);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}