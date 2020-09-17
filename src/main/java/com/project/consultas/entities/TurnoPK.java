package com.project.consultas.entities;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class TurnoPK implements Serializable {

    private String id;
    private LocalDateTime startTime;

    public TurnoPK() {}

    public TurnoPK(String id, LocalDateTime startTime) {
        this.id = id;
        this.startTime = startTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TurnoPK turnoPK = (TurnoPK) o;
        return id == turnoPK.id &&
                startTime.equals(turnoPK.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startTime);
    }
}
