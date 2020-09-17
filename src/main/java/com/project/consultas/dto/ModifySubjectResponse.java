package com.project.consultas.dto;

import com.project.consultas.entities.Professor;
import com.project.consultas.entities.Subject;

import java.util.List;
import java.util.Set;

public class ModifySubjectResponse {

    private Subject subject;
    private Set<Professor> subjectProfessors;
    private List<Professor> allProfessors;

    public ModifySubjectResponse(Subject subject, List<Professor> allProfessors) {
        this.subject = subject;
        this.subjectProfessors = subject.getSubjectProfessors();
        this.allProfessors = allProfessors;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Set<Professor> getSubjectProfessors() {
        return subjectProfessors;
    }

    public void setSubjectProfessors(Set<Professor> subjectProfessors) {
        this.subjectProfessors = subjectProfessors;
    }

    public List<Professor> getAllProfessors() {
        return allProfessors;
    }

    public void setAllProfessors(List<Professor> allProfessors) {
        this.allProfessors = allProfessors;
    }
}
