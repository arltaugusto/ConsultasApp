package com.project.consultas.repositories;

import com.project.consultas.entities.Admin;
import com.project.consultas.entities.Professor;

import javax.transaction.Transactional;

@Transactional
public interface AdminRepository extends UserBaseRepository<Admin>{
}
