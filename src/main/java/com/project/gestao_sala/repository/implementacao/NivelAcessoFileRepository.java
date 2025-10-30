package com.project.gestao_sala.repository.implementacao;

import com.project.gestao_sala.data.FileStorage;
import com.project.gestao_sala.data.Handle;
import com.project.gestao_sala.model.nivelAcesso.NivelAcesso;
import com.project.gestao_sala.repository.NivelAcessoRepository;
import com.project.gestao_sala.serealization.Serializer;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class NivelAcessoFileRepository implements NivelAcessoRepository {
    private static final String FILENAME = "niveisAcesso.txt";
    private final FileStorage fileStorage;
    private final Serializer serializer;

    public NivelAcessoFileRepository(Serializer serializer) {
        this.serializer = serializer;
        this.fileStorage = new FileStorage();
    }

    @Override
    public NivelAcesso buscar(int nivel) {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<NivelAcesso> niveis = listarTodos(lockHandle);
            return niveis
                    .stream()
                    .filter(na -> na.getNivel() == nivel)
                    .findFirst().orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar NivelAcesso", e);
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    @Override
    public NivelAcesso[] listar() {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<NivelAcesso> niveis = listarTodos(lockHandle);
            return niveis.toArray(new NivelAcesso[0]);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar Niveis de Acesso", e);
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    @Override
    public void salvar(NivelAcesso n) {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<NivelAcesso> allNiveis = listarTodos(lockHandle);

            allNiveis.removeIf(na -> na.getNivel() == n.getNivel());
            allNiveis.add(n);

            allNiveis.sort(Comparator.comparingInt(NivelAcesso::getNivel));

            String contentToSave = allNiveis.stream()
                    .map(this::serializarNivelAcesso)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\n"));

            fileStorage.write(lockHandle, contentToSave.getBytes());

        } catch (IOException e) {
            throw new RuntimeException("Erro de IO ao salvar NivelAcesso", e);
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao salvar NivelAcesso: " + e.getMessage(), e);
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    @Override
    public void deletar(int nivel){
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<NivelAcesso> niveis = listarTodos(lockHandle);

            boolean removido = niveis.removeIf(na -> na.getNivel() == nivel);

            if (!removido) {
                throw new RuntimeException("Nível de acesso não encontrado para exclusão: " + nivel);
            }

            String contentToSave = niveis.stream()
                    .map(this::serializarNivelAcesso)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\n"));

            fileStorage.write(lockHandle, contentToSave.getBytes());

        } catch (IOException e) {
            throw new RuntimeException("Erro de IO ao deletar NivelAcesso", e);
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao deletar NivelAcesso: " + e.getMessage(), e);
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    @Override
    public void atualizar(NivelAcesso n){
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<NivelAcesso> niveis = listarTodos(lockHandle);

            Optional<NivelAcesso> existente = Optional.ofNullable(buscar(n.getNivel()));

            if (existente.isPresent()) {
                niveis.removeIf(na -> na.getNivel() == n.getNivel());
                niveis.add(n);

                niveis.sort(Comparator.comparingInt(NivelAcesso::getNivel));

                String contentToSave = niveis.stream()
                        .map(this::serializarNivelAcesso)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining("\n"));

                fileStorage.write(lockHandle, contentToSave.getBytes());
            } else {
                throw new RuntimeException("Nível de acesso não encontrado para atualização: " + n.getNivel());
            } } catch (IOException e) {
            throw new RuntimeException("Erro de IO ao atualizar NivelAcesso", e);
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao atualizar NivelAcesso: " + e.getMessage(), e);
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    private List<NivelAcesso> listarTodos(Handle handle) throws IOException {
        byte[] data = fileStorage.read(handle);
        String fileContent = new String(data);

        if (!fileContent.trim().isEmpty()) {
            return Arrays.stream(fileContent.split("\n"))
                    .filter(line -> !line.trim().isEmpty())
                    .map(this::deserializarNivelAcesso)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private String serializarNivelAcesso(NivelAcesso nivelAcesso) {
        try {
            byte[] serializedData = serializer.serialize(nivelAcesso);
            return new String(serializedData);
        } catch (IOException e) {
            System.err.println("Erro ao serializar NivelAcesso " + nivelAcesso + " - " + e.getMessage());
            return null;
        }
    }

    private NivelAcesso deserializarNivelAcesso(String linha) {
        try {
            if (linha == null || linha.trim().isEmpty()) {
                return null;
            }
            byte[] data = linha.getBytes();
            return (NivelAcesso) serializer.deserialize(data, NivelAcesso.class);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao deserializar linha: '" + linha + "' - " + e.getMessage());
            return null;
        }
    }
}