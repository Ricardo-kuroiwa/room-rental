package com.project.gestao_sala.services;

import com.project.gestao_sala.model.nivelAcesso.NivelAcesso;
import com.project.gestao_sala.model.nivelAcesso.NivelAcessoDTO;
import com.project.gestao_sala.model.usuario.*;
import com.project.gestao_sala.repository.NivelAcessoRepository;
import com.project.gestao_sala.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Service
public class AdminUsuariosAppService {
    private final UsuarioRepository usuarioRepository;
    private final NivelAcessoRepository nivelAcessoRepository;
    private  final BCryptPasswordEncoder passwordEncoder;
    public AdminUsuariosAppService(UsuarioRepository usuarioRepository,
                                   NivelAcessoRepository nivelAcessoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.nivelAcessoRepository = nivelAcessoRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public boolean criarUsuarioComum(UsuarioComumDTO dto) {
        // Busca o NivelAcesso completo pelo ID
        NivelAcesso nivel = nivelAcessoRepository.buscar(dto.nivel());
        if (nivel == null) {
            System.err.println("Nível de acesso " + dto.nivel() + " não encontrado.");
            return false;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenhaHash( passwordEncoder.encode( dto.senhaHash()) );
        usuario.setNivel(nivel);  // Atribui o objeto completo
        usuario.setTelefone(dto.telefone());
        return usuarioRepository.salvar(usuario);
    }

    public boolean criarUsuarioDepto(UsuarioDeptoDTO dto) {
        // Busca o NivelAcesso completo pelo ID
        NivelAcesso nivel = nivelAcessoRepository.buscar(dto.nivel());
        if (nivel == null) {
            System.err.println("Nível de acesso " + dto.nivel()+ " não encontrado.");
            return false;
        }

        UsuarioDepto usuario = new UsuarioDepto();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenhaHash(passwordEncoder.encode(dto.senhaHash()));
        usuario.setNivel(nivel);
        usuario.setTelefone(dto.telefone());
        usuario.setRamal(dto.ramal());
        return usuarioRepository.salvar(usuario);
    }

    public boolean atualizarUsuarioComum(String email, UsuarioComumDTO dto) {
        try {
            Usuario usuario = usuarioRepository.buscar(email);
            if (usuario == null || usuario instanceof UsuarioDepto) {
                return false;
            }

            // Busca o novo nível
            NivelAcesso nivel = nivelAcessoRepository.buscar(dto.nivel());
            if (nivel == null) {
                System.err.println("Nível de acesso " + dto.nivel() + " não encontrado.");
                return false;
            }

            usuario.setNome(dto.nome());
            if (StringUtils.hasText(dto.senhaHash())) {
                usuario.setSenhaHash(passwordEncoder.encode(dto.senhaHash()));
            }
            usuario.setNivel(nivel);
            usuario.setTelefone(dto.telefone());
            return usuarioRepository.atualizar(usuario);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarUsuarioDepto(String email, UsuarioDeptoDTO dto) {
        try {
            Usuario usuario = usuarioRepository.buscar(email);
            if (!(usuario instanceof UsuarioDepto)) {
                return false;
            }

            // Busca o novo nível
            NivelAcesso nivel = nivelAcessoRepository.buscar(dto.nivel());
            if (nivel == null) {
                System.err.println("Nível de acesso " + dto.nivel() + " não encontrado.");
                return false;
            }

            UsuarioDepto usuarioDepto = (UsuarioDepto) usuario;
            usuarioDepto.setNome(dto.nome());
            if (StringUtils.hasText(dto.senhaHash())) {
                usuarioDepto.setSenhaHash(passwordEncoder.encode(dto.senhaHash()));
            }
            usuarioDepto.setNivel(nivel);
            usuarioDepto.setTelefone(dto.telefone());
            usuarioDepto.setRamal(dto.ramal());
            return usuarioRepository.atualizar(usuarioDepto);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar usuário de departamento: " + e.getMessage());
            return false;
        }
    }

    public boolean deletarUsuario(String email) {
        return usuarioRepository.deletar(email);
    }

    // Retorna DTOs de resposta com dados completos do nível
    public Object[] listarUsuarios() {
        try {
            Usuario[] usuarios = usuarioRepository.listar();
            if (usuarios == null || usuarios.length == 0) {
                return new Object[0];
            }
            return Arrays.stream(usuarios)
                    .map(this::converterParaResponseDTO)
                    .toArray(Object[]::new);
        } catch (Exception e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
            return new Object[0];
        }
    }

    public Object buscarUsuario(String email) {
        try {
            Usuario usuario = usuarioRepository.buscar(email);
            if (usuario == null) {
                return null;
            }
            return converterParaResponseDTO(usuario);
        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
            return null;
        }
    }

    // Converte para DTO de resposta (com dados completos do nível)
    private Object converterParaResponseDTO(Usuario usuario) {
        NivelAcessoDTO nivelDTO = new NivelAcessoDTO(
                usuario.getNivel().getNivel(),
                usuario.getNivel().getPermissoes()
        );

        if (usuario instanceof UsuarioDepto depto) {
            return new UsuarioDeptoResponseDTO(
                    depto.getNome(),
                    depto.getEmail(),
                    depto.getTelefone(),
                    depto.getRamal(),
                    nivelDTO
            );
        } else {
            return new UsuarioComumResponseDTO(
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getTelefone(),
                    nivelDTO
            );
        }
    }
}