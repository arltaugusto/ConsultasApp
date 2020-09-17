package com.project.consultas.notifications;

import java.io.IOException;

public abstract class EmailNotificationMessage extends NotificationMessage {

    private String subject;
    private String destinationEmail;

    public EmailNotificationMessage(int status, String destinationEmail, String subject) {
        super(status);
        this.destinationEmail = destinationEmail;
        this.subject = subject;
    }

    public abstract void buildMessage() throws IOException;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        subject = subject;
    }

    public String getDestinationEmail() {
        return destinationEmail;
    }

    public void setDestinationEmail(String destinationEmail) {
        this.destinationEmail = destinationEmail;
    }
}
