package com.project.consultas.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class Comment {

    @EmbeddedId
    private CommentPK commentPK;

    private String comment;

    @ManyToOne
    @MapsId("id")
    @JsonIgnore
    private Clase clase;

    public Comment() {}

    public Comment(CommentPK commentPK, String comment) {
        this.commentPK = commentPK;
        this.comment = comment;
    }

    public CommentPK getCommentPK() {
        return commentPK;
    }

    public void setCommentPK(CommentPK commentPK) {
        this.commentPK = commentPK;
    }

    public Clase getClase() {
        return clase;
    }

    public void setClase(Clase clase) {
        this.clase = clase;
    }

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
    
}
