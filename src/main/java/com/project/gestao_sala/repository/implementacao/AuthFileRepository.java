package com.project.gestao_sala.repository.implementacao;

import com.project.gestao_sala.data.FileStorage;
import com.project.gestao_sala.data.Handle;
import com.project.gestao_sala.model.categoria.Categoria;
import com.project.gestao_sala.model.token.Token;
import com.project.gestao_sala.serealization.Serializer;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class AuthFileRepository {
    private static final String FILENAME = "token.txt";
    private final FileStorage fileStorage;
    private final Serializer serializer;

    public AuthFileRepository( Serializer serializer) {
        this.fileStorage = new FileStorage();
        this.serializer = serializer;
    }

    public Token buscar (String token){
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Token> tokens = listarTodos(lockHandle);
            return tokens
                    .stream()
                    .filter(token1 -> Objects.equals(token1.getToken(), token))
                    .findFirst().orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }
    public boolean salvar(Token token){
        Handle lockHandle = null;

        try {
            lockHandle = fileStorage.lock(FILENAME);
            //Get todos os niveis
            List<Token> allToken = listarTodos(lockHandle);
            //Remove o que ja existe
            allToken.removeIf(tk -> Objects.equals(tk.getToken(), token.getToken()));
            //Adiciona o novo nivel
            allToken.add(0,token);

            String contentToSave = allToken.stream()
                    .map(this::serializarToken)
                    .collect(Collectors.joining("\n"));

            fileStorage.write(lockHandle, contentToSave.getBytes());
            return true;

        } catch (Exception exception) {
            System.err.println("Erro inesperado ao Salvar Categoria: " + exception.getMessage());
            return false;
        }finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }
    public boolean deletar(String token) {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Token> allTokens = listarTodos(lockHandle);

            boolean removido = allTokens.removeIf(tk -> Objects.equals(tk.getToken(),token));

            if (!removido) {
                System.err.println("Token não encontrada para exclusão: " + token);
                return false;
            }

            String contentToSave = allTokens.stream()
                    .map(this::serializarToken)
                    .collect(Collectors.joining("\n"));

            fileStorage.write(lockHandle, contentToSave.getBytes());

            return true;
        } catch (Exception e) {
            System.err.println("Erro ao deletar token: " + e.getMessage());
            return false;
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }
    private Token deserializarToken(String linha) {
        try {
            return (Token) serializer.deserialize(linha.getBytes(),Token.class);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao deserializar linha: " + linha + " - " + e.getMessage());
            return null;
        }
    }

    private String serializarToken(Token token) {
        try {
            return new String(serializer.serialize(token));
        } catch (IOException e) {
            System.err.println("Erro ao serializar Token " + token + " - " + e.getMessage());
            return null;
        }
    }

    private List<Token> listarTodos(Handle handle) throws IOException {
        byte[] data = fileStorage.read(handle);
        String fileContent = new String(data);

        if (!fileContent.trim().isEmpty()) {
            return Arrays.stream(fileContent.split("\n"))
                    .filter(line -> !line.trim().isEmpty())
                    .map(this::deserializarToken)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
