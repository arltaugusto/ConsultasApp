package com.project.consultas.entities;

import javax.persistence.*;

import com.project.consultas.dto.UserDTO;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity(name = "Professor")
public class Professor extends User {

    @OneToMany(mappedBy = "professor")
    @JsonIgnore
    private Set<Clase> clases = new HashSet<>();
    
    @ManyToMany(mappedBy = "subjectProfessors")
    @JsonIgnore
    private Set<Subject> subjectProfessors = new HashSet<>();

    public Professor() {}
    public Professor(String legajo, String email, String name, String role, String password, String mobile, String deviceToken, String surname) {
        super(legajo, email, name, role, password, mobile, deviceToken, surname);
    }

    public Set<Clase> getClases() {
        return clases;
    }

    public void setClases(Set<Clase> clases) {
        this.clases = clases;
    }
    
	public Set<Subject> getSubjectProfessors() {
		return subjectProfessors;
	}
	
	public void setSubjectProfessors(Set<Subject> subjectProfessors) {
		this.subjectProfessors = subjectProfessors;
	}
	
	@Override
	public Set<String> getSubscriptions() {
		setDeviceToken(StringUtils.EMPTY);
		return new HashSet<>();
	}

    @Override
    public boolean checkAvailability(Set<Clase> clases, LocalDateTime newStartTime, LocalDateTime newEndTime) {
        Set<Clase> professorClases = getClases().stream().filter(clase -> clase.getStatus().equalsIgnoreCase("Confirmada")
                && clase.getInitTime().compareTo(LocalDateTime.now()) > 0).collect(Collectors.toSet());
        return super.checkAvailability(professorClases, newStartTime, newEndTime);
    }

    @Override
    public void updateData(UserDTO user) {
        super.updateData(user);
        setShowMobile(user.isShowMobile());
    }

}
