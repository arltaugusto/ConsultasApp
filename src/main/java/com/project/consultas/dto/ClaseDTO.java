package com.project.consultas.dto;

import java.time.LocalDateTime;

public class ClaseDTO {

    private String consultaId;
    private LocalDateTime initTime;
    private long durationInMinutes;
    private boolean hasSingleTurnos;
    private long subjectId;
    private boolean isRegular;
    private long turnoDuration;
    private int cantidadTurnos;
    private String legajo;
    private String userId;

    public ClaseDTO() {}

    public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public void setDurationInMinutes(long durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public boolean isRegular() {
        return isRegular;
    }

    public void setIsRegular(boolean regular) {
        isRegular = regular;
    }

    public long getTurnoDuration() {
        return turnoDuration;
    }

    public void setTurnoDuration(long turnoDuration) {
        this.turnoDuration = turnoDuration;
    }

    public int getCantidadTurnos() {
        return cantidadTurnos;
    }

    public void setCantidadTurnos(int cantidadTurnos) {
        this.cantidadTurnos = cantidadTurnos;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public String getConsultaId() {
        return consultaId;
    }

    public void setConsultaId(String consultaId) {
        this.consultaId = consultaId;
    }

    public LocalDateTime getInitTime() {
        return initTime;
    }

    public void setInitTime(LocalDateTime initTime) {
        this.initTime = initTime;
    }

    public long getDurationInMinutes() {
        return durationInMinutes;
    }

    public boolean isHasSingleTurnos() {
        return hasSingleTurnos;
    }

    public void setHasSingleTurnos(boolean hasSingleTurnos) {
        this.hasSingleTurnos = hasSingleTurnos;
    }

}
