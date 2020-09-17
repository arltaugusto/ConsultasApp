package com.project.consultas.dto;

import java.util.List;

public class NewSubjectDTO {

    private String name;
    private List<String> professorIds;

    public NewSubjectDTO(String name, List<String> professorIds) {
        this.name = name;
        this.professorIds = professorIds;
    }

    public NewSubjectDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getProfessorIds() {
        return professorIds;
    }

    public void setProfessorIds(List<String> professorIds) {
        this.professorIds = professorIds;
    }
}
