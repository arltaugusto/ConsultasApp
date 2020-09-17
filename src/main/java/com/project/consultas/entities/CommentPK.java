package com.project.consultas.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Embeddable
public class CommentPK implements Serializable {

	@JsonIgnore
    private String id;
    private LocalDateTime commentTime;

    public CommentPK() {}

    public CommentPK(String id, LocalDateTime commentTime) {
        this.id = id;
        this.commentTime = commentTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(LocalDateTime commentTime) {
        this.commentTime = commentTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentPK commentPK = (CommentPK) o;
        return id.equals(commentPK.id) &&
                commentTime.equals(commentPK.commentTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, commentTime);
    }
}
