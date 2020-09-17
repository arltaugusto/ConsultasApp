package com.project.consultas.repositories;

import com.project.consultas.entities.Student;

import javax.transaction.Transactional;

@Transactional
public interface StudentRepository extends UserBaseRepository<Student> {}