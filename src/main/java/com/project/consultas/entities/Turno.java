package com.project.consultas.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Turno {

    @EmbeddedId
    private TurnoPK turnoPk;
    private LocalDateTime endTime;

    @ManyToOne
    @MapsId("id")
    @JsonIgnore
    private Clase clase;

    @ManyToMany
    @JsonIgnore
    private Set<Student> students = new HashSet<>();

    private boolean hasUsers;

    public Turno() {}

    public Turno(TurnoPK turnoPk, LocalDateTime endTime) {
        this.turnoPk = turnoPk;
        this.endTime = endTime;
    }

    public TurnoPK getTurnoPk() {
        return turnoPk;
    }

    public void setTurnoPk(TurnoPK turnoPk) {
        this.turnoPk = turnoPk;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Clase getClase() {
        return clase;
    }

    public void setClase(Clase clase) {
        this.clase = clase;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    public boolean isHasUsers() {
        return hasUsers;
    }

    public void setHasUsers(boolean hasUsers) {
        this.hasUsers = hasUsers;
    }
}
