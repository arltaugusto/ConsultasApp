package com.project.consultas.dto;

import java.util.List;

public class SubjectDTO {
	
	private long id;
	private String studentId;
	private String name;
	private List<String> subjectProfessors;
	private List<String> subjectProfessorsToRemove;

	public SubjectDTO () {}
	
	public SubjectDTO(long id, String name, List<String> subjectProfessors, List<String> subjectProfessorsToRemove, String studentId) {
		this.name = name;
		this.id = id;
		this.subjectProfessors = subjectProfessors;
		this.subjectProfessorsToRemove = subjectProfessorsToRemove;
		this.studentId = studentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getSubjectProfessors() {
		return subjectProfessors;
	}

	public void setSubjectProfessors(List<String> subjectProfessors) {
		this.subjectProfessors = subjectProfessors;
	}

	public List<String> getSubjectProfessorsToRemove() {
		return subjectProfessorsToRemove;
	}

	public void setSubjectProfessorsToRemove(List<String> subjectProfessorsToRemove) {
		this.subjectProfessorsToRemove = subjectProfessorsToRemove;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
}
