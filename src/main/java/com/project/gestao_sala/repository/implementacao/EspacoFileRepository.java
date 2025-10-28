package com.project.gestao_sala.repository.implementacao;

import com.project.gestao_sala.data.FileStorage;
import com.project.gestao_sala.data.Handle;
import com.project.gestao_sala.model.espaco.Espaco;
import com.project.gestao_sala.repository.EspacoRepository;
import com.project.gestao_sala.serealization.Serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EspacoFileRepository implements EspacoRepository {
    private static final String FILENAME = "espacos.txt";
    private final FileStorage fileStorage = new FileStorage();
    private Serializer serializer;

    @Override
    public Espaco buscar(char codigo) {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Espaco> espacos = listarTodos();
            return espacos
                    .stream()
                    .filter(espaco -> espaco.getCodigo() == codigo)
                    .findFirst().orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(FILENAME);
            }
        }
    }

    @Override
    public Espaco[] listar() {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Espaco> espacos = listarTodos();
            return espacos.toArray(new Espaco[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(FILENAME);
            }
        }
    }

    @Override
    public void salvar(Espaco e) {
        Handle lockHandle = null;

        try {
            lockHandle = fileStorage.lock(FILENAME);
            //Get todos os niveis
            List<Espaco> allEspacos = listarTodos();
            //Remove o que ja existe
            allEspacos.removeIf(es -> es.getCodigo() == e.getCodigo());
            //Adiciona o novo nivel
            allEspacos.add(e);

            String contentToSave = allEspacos.stream()
                    .map(this::serializarEspaco)
                    .collect(Collectors.joining("\n"));

            fileStorage.write(FILENAME, contentToSave.getBytes());
            fileStorage.unlock(FILENAME);
            lockHandle = null;


        } catch (IOException exception) {
            throw new RuntimeException(exception);
        } catch (RuntimeException exception) {
            System.err.println("Erro ao adquirir lock ou serializar NivelAcesso: " + exception.getMessage());
        }finally {
            if (lockHandle != null) {
                fileStorage.unlock(FILENAME);
            }
        }
    }

    private Espaco deserializarEspaco(String linha) {
        try {
            return (Espaco) serializer.deserialize(linha.getBytes());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao deserializar linha: " + linha + " - " + e.getMessage());
            return null;
        }
    }

    private String serializarEspaco(Espaco espaco) {
        try {
            return new String(serializer.serialize(espaco));
        } catch (IOException e) {
            System.err.println("Erro ao serializar NivelAcesso " + espaco + " - " + e.getMessage());
            return null;
        }
    }

    private List<Espaco> listarTodos() {
        try {
            byte[] data = fileStorage.read(FILENAME);
            String fileContent = new String(data);
            List<Espaco> allEspacos = new ArrayList<>();
            if (!fileContent.trim().isEmpty()) {
                allEspacos = Arrays.stream(fileContent.split("\n"))
                        .filter(line -> !line.trim().isEmpty())
                        .map(this::deserializarEspaco)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                return allEspacos;
            } else {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
