package com.project.consultas.exceptions;

import java.time.LocalDateTime;

public class ApplicationErrors {

    private String message;
    private LocalDateTime date;
    private String code;

    public ApplicationErrors(String message, String code) {
        super();
        this.message = message;
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}