package com.project.consultas.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.consultas.utils.TopicSubscriptionCapable;

@Entity
public class Subject implements TopicSubscriptionCapable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "subject_id")
    private long subjectId;
    private String name;
    private String imagePath;
    private String subjectTopic;
    
    @ManyToMany
    @JsonIgnore
    private Set<Professor> subjectProfessors = new HashSet<>();
    
    @ManyToMany
    @JsonIgnore
    private Set<Student> studentsFollowing = new HashSet<>();

    @OneToMany(mappedBy = "subject")
    @JsonIgnore
    private Set<Clase> clases = new HashSet<>();

    public Subject() {}
    
    public Subject(String name, Set<Professor> professorList) {
		super();
		this.name = name;
		this.subjectProfessors = professorList;
		this.subjectTopic = name.toLowerCase().replace(StringUtils.SPACE, StringUtils.EMPTY) + "Topic";
	}

	public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Clase> getClases() {
        return clases;
    }

    public void setClases(Set<Clase> clases) {
        this.clases = clases;
    }

	@Override
	@JsonIgnore
	public String getTopic() {
		return subjectTopic;
	}

	@Override
	public void setTopic(String subjectTopic) {
		this.subjectTopic = subjectTopic;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Set<Professor> getSubjectProfessors() {
		return subjectProfessors;
	}

	public void setSubjectProfessors(Set<Professor> subjectProfessors) {
		this.subjectProfessors = subjectProfessors;
	}

	public Set<Student> getStudentsFollowing() {
		return studentsFollowing;
	}

	public void setStudentsFollowing(Set<Student> studentsFollowing) {
		this.studentsFollowing = studentsFollowing;
	}
}
