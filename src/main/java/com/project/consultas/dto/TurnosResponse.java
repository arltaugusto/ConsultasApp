package com.project.consultas.dto;

import com.project.consultas.entities.Student;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class TurnosResponse {

    private LocalDateTime turnoTime;
    private Set<Student> students;

    public TurnosResponse() {
    }

    public TurnosResponse(LocalDateTime turnoTime, Set<Student> students) {
        this.turnoTime = turnoTime;
        this.students = students;
    }

    public LocalDateTime getTurnoTime() {
        return turnoTime;
    }

    public void setTurnoTime(LocalDateTime turnoTime) {
        this.turnoTime = turnoTime;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }
}
