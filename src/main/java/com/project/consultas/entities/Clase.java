package com.project.consultas.entities;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.consultas.utils.TopicSubscriptionCapable;

@Entity
@JsonFilter("classFilter")
public class Clase implements TopicSubscriptionCapable {

    @Id
    private String id;
    private LocalDateTime initTime;
    @JsonIgnore
    private long durationInMinutes;
    private LocalDateTime creationDate;
    private LocalDateTime endTime;
    private boolean hasSingleTurnos;
    private String status;
    
    @JsonIgnore
    private String claseTopic;

    @OneToMany(mappedBy = "clase")
    @JsonIgnore
    private Set<Turno> turnos = new HashSet<>();

    @OneToMany(mappedBy = "clase")
    @JsonIgnore
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="clase_id")
    private Subject subject;

    @ManyToOne
    private Professor professor;



    public static class Builder {
        private String id;
        private LocalDateTime initTime;
        private long durationInMinutes;
        private LocalDateTime creationDate;
        private LocalDateTime endTime;
        private boolean hasSingleTurnos;
        private String status;
        private Subject subject;
        private Professor professor;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setInitTime(LocalDateTime initTime) {
            this.initTime = initTime;
            return this;
        }

        public Builder setDurationInMinutes(long durationInMinutes) {
            this.durationInMinutes = durationInMinutes;
            return this;
        }

        public Builder setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder setHasSingleTurnos(boolean hasSingleTurnos) {
            this.hasSingleTurnos = hasSingleTurnos;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setSubject(Subject subject) {
            this.subject = subject;
            return this;
        }

        public Builder setProfessor(Professor professor) {
            this.professor = professor;
            return this;
        }

        public Builder setCreationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Clase build() {
            Clase clase = new Clase();
            clase.setId(id);
            clase.setInitTime(initTime);
            clase.setDurationInMinutes(durationInMinutes);
            clase.setHasSingleTurnos(hasSingleTurnos);
            clase.setSubject(subject);
            clase.setProfessor(professor);
            clase.setStatus("Confirmada");
            clase.setEndTime(initTime.plusMinutes(durationInMinutes));
            clase.setCreationDate(creationDate);
            clase.setTopic(String.format("clase%sTopic", id));
            return clase;
        }
    }
    private Clase() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getInitTime() {
        return initTime;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setInitTime(LocalDateTime initTime) {
        this.initTime = initTime;
    }

    public long getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(long durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isHasSingleTurnos() {
        return hasSingleTurnos;
    }

    public void setHasSingleTurnos(boolean hasSingleTurnos) {
        this.hasSingleTurnos = hasSingleTurnos;
    }

    public Set<Turno> getTurnos() {
        return turnos;
    }

    public void setTurnos(Set<Turno> turnos) {
        this.turnos = turnos;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

	@Override
    @JsonIgnore
	public String getTopic() {
		return claseTopic;
	}

	@Override
	public void setTopic(String topic) {
		this.claseTopic = topic;
	}
	
	public long countStudentsSubscribed() {
		return getTurnos().stream()
                  .map(Turno::getStudents)
                  .flatMap(Collection::stream)
                  .count();
	}

	public Set<Student> getClassStudents() {
        return getTurnos().stream()
            .map(Turno::getStudents)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    public Map<String, String> getClassNotificationData() {
        Map<String, String> map = new HashMap<>();
        map.put("classId", this.id);
        return map;
    }
}
