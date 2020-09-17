package com.project.consultas.repositories;

import com.project.consultas.entities.Professor;
import com.project.consultas.entities.Student;
import com.project.consultas.entities.User;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Transactional
public interface ProfessorRepository extends UserBaseRepository<Professor> {
	
	@Query( "select o from Professor o where id in :ids" )
	Set<Professor> findByUserIds(@Param("ids") List<String> professorIdList);
}