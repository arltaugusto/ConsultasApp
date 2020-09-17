package com.project.consultas.notifications;

import java.io.IOException;

public abstract class NotificationMessage {

    private int status;
    private String message;

    public NotificationMessage(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public abstract void buildMessage() throws IOException;

}
