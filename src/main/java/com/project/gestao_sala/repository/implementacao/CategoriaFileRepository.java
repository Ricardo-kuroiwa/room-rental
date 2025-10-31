package com.project.gestao_sala.repository.implementacao;

import com.project.gestao_sala.data.FileStorage;
import com.project.gestao_sala.data.Handle;
import com.project.gestao_sala.model.categoria.Categoria;
import com.project.gestao_sala.repository.CategoriaRepository;
import com.project.gestao_sala.serealization.Serializer;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CategoriaFileRepository implements CategoriaRepository {
    private static final String FILENAME = "categoria.txt";
    private final FileStorage fileStorage;
    private final Serializer serializer;

    public CategoriaFileRepository(Serializer serializer) {
        this.serializer = serializer;
        this.fileStorage = new FileStorage();
    }

    @Override
    public Categoria buscar(String nome) {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Categoria> categorias = listarTodos(lockHandle);
            return categorias
                    .stream()
                    .filter(ca -> Objects.equals(ca.getNome(), nome))
                    .findFirst().orElse(null);
        } catch (Exception e) {
            System.err.println("Erro ao buscar categoria: " + e.getMessage());
            throw new RuntimeException("Falha ao ler o arquivo de categorias.", e);
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
            List<Categoria> categorias = listarTodos(lockHandle);
            return categorias.toArray(new Categoria[0]);
        } catch (Exception e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
            return new Categoria[0];
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    @Override
    public boolean salvar(Categoria c) {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Categoria> allCategorias = listarTodos(lockHandle);

            allCategorias.removeIf(categoriaExistente -> Objects.equals(categoriaExistente.getNome(), c.getNome()));
            allCategorias.add(c);

            String contentToSave = allCategorias.stream()
                    .map(this::serializarCategoria)
                    .collect(Collectors.joining("\n"));

            fileStorage.write(lockHandle, contentToSave.getBytes());

            return true;
        } catch (Exception e) {
            System.err.println("Erro ao salvar categoria: " + e.getMessage());
            return false;
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    @Override
    public boolean deletar(String nome) {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Categoria> allCategorias = listarTodos(lockHandle);

            boolean removido = allCategorias.removeIf(ca -> Objects.equals(ca.getNome(), nome));

            if (!removido) {
                System.err.println("Categoria não encontrada para exclusão: " + nome);
                return false;
            }

            String contentToSave = allCategorias.stream()
                    .map(this::serializarCategoria)
                    .collect(Collectors.joining("\n"));

            fileStorage.write(lockHandle, contentToSave.getBytes());

            return true;
        } catch (Exception e) {
            System.err.println("Erro ao deletar categoria: " + e.getMessage());
            return false;
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    @Override
    public boolean atualizar(Categoria c) {
        return salvar(c);
    }

    private Categoria deserializarCategoria(String linha) {
        try {
            return (Categoria) serializer.deserialize(linha.getBytes(), Categoria.class);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao deserializar linha: " + linha + " - " + e.getMessage());
            return null;
        }
    }

    private String serializarCategoria(Categoria categoria) {
        try {
            return new String(serializer.serialize(categoria));
        } catch (IOException e) {
            System.err.println("Erro ao serializar Categoria " + categoria + " - " + e.getMessage());
            return null;
        }
    }

    private List<Categoria> listarTodos(Handle handle) throws IOException {
        byte[] data = fileStorage.read(handle);
        String fileContent = new String(data);
        if (!fileContent.trim().isEmpty()) {
            return Arrays.stream(fileContent.split("\n"))
                    .filter(line -> !line.trim().isEmpty())
                    .map(this::deserializarCategoria)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}