package com.project.consultas.repositories;

import java.util.Optional;

import javax.transaction.Transactional;

import com.project.consultas.entities.User;

@Transactional
public interface UserRepository extends UserBaseRepository<User> {
}