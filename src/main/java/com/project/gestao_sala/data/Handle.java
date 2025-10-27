package com.project.gestao_sala.data;

import java.nio.channels.FileLock;

public class Handle {
    private FileLock fileLock;

    public Handle(FileLock fileLock) {
        this.fileLock = fileLock;
    }

    public FileLock getFileLock() {
        return fileLock;
    }
}