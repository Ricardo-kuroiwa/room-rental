package com.project.gestao_sala.repository.implementacao;

import com.project.gestao_sala.data.FileStorage;
import com.project.gestao_sala.data.Handle;
import com.project.gestao_sala.model.espaco.Espaco;
import com.project.gestao_sala.model.reserva.Reserva;
import com.project.gestao_sala.repository.EspacoRepository;
import com.project.gestao_sala.serealization.Serializer;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Repository
public class EspacoFileRepository implements EspacoRepository {
    private static final String FILENAME = "espacos.txt";
    private final FileStorage fileStorage;
    private final Serializer serializer;

    public EspacoFileRepository(Serializer serializer) {
        this.fileStorage = new FileStorage();
        this.serializer = serializer;
    }

    @Override
    public Espaco buscar(char codigo) {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Espaco> espacos = listarTodos(lockHandle);
            return espacos
                    .stream()
                    .filter(espaco -> espaco.getCodigo() == codigo)
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
    public Espaco[] listar() {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Espaco> espacos = listarTodos(lockHandle);
            return espacos.toArray(new Espaco[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    @Override
    public boolean salvar(Espaco e) {
        Handle lockHandle = null;

        try {
            lockHandle = fileStorage.lock(FILENAME);
            //Get todos os niveis
            List<Espaco> allEspacos = listarTodos(lockHandle);
            //Remove o que ja existe
            allEspacos.removeIf(es -> es.getCodigo() == e.getCodigo());
            //Adiciona o novo nivel
            allEspacos.add(e);

            String contentToSave = allEspacos.stream()
                    .map(this::serializarEspaco)
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

    @Override
    public  boolean deletar(char codigo){
        Handle lockHandle = null;

        try {
            lockHandle = fileStorage.lock(FILENAME);
            //Get todos os niveis
            List<Espaco> allEspacos = listarTodos(lockHandle);
            boolean espaco  = allEspacos.removeIf(es -> es.getCodigo() == codigo);
            if (!espaco){
                throw new RuntimeException("Nível de acesso não encontrado para exclusão: " + codigo);
            }

            String contentToSave = allEspacos.stream()
                    .map(this::serializarEspaco)
                    .collect(Collectors.joining("\n"));

            fileStorage.write(lockHandle, contentToSave.getBytes());
            return true;

        } catch (Exception exception) {
            System.err.println("Erro inesperado ao deletar Categoria: " + exception.getMessage());
            return false;
        }finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    @Override
    public boolean atualizar(Espaco e) {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Espaco> espacos = listarTodos(lockHandle);

            Optional<Espaco> existenteOpt = espacos.stream()
                    .filter(espaco -> espaco.getCodigo() == e.getCodigo())
                    .findFirst();

            if (existenteOpt.isPresent()) {
                espacos.removeIf(espaco -> espaco.getCodigo() == e.getCodigo());
                espacos.add(e);

                espacos.sort(Comparator.comparingInt(Espaco::getCodigo));

                String contentToSave = espacos.stream()
                        .map(this::serializarEspaco)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining("\n"));

                fileStorage.write(lockHandle, contentToSave.getBytes());
                return true;
            } else {
                System.err.println("Espaco com código '" + e.getCodigo() + "' não encontrado na lista para atualização.");
                return false;
            }
        } catch (Exception exception) {
            System.err.println("Erro inesperado ao atualizar Espaco: " + exception.getMessage());
            exception.printStackTrace();
            return false;
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }
    @Override
    public boolean criarNovaReserva(char codigoEspaco, Reserva novaReserva) {
        Handle lockHandle = null;
        try {
            lockHandle = fileStorage.lock(FILENAME);
            List<Espaco> allEspacos = listarTodos(lockHandle);
            Optional<Espaco> espacoParaReservarOpt = allEspacos.stream()
                    .filter(e -> e.getCodigo() == codigoEspaco)
                    .findFirst();

            if (espacoParaReservarOpt.isEmpty()) {
                throw new IllegalArgumentException("Espaço com código '" + codigoEspaco + "' não encontrado.");
            }

            Espaco espacoAlvo = espacoParaReservarOpt.get();

            for (Reserva existente : espacoAlvo.getReservas()) {
                if (existente.isAtiva() && existente.getData().equals(novaReserva.getData())) {

                    boolean haConflito = novaReserva.getHoraInicio().isBefore(existente.getHoraFim()) &&
                            existente.getHoraInicio().isBefore(novaReserva.getHoraFim());

                    if (haConflito) {
                        throw new IllegalStateException("Conflito de horário para a reserva. Já existe uma reserva nesse período.");
                    }
                }
            }

            novaReserva.setAtiva(true);
            novaReserva.setDataCriacao(LocalDateTime.now());
            espacoAlvo.AdicionarReserva(novaReserva);

            String contentToSave = allEspacos.stream()
                    .map(this::serializarEspaco)
                    .collect(Collectors.joining("\n"));

            fileStorage.write(lockHandle, contentToSave.getBytes());

            return true;

        } catch (Exception e) {
            System.err.println("Erro ao criar nova reserva: " + e.getMessage());
            return false;
        } finally {
            if (lockHandle != null) {
                fileStorage.unlock(lockHandle);
            }
        }
    }

    private Espaco deserializarEspaco(String linha) {
        try {
            return (Espaco) serializer.deserialize(linha.getBytes(),Espaco.class);
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

    private List<Espaco> listarTodos(Handle handle) throws IOException {
        byte[] data = fileStorage.read(handle);
        String fileContent = new String(data);

        if (!fileContent.trim().isEmpty()) {
            return Arrays.stream(fileContent.split("\n"))
                    .filter(line -> !line.trim().isEmpty())
                    .map(this::deserializarEspaco)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
