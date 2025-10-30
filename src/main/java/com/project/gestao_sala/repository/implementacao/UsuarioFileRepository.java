package com.project.gestao_sala.repository.implementacao;

import com.project.gestao_sala.data.FileStorage;
import com.project.gestao_sala.data.Handle;
import com.project.gestao_sala.model.usuario.Usuario;
import com.project.gestao_sala.repository.UsuarioRepository;
import com.project.gestao_sala.serealization.Serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UsuarioFileRepository  implements UsuarioRepository {
    private static final String FILENAME = "niveisAcesso.txt";
    private final FileStorage fileStorage = new FileStorage();
    private Serializer serializer;

    @Override
    public Usuario buscar(String nome) {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Usuario> usuarios = listarTodos();
            return usuarios
                    .stream()
                    .filter(u -> u.getNome() == nome)
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
    public Usuario[] listar() {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Usuario> usuarios = listarTodos();
            return usuarios.toArray(new Usuario[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    @Override
    public void salvar(Usuario u) {
        Handle lockHandle = null;

        try {
            lockHandle = fileStorage.lock(FILENAME);
            //Get todos os niveis
            List<Usuario> allUsuarios = listarTodos();
            //Remove o que ja existe
            allUsuarios.removeIf(usu -> usu.getNome() == u.getNome());
            //Adiciona o novo nivel
            allUsuarios.add(u);

            String contentToSave = allUsuarios.stream()
                    .map(this::serializarUsuario)
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

    private Usuario deserializarUsuario(String linha) {
        try {
            return (Usuario) serializer.deserialize(linha.getBytes(),Usuario.class);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao deserializar linha: " + linha + " - " + e.getMessage());
            return null;
        }
    }

    private String serializarUsuario(Usuario usuario) {
        try {
            return new String(serializer.serialize(usuario));
        } catch (IOException e) {
            System.err.println("Erro ao serializar NivelAcesso " + usuario + " - " + e.getMessage());
            return null;
        }
    }

    private List<Usuario> listarTodos() {
        try {
            byte[] data = fileStorage.read(FILENAME);
            String fileContent = new String(data);
            List<Usuario> allUsuarios = new ArrayList<>();
            if (!fileContent.trim().isEmpty()) {
                allUsuarios = Arrays.stream(fileContent.split("\n"))
                        .filter(line -> !line.trim().isEmpty())
                        .map(this::deserializarUsuario)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                return allUsuarios;
            } else {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
