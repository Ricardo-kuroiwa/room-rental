package com.project.gestao_sala.serealization.implementacao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.gestao_sala.model.nivelAcesso.NivelAcesso;
import com.project.gestao_sala.serealization.Serializer;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonSerializable implements Serializer {

    private final ObjectMapper objectMapper;

    // O ObjectMapper do Jackson é responsável pela serialização/desserialização
    public JsonSerializable() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.registerSubtypes(NivelAcesso.class);
    }

    @Override
    public byte[] serialize(Object obj) throws IOException {
        try {
            // Converte o objeto para JSON
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new IOException("Erro ao serializar objeto para JSON: " + e.getMessage(), e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (JsonProcessingException e) {
            throw new IOException("Erro ao desserializar JSON para objeto: " + e.getMessage(), e);
        }
    }

}
