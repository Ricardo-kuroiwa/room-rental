package com.project.gestao_sala.services;

import com.project.gestao_sala.model.categoria.Categoria;
import com.project.gestao_sala.model.categoria.CategoriaDTO;
import com.project.gestao_sala.repository.CategoriaRepository;
import com.project.gestao_sala.repository.implementacao.CategoriaFileRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

@Service
public class AdminCategoriasAppService {
    private final CategoriaFileRepository categoriaRepository;

    public AdminCategoriasAppService(CategoriaFileRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public boolean criarCategoria(CategoriaDTO dto){
        try{
            Categoria categoria = new Categoria();
            categoria.setNome(dto.nome());
            categoria.setDescricao(dto.descricao());
            categoriaRepository.salvar(categoria);
            return true;
        }catch (Exception e){
            System.err.println("Erro ao criar categoria : "+e.getMessage());
            return false;
        }
    }
    public boolean atualizarCategoria(String nome,String descricao){
        try{
            Categoria categoria = categoriaRepository.buscar(nome);
            if (categoria==null){
                return false;

            }
            categoria.setDescricao(descricao);
            categoriaRepository.salvar(categoria);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar categoria: " + e.getMessage());
            return false;
        }
    }
    public boolean deletarCategoria(String nome){
        try{
            return categoriaRepository.deletar(nome);
        }catch (Exception e){
            System.err.println("Erro ao excluir categoria " + e.getMessage());
            return false;
        }
    }
    public CategoriaDTO[] listarCategorias(){
        try {
            Categoria[] categorias = categoriaRepository.listar();
            if(categorias==null || categorias.length==0){
                return new CategoriaDTO[0];
            }
            return Arrays.stream(categorias)
                    .map(categoria -> new CategoriaDTO(categoria.getNome(),categoria.getDescricao()))
                    .toArray(CategoriaDTO[]::new);
        } catch (Exception e) {
            System.err.println("Erro ao categorias: " + e.getMessage());
            return new CategoriaDTO[0];
        }
    }

}
