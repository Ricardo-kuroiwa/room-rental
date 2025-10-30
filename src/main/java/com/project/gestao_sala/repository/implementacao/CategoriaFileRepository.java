package com.project.gestao_sala.repository.implementacao;

import com.project.gestao_sala.data.FileStorage;
import com.project.gestao_sala.data.Handle;
import com.project.gestao_sala.model.categoria.Categoria;
import com.project.gestao_sala.model.nivelAcesso.NivelAcesso;
import com.project.gestao_sala.repository.CategoriaRepository;
import com.project.gestao_sala.serealization.Serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CategoriaFileRepository implements CategoriaRepository {
    private static final String FILENAME = "categoria.txt";
    private final FileStorage fileStorage = new FileStorage();
    private Serializer serializer;

    @Override
    public Categoria buscar(String nome) {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Categoria> categorias = listarTodos();
            return categorias
                    .stream()
                    .filter(ca -> ca.getNome() == nome)
                    .findFirst().orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    @Override
    public Categoria[] listar() {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Categoria> categorias = listarTodos();
            return categorias.toArray(new Categoria[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    @Override
    public void salvar(Categoria c) {
        Handle lockHandle = null;

        try {
            lockHandle = fileStorage.lock(FILENAME);
            //Get todos os niveis
            List<Categoria> allCategorias = listarTodos();
            //Remove o que ja existe
            allCategorias.removeIf(ca -> ca.getNome() == ca.getNome());
            //Adiciona o novo nivel
            allCategorias.add(c);

            String contentToSave = allCategorias.stream()
                    .map(this::serializarNivelCategoria)
                    .collect(Collectors.joining("\n"));

            fileStorage.write(FILENAME, contentToSave.getBytes());
            fileStorage.unlock(lockHandle);
            lockHandle = null;


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            System.err.println("Erro ao adquirir lock ou serializar NivelAcesso: " + e.getMessage());
        }finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    private Categoria deserializarCategoria(String linha) {
        try {
            return (Categoria) serializer.deserialize(linha.getBytes(),Categoria.class);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao deserializar linha: " + linha + " - " + e.getMessage());
            return null;
        }
    }

    private String serializarNivelCategoria(Categoria categoria) {
        try {
            return new String(serializer.serialize(categoria));
        } catch (IOException e) {
            System.err.println("Erro ao serializar NivelAcesso " + categoria + " - " + e.getMessage());
            return null;
        }
    }

    private List<Categoria> listarTodos() {
        try {
            byte[] data = fileStorage.read(FILENAME);
            String fileContent = new String(data);
            List<Categoria> allCategorias = new ArrayList<>();
            if (!fileContent.trim().isEmpty()) {
                allCategorias = Arrays.stream(fileContent.split("\n"))
                        .filter(line -> !line.trim().isEmpty())
                        .map(this::deserializarCategoria)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                return allCategorias;
            } else {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
