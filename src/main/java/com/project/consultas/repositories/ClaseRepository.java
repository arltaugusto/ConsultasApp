package com.project.consultas.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.consultas.entities.Clase;

public interface ClaseRepository extends JpaRepository<Clase, String> {
	
	@Query( "select o from Clase o where id in :ids" )
	Set<Clase> findByUserIds(@Param("ids") List<String> professorIdList);

	@Query( "select c from Clase c where YEAR(c.endTime) = YEAR(CURRENT_DATE) and MONTH(c.endTime) = MONTH(CURRENT_DATE) and DAY(c.endTime) = DAY(CURRENT_DATE)" )
	Set<Clase> findDayClasses();
}
