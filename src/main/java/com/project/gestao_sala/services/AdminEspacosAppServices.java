package com.project.gestao_sala.services;

import com.project.gestao_sala.model.categoria.Categoria;
import com.project.gestao_sala.model.espaco.Espaco;
import com.project.gestao_sala.model.espaco.EspacoDTO;
import com.project.gestao_sala.model.nivelAcesso.NivelAcesso;
import com.project.gestao_sala.model.usuario.Usuario;
import com.project.gestao_sala.model.usuario.UsuarioDepto;
import com.project.gestao_sala.repository.NivelAcessoRepository;
import com.project.gestao_sala.repository.implementacao.CategoriaFileRepository;
import com.project.gestao_sala.repository.implementacao.EspacoFileRepository;
import com.project.gestao_sala.repository.implementacao.NivelAcessoFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Service
public class AdminEspacosAppServices {
    private  final EspacoFileRepository espacoFileRepository;
    private final CategoriaFileRepository categoriaFileRepository;
    private final NivelAcessoFileRepository nivelAcessoFileRepository;

    public AdminEspacosAppServices(EspacoFileRepository espacoFileRepository, CategoriaFileRepository categoriaFileRepository, NivelAcessoFileRepository nivelAcessoFileRepository) {
        this.espacoFileRepository = espacoFileRepository;
        this.categoriaFileRepository = categoriaFileRepository;
        this.nivelAcessoFileRepository = nivelAcessoFileRepository;
    }

    public boolean criarEspaco(EspacoDTO dto){
        try{
            Espaco espaco =  new Espaco();
            espaco.setCodigo(dto.codigo());
            espaco.setNome(dto.nome());
            espaco.setTipo(dto.tipo());
            espaco.setPredio(dto.predio());
            espaco.setCapacidade(dto.capacidade());
            espaco.setAcesso(dto.acesso());
            espaco.setAtivo(dto.ativo());
            Categoria categoriaAlvo =categoriaFileRepository.buscar(dto.categoriaNome());
            if (categoriaAlvo==null){
                System.err.println("Erro ao criar espaco: " );
                return false;
            }
            espaco.setCategoria(categoriaAlvo);

            espacoFileRepository.salvar(espaco);
            return true;
        }catch (Exception e){
            System.err.println("Erro ao criar espaco: " + e.getMessage());
            return false;
        }
    }
    public Espaco findByCodigo(Character codigo){

        try{
            return espacoFileRepository.buscar(codigo);
        }catch (Exception e ){
            return null;
        }
    }
    public boolean excluirEspaco(char codigo){
        try {
            return espacoFileRepository.deletar(codigo);
        }catch (Exception e){
            System.err.println("Erro ao excluir espaco: " + e.getMessage());
            return false;
        }
    }
    public boolean atualizarEspaco(EspacoDTO dto){
        try {
            Espaco  espaco= espacoFileRepository.buscar(dto.codigo());
            if (espaco == null) {
                return false;
            }

            espaco.setNome(dto.nome());
            Categoria categoria = categoriaFileRepository.buscar(dto.categoriaNome());
            espaco.setCategoria(categoria);
            espaco.setTipo(dto.tipo());
            espaco.setPredio(dto.predio());
            espaco.setCapacidade(dto.capacidade());
            espaco.setAcesso(dto.acesso());
            espaco.setAtivo(dto.ativo());

            return espacoFileRepository.atualizar(espaco);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            return false;
        }
    }
    public EspacoDTO[] listarEspaco(){
        try{
            Espaco[] espacos =espacoFileRepository.listar();
            if (espacos == null || espacos.length == 0) {
                return new EspacoDTO[0];  // Nenhum nível encontrado
            }
            return Arrays.stream(espacos)
                    .map(espaco -> new EspacoDTO(
                            espaco.getCodigo(),
                            espaco.getNome(),
                            espaco.getTipo(),
                            espaco.getPredio(),
                            espaco.getCapacidade(),
                            espaco.getAcesso(),
                            espaco.isAtivo(),
                            espaco.getCategoria().getNome()
                    )).toArray(EspacoDTO[]::new);

        }catch (Exception e){
            System.err.println("Erro ao listar níveis de acesso: " + e.getMessage());
            return new EspacoDTO[0];
        }
    }
}
