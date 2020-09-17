package com.project.consultas.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.consultas.utils.BasicEntityUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity(name = "Student")
public class Student extends User {

    @ManyToMany(mappedBy = "students")
    private Set<Turno> books = new HashSet<>();
    
    @ManyToMany(mappedBy = "studentsFollowing")
    private Set<Subject> followedSubjects = new HashSet<>();
    
    public Student() {}
    public Student(String legajo, String email, String name, String role, String password, String mobile, String deviceToken, String surname) {
        super(legajo, email, name, role, password, mobile, deviceToken, surname);
    }

    public Set<Turno> getBooks() {
        return books;
    }

    public void setBooks(Set<Turno> books) {
        this.books = books;
    }
    
	public Set<Subject> getFollowedSubjects() {
		return followedSubjects;
	}
	
	public void setFollowedSubjects(Set<Subject> followedSubjects) {
		this.followedSubjects = followedSubjects;
	}
	
	@Override
	public Set<String> getSubscriptions() {
		Set<String> followedSubjectsTopics = followedSubjects.stream().map(Subject::getTopic).collect(Collectors.toSet());
		Set<String> booksTopics = books.stream().map(Turno::getClase).map(Clase::getTopic).collect(Collectors.toSet());
		return BasicEntityUtils.mergeSet(followedSubjectsTopics, booksTopics);
	}

    @Override
    public boolean checkAvailability(Set<Clase> clases, LocalDateTime newStartTime, LocalDateTime newEndTime) {
        Set<Clase> studentClases = getBooks().stream().map(Turno::getClase)
                .filter(clas -> clas.getStatus().equalsIgnoreCase("CONFIRMADA")
                        && clas.getInitTime().compareTo(LocalDateTime.now()) > 0)
                .collect(Collectors.toSet());
        return super.checkAvailability(studentClases, newStartTime, newEndTime);
    }
}
