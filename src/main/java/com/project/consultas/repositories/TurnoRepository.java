package com.project.consultas.repositories;

import com.project.consultas.entities.TurnoPK;
import com.project.consultas.entities.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurnoRepository extends JpaRepository<Turno, TurnoPK> {
}
