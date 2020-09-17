package com.project.consultas.entities;

import javax.persistence.Entity;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "Admin")
public class Admin extends User {

    public Admin() {}
    public Admin(String legajo, String email, String name, String role, String password, String mobile, String deviceToken, String surname) {
        super(legajo, email, name, role, password, mobile, deviceToken, surname);
    }

    @Override
    public Set<String> getSubscriptions() {
        return new HashSet<>();
    }
}
