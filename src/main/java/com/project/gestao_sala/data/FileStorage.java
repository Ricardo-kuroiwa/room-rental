package com.project.gestao_sala.data;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

public class FileStorage {
    private final String BASE_DIR="data/files";
    private final ConcurrentHashMap<String, Lock> fileLocks = new ConcurrentHashMap<>();

    public FileStorage(){
        try{
            Files.createDirectories(Paths.get(BASE_DIR));

        }catch (IOException e){
            throw new RuntimeException("Não foi possível criar po diretório base para o armazanamento de daodos: " +BASE_DIR,e);
        }
    }
    public byte[] read(String path) throws IOException {
        Path filePath = Paths.get(BASE_DIR, path);
        if (Files.exists(filePath)) {
            return Files.readAllBytes(filePath);
        }

        return  null;
    }

    public void write(String path, byte[] data) throws IOException {
        Path filePath = Paths.get(BASE_DIR, path);
        Files.write(filePath, data);
    }
    public Handle lock(String path) {
        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }

            FileChannel channel = FileChannel.open(filePath, StandardOpenOption.READ, StandardOpenOption.WRITE);
            FileLock fileLock = channel.lock();

            return new Handle(fileLock);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao adquirir lock do arquivo: " + path, e);
        }
    }
    public void unlock(String handle) {
        if (handle != null && handle.getFileLock() != null) {
            try {
                handle.getFileLock().release(); // Libera o lock
                handle.getFileLock().channel().close();
            } catch (IOException e) {
                System.err.println("Erro ao liberar lock do arquivo: " + e.getMessage());
            }
        }
    }
}
