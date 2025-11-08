package com.project.gestao_sala.config;

import com.project.gestao_sala.enums.Permissao;
import com.project.gestao_sala.model.nivelAcesso.NivelAcesso;
import com.project.gestao_sala.model.nivelAcesso.NivelAcessoDTO;
import com.project.gestao_sala.model.usuario.Usuario;
import com.project.gestao_sala.model.usuario.UsuarioComumDTO;
import com.project.gestao_sala.repository.UsuarioRepository;
import com.project.gestao_sala.services.AdminNiveisAppService;
import com.project.gestao_sala.services.AdminUsuariosAppService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class databaseSeeder implements CommandLineRunner {
    private final AdminUsuariosAppService adminUsuariosAppService;
    private final AdminNiveisAppService adminNiveisAppService;
    private  final BCryptPasswordEncoder passwordEncoder;
    public databaseSeeder( AdminUsuariosAppService adminUsuariosAppService, AdminNiveisAppService adminNiveisAppService) {

        this.adminUsuariosAppService = adminUsuariosAppService;
        this.adminNiveisAppService = adminNiveisAppService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void run(String... args) throws Exception {
        seedUsuarios();
    }
    private void seedUsuarios(){

        Usuario usuario = new Usuario();
        NivelAcesso nivelAcesso = new NivelAcesso();
        Permissao[] permissaos1 = Permissao.values();
        Permissao[] permissaos2 = {Permissao.VIZUALIZAR_ESPACOS,Permissao.RESERVAR_ESPACOS,Permissao.CADASTEAR_ESPACOS};
        Permissao[] permissaos3 = {Permissao.CADASTRAR_USUARIOS};
        NivelAcessoDTO nivel1 = new NivelAcessoDTO(
            1,permissaos1
        );
        NivelAcessoDTO nivel2 = new NivelAcessoDTO(
                2,permissaos2
        );
        NivelAcessoDTO nivel3 = new NivelAcessoDTO(
                3,permissaos3
        );
        adminNiveisAppService.criarNivelAcesso(nivel1);
        adminNiveisAppService.criarNivelAcesso(nivel2);
        adminNiveisAppService.criarNivelAcesso(nivel3);

        UsuarioComumDTO usuarioComumDTO = new UsuarioComumDTO(
                "João Silva",
                "joao.silva@empresa.com",
                "senhaSegura",
                1,
                999887766
        );
        adminUsuariosAppService.criarUsuarioComum(usuarioComumDTO);

    }
}
