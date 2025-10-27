package com.project.gestao_sala.repository.implementacao;

import com.project.gestao_sala.data.FileStorage;
import com.project.gestao_sala.data.Handle;
import com.project.gestao_sala.model.nivelAcesso.NivelAcesso;
import com.project.gestao_sala.repository.NivelAcessoRepository;
import com.project.gestao_sala.serealization.Serializer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class NivelAcessoFileRepository implements NivelAcessoRepository {
    private static final String FILENAME = "niveisAcesso.txt";
    private final FileStorage fileStorage = new FileStorage();
    private Serializer serializer;

    @Override
    public NivelAcesso buscar(int nivel) {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<NivelAcesso> niveis = listarTodos();
            return niveis
                    .stream()
                    .filter(na -> na.getNivel() == nivel)
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
    public NivelAcesso[] listar() {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<NivelAcesso> niveis = listarTodos();
            return niveis.toArray(new NivelAcesso[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(FILENAME);
            }
        }
    }

    @Override
    public void salvar(NivelAcesso n) {
        Handle lockHandle = null;

        try {
            lockHandle = fileStorage.lock(FILENAME);
            //Get todos os niveis
            List<NivelAcesso> allNiveis = listarTodos();
            //Remove o que ja existe
            allNiveis.removeIf(na -> na.getNivel() == n.getNivel());
            //Adiciona o novo nivel
            allNiveis.add(n);

            String contentToSave = allNiveis.stream()
                    .map(this::serializarNivelAcesso)
                    .collect(Collectors.joining("\n"));

            fileStorage.write(FILENAME, contentToSave.getBytes());
            fileStorage.unlock(FILENAME);
            lockHandle = null;


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            System.err.println("Erro ao adquirir lock ou serializar NivelAcesso: " + e.getMessage());
        }
    }


    private NivelAcesso deserializarNivelAcesso(String linha) {
        try {
            return (NivelAcesso) serializer.deserialize(linha.getBytes());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao deserializar linha: " + linha + " - " + e.getMessage());
            return null;
        }
    }

    private String serializarNivelAcesso(NivelAcesso nivelAcesso) {
        try {
            return new String(serializer.serialize(nivelAcesso));
        } catch (IOException e) {
            System.err.println("Erro ao serializar NivelAcesso " + nivelAcesso + " - " + e.getMessage());
            return null;
        }
    }

    private List<NivelAcesso> listarTodos() {
        try {
            byte[] data = fileStorage.read(FILENAME);
            String fileContent = new String(data);
            List<NivelAcesso> allNiveis = new ArrayList<>();
            if (!fileContent.trim().isEmpty()) {
                allNiveis = Arrays.stream(fileContent.split("\n"))
                        .filter(line -> !line.trim().isEmpty())
                        .map(this::deserializarNivelAcesso)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                return allNiveis;
            } else {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
