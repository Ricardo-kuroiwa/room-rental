package com.project.gestao_sala.serealization;

import java.io.IOException;

public interface Serializer {
    byte[] serialize(Object obj) throws IOException;
    Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException;
}
