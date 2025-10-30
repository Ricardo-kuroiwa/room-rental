package com.project.gestao_sala.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileStorage {
    private final String BASE_DIR = "data/files";

    public FileStorage() {
        try {
            Path path = Paths.get(BASE_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o diretório base para o armazenamento de dados: " + BASE_DIR, e);
        }
    }

    public byte[] read(String filename) throws IOException {
        Path filePath = Paths.get(BASE_DIR, filename);
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        return Files.readAllBytes(filePath);
    }

    public void write(String filename, byte[] data) throws IOException {
        Path filePath = Paths.get(BASE_DIR, filename);
        Files.write(filePath, data);
    }

    public byte[] read(Handle handle) throws IOException {
        FileChannel channel = handle.getFileChannel();
        channel.position(0);

        int fileSize = (int) channel.size();
        if (fileSize == 0) {
            return new byte[0];
        }
        ByteBuffer buffer = ByteBuffer.allocate(fileSize);

        channel.read(buffer);

        buffer.flip();
        return buffer.array();
    }

    public void write(Handle handle, byte[] data) throws IOException {
        FileChannel channel = handle.getFileChannel();
        channel.position(0);
        channel.truncate(data.length);
        channel.write(ByteBuffer.wrap(data));
    }

    public Handle lock(String filename) {
        try {
            Path filePath = Paths.get(BASE_DIR, filename);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }

            FileChannel channel = FileChannel.open(filePath, StandardOpenOption.READ, StandardOpenOption.WRITE);
            FileLock fileLock = channel.lock();

            return new Handle(fileLock, channel);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao adquirir lock do arquivo: " + filename, e);
        }
    }

    public void unlock(Handle handle) {
        if (handle != null && handle.getFileLock() != null && handle.getFileLock().isValid()) {
            try {
                handle.getFileLock().release();
                handle.getFileLock().channel().close();
            } catch (IOException e) {
                System.err.println("Erro ao liberar lock do arquivo: " + e.getMessage());
            }
        }
    }
}