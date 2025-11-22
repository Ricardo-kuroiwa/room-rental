package com.project.gestao_sala.services;

import com.project.gestao_sala.enums.Permissao;
import com.project.gestao_sala.model.nivelAcesso.NivelAcesso;
import com.project.gestao_sala.model.nivelAcesso.NivelAcessoDTO;
import com.project.gestao_sala.repository.implementacao.NivelAcessoFileRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AdminNiveisAppService {
    private final NivelAcessoFileRepository nivelAcessoFileRepository;

    public AdminNiveisAppService(NivelAcessoFileRepository nivelAcessoFileRepository) {
        this.nivelAcessoFileRepository = nivelAcessoFileRepository;
    }

    public boolean criarNivelAcesso(NivelAcessoDTO dto){
        try{
            NivelAcesso nivelAcesso = new NivelAcesso();
            nivelAcesso.setNivel(dto.nivel());
            nivelAcesso.setPermissoes(dto.permissoes());

            nivelAcessoFileRepository.salvar(nivelAcesso);
            return true;
        }catch (Exception e){
            System.err.println("Erro ao criar nível de acesso: " + e.getMessage());
            return false;
        }
    }
    public boolean atualizarNivelAcesso(int nivel , Permissao[] permissaos){
        try {
            NivelAcesso nivelAcesso = nivelAcessoFileRepository.buscar(nivel);
            if (nivelAcesso == null) {
                return false;
            }

            nivelAcesso.setPermissoes(permissaos);
            nivelAcessoFileRepository.salvar(nivelAcesso);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar nível de acesso: " + e.getMessage());
            return false;
        }
    }
    public NivelAcesso findById(int nivel){
        try{
            return nivelAcessoFileRepository.buscar(nivel);
        } catch (Exception e) {
            System.err.println("Erro ao buscar nível de acesso: " + e.getMessage());
            return null;
        }
    }
    public boolean excluirNivelAcesso(int nivel){
        try{
            return nivelAcessoFileRepository.deletar(nivel);
        }catch (Exception e){
            System.err.println("Erro ao deletar nível de acesso: " + e.getMessage());
            return false;
        }
    }
    public NivelAcessoDTO[] listarNivelAcesso(){
        try{
            NivelAcesso[] niveis = nivelAcessoFileRepository.listar();
            if (niveis == null || niveis.length == 0) {
                return new NivelAcessoDTO[0];  // Nenhum nível encontrado
            }
            return Arrays.stream(niveis)
                    .map(nivel -> new NivelAcessoDTO(nivel.getNivel(), nivel.getPermissoes()))
                    .toArray(NivelAcessoDTO[]::new);
            
        }catch (Exception e){
            System.err.println("Erro ao listar níveis de acesso: " + e.getMessage());
            return new NivelAcessoDTO[0];
        }
    }



}
