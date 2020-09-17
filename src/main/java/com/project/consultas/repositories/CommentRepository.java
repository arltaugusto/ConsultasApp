package com.project.consultas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.consultas.entities.CommentPK;
import com.project.consultas.entities.Comment;

public interface CommentRepository extends JpaRepository<Comment, CommentPK> {

}
