package com.project.consultas.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.project.consultas.entities.Clase;
import com.project.consultas.entities.Professor;

public class ProfessorClassesDTO {
	
	private Professor professor;
	private List<Clase> values;
	
	public ProfessorClassesDTO(Professor professor, List<Clase> values) {
		super();
		this.professor = professor;
		this.values = values;
	}
	
	public Professor getProfessor() {
		return professor;
	}
	public void setProfessor(Professor professor) {
		this.professor = professor;
	}
	public List<Clase> getValues() {
		return values;
	}
	public void setValues(List<Clase> values) {
		this.values = values;
	}
	

}
