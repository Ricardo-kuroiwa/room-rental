package com.project.gestao_sala.data;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;


public class Handle {
    private final FileLock fileLock;
    private final FileChannel fileChannel;

    public Handle(FileLock fileLock, FileChannel fileChannel) {
        this.fileLock = fileLock;
        this.fileChannel = fileChannel;
    }

    public FileLock getFileLock() {
        return fileLock;
    }

    public FileChannel getFileChannel() {
        return fileChannel;
    }
}