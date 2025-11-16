package com.project.gestao_sala.exceptions;

public class InfrastructureException extends RuntimeException {

    public InfrastructureException(String message) {
        super(message);
    }

    public InfrastructureException(String message, Throwable causa) {
        super(message, causa);
    }

    public InfrastructureException(Throwable causa) {
        super(causa);
    }
}
