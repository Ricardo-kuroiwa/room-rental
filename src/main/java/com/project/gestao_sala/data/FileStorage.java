package com.project.gestao_sala.data;

import java.io.*;

public class FileStorage {
    public byte[] read(String path) throws IOException {
        try (FileInputStream fis = new FileInputStream(path)) {
            return fis.readAllBytes();
        }
    }

    public void write(String path, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(data);
        }
    }
    // Vereficar o que é funcao lock ?
}
