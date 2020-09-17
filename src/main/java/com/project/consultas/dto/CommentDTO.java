package com.project.consultas.dto;

import java.time.LocalDateTime;

public class CommentDTO {

    private String id;
    private String comment;
    private LocalDateTime dateTime;

    public CommentDTO() {}

    public CommentDTO(String id, String comment, LocalDateTime dateTime) {
        this.id = id;
        this.comment = comment;
        this.dateTime = dateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
