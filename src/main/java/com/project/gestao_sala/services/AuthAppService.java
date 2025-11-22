package com.project.gestao_sala.services;

import com.project.gestao_sala.model.nivelAcesso.NivelAcesso;
import com.project.gestao_sala.model.token.Token;
import com.project.gestao_sala.model.usuario.Usuario;
import com.project.gestao_sala.plataform.TokenGenerator;
import com.project.gestao_sala.repository.UsuarioRepository;
import com.project.gestao_sala.repository.implementacao.AuthFileRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AuthAppService {
    private final UsuarioRepository usuarioRepository;
    private  final AuthFileRepository authFileRepository;
    private  final BCryptPasswordEncoder passwordEncoder;
    private  final TokenGenerator tokenGenerator;
    private final JWTService jwtService;

    public AuthAppService(UsuarioRepository usuarioRepository, AuthFileRepository authFileRepository, TokenGenerator tokenGenerator, JWTService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.authFileRepository = authFileRepository;
        this.tokenGenerator = tokenGenerator;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String autenticar(String email,String senha){
        try{
            Usuario userAlvo  = usuarioRepository.buscar(email);
            if(userAlvo==null){
                System.err.println("Usuario :  " + email + " não encontrado.");
                return null;
            }
            if (!passwordEncoder.matches(senha, userAlvo.getSenhaHash())) {
                System.err.println("Usuario :  " + email + " não encontrado.");
                return null;
            }
            NivelAcesso nivelAcesso = userAlvo.getNivel();
            String[] permissoes = new String[nivelAcesso.getPermissoes().length];
            for (int i = 0; i < nivelAcesso.getPermissoes().length; i++) {
                permissoes[i] = nivelAcesso.getPermissoes()[i].name(); // Convertendo o enum para string
            }
            return jwtService.gerarToken(userAlvo.getEmail(),userAlvo.getNome(), permissoes);


        } catch (Exception e) {
            System.err.println("Erro ao autenticar usuario: " + e.getMessage());
            return null;
        }
    }

    public boolean enviarTokenRecuperacao(String email){
        try{
            String token  = tokenGenerator.generateToke();
            System.out.println("Token Gerado: " + token );
            Token token1 =new Token();
            token1.setToken(token);
            token1.setEmail(email);
            token1.setDataCriacao(LocalDateTime.now());
            authFileRepository.salvar(token1);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao enviar token: " + e.getMessage());
            return false;
        }
    }

    public boolean recuperarSenha(String token,String email,String novaSenha){
        try{
            Token tokenTarget  =  authFileRepository.buscar(token);
            if (tokenTarget == null){
                System.err.println("Erro ao recuperar Senha: token invalido ");
                return false;
            }
            if(!Objects.equals(tokenTarget.getEmail(), email)){
                System.err.println("Erro ao recuperar Senha: email não bate" );
                return false;
            }
            Usuario usuarioTarget = usuarioRepository.buscar(email);
            if (usuarioTarget== null){
                System.err.println("Erro ao recuperar Senha: usuario nao existe");
                return false;
            }
            String novaSenhaHash = passwordEncoder.encode(novaSenha);
            usuarioTarget.setSenhaHash(novaSenhaHash);
            boolean ok = usuarioRepository.atualizar(usuarioTarget);

            if (ok) {
                authFileRepository.deletar(tokenTarget.getToken());
            }
            return ok;

        } catch (Exception e) {
            System.err.println("Erro ao recuperar Senha: " + e.getMessage());
            return false;
        }
    }
}
