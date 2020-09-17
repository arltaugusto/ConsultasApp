package com.project.consultas.dto;

import java.time.LocalDateTime;
import java.util.*;

import com.project.consultas.entities.*;

public class ClassDataDTO {

	private List<TurnosResponse> turnos = new ArrayList<>();
	//init tunrto
	// list alumnos
	private Set<Comment> comments = new HashSet<>();
	private Professor professor;
	private Subject subject;
	private String status;
	private boolean hasSingleTurnos;
	private LocalDateTime initTime;

	public ClassDataDTO(List<TurnosResponse> turnos, Set<Comment> comments, Professor professor, Subject subject, String status, boolean hasSingleTurnos, LocalDateTime initTime) {
		this.turnos = turnos;
		this.comments = comments;
		this.professor = professor;
		this.subject = subject;
		this.status = status;
		this.hasSingleTurnos = hasSingleTurnos;
		this.initTime = initTime;
	}

	public Professor getProfessor() {
		return professor;
	}

	public LocalDateTime getInitTime() {
		return initTime;
	}

	public void setInitTime(LocalDateTime initTime) {
		this.initTime = initTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isHasSingleTurnos() {
		return hasSingleTurnos;
	}

	public void setHasSingleTurnos(boolean hasSingleTurnos) {
		this.hasSingleTurnos = hasSingleTurnos;
	}

	public void setProfessor(Professor professor) {
		this.professor = professor;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public List<TurnosResponse> getTurnos() {
		return turnos;
	}
	public void setTurnos(List<TurnosResponse> turnos) {
		this.turnos = turnos;
	}
	public Set<Comment> getComments() {
		return comments;
	}
	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

}
