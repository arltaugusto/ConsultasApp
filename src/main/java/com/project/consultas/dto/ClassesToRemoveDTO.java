package com.project.consultas.dto;

import java.util.List;

public class ClassesToRemoveDTO {

    private List<String> classesToRemove;

    public ClassesToRemoveDTO() {}

    public ClassesToRemoveDTO(List<String> classesToRemove) {
        this.classesToRemove = classesToRemove;
    }

    public List<String> getClassesToRemove() {
        return classesToRemove;
    }

    public void setClassesToRemove(List<String> classesToRemove) {
        this.classesToRemove = classesToRemove;
    }
}
