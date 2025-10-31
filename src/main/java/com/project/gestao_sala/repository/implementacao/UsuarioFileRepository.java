package com.project.gestao_sala.repository.implementacao;

import com.project.gestao_sala.data.FileStorage;
import com.project.gestao_sala.data.Handle;
import com.project.gestao_sala.model.usuario.Usuario;
import com.project.gestao_sala.model.usuario.UsuarioDepto;
import com.project.gestao_sala.repository.UsuarioRepository;
import com.project.gestao_sala.serealization.Serializer;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UsuarioFileRepository implements UsuarioRepository {
    private static final String FILENAME = "usuarios.txt";
    private final FileStorage fileStorage;
    private final Serializer serializer;

    public UsuarioFileRepository(Serializer serializer) {
        this.serializer = serializer;
        this.fileStorage = new FileStorage();
    }

    @Override
    public Usuario buscar(String email) {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Usuario> usuarios = listarTodos(lockHandle);
            return usuarios.stream()
                    .filter(u -> Objects.equals(u.getEmail(), email))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
            throw new RuntimeException("Falha ao ler o arquivo de usuários.", e);
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
            List<Usuario> usuarios = listarTodos(lockHandle);
            return usuarios.toArray(new Usuario[0]);
        } catch (Exception e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
            return new Usuario[0];
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    @Override
    public boolean salvar(Usuario usuario) {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Usuario> allUsuarios = listarTodos(lockHandle);

            allUsuarios.removeIf(u -> Objects.equals(u.getEmail(), usuario.getEmail()));
            allUsuarios.add(usuario);

            String contentToSave = allUsuarios.stream()
                    .map(this::serializarUsuario)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\n"));

            fileStorage.write(lockHandle, contentToSave.getBytes());
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
            return false;
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    @Override
    public boolean deletar(String email) {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Usuario> allUsuarios = listarTodos(lockHandle);

            boolean removido = allUsuarios.removeIf(u -> Objects.equals(u.getEmail(), email));

            if (!removido) {
                System.err.println("Usuário não encontrado para exclusão: " + email);
                return false;
            }

            String contentToSave = allUsuarios.stream()
                    .map(this::serializarUsuario)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\n"));

            fileStorage.write(lockHandle, contentToSave.getBytes());
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao deletar usuário: " + e.getMessage());
            return false;
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    @Override
    public boolean atualizar(Usuario usuario) {
        return salvar(usuario);
    }

    private String serializarUsuario(Usuario usuario) {
        try {
            String tipo = usuario.getClass().getSimpleName();
            String dados = new String(serializer.serialize(usuario));
            return tipo + "|" + dados;
        } catch (IOException e) {
            System.err.println("Erro ao serializar usuário: " + e.getMessage());
            return null;
        }
    }

    private Usuario deserializarUsuario(String linha) {
        try {
            String[] partes = linha.split("\\|", 2);
            if (partes.length != 2) {
                System.err.println("Formato inválido: " + linha);
                return null;
            }

            String tipo = partes[0];
            byte[] dados = partes[1].getBytes();

            Class<? extends Usuario> classe = switch (tipo) {
                case "Usuario" -> Usuario.class;
                case "UsuarioDepto" -> UsuarioDepto.class;
                default -> {
                    System.err.println("Tipo desconhecido: " + tipo);
                    yield null;
                }
            };

            if (classe == null) {
                return null;
            }

            return (Usuario) serializer.deserialize(dados, classe);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao deserializar usuário: " + e.getMessage());
            return null;
        }
    }

    private List<Usuario> listarTodos(Handle handle) throws IOException {
        byte[] data = fileStorage.read(handle);
        String fileContent = new String(data);

        if (!fileContent.trim().isEmpty()) {
            return Arrays.stream(fileContent.split("\n"))
                    .filter(line -> !line.trim().isEmpty())
                    .map(this::deserializarUsuario)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}