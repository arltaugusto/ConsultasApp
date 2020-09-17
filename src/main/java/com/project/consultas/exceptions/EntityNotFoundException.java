package com.project.consultas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityNotFoundException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final String entityName;

    public EntityNotFoundException(String name) {
        this.entityName = name;
    }

    @Override
    public String toString() {
        return "Bad entity key for" + entityName;
    }
}