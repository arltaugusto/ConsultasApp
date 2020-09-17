package com.project.consultas.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import com.project.consultas.entities.User;

@NoRepositoryBean
public interface UserBaseRepository<T extends User> extends JpaRepository<T, String> {
	Optional<T> findByLegajo (String legajo);
}

