package com.project.consultas.dto;

import com.project.consultas.entities.Clase;
import com.project.consultas.entities.Professor;
import com.project.consultas.entities.Subject;
import com.project.consultas.entities.Turno;

public class UserInscriptionResponse {
    private Subject subject;
    private Clase clase;
    private Turno turno;
    private Professor professor;

    public UserInscriptionResponse(Turno turno) {
        final Clase clase = turno.getClase();
        this.clase = clase;
        this.turno = turno;
        this.professor = clase.getProfessor();
        this.subject = clase.getSubject();
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Clase getClasse() {
        return clase;
    }

    public void setClasse(Clase classe) {
        this.clase = classe;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
