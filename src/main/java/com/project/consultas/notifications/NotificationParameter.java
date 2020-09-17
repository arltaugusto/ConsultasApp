package com.project.consultas.notifications;

public enum NotificationParameter {
    SOUND("default"),
    COLOR("#60CECE");

    private String value;

    NotificationParameter(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}