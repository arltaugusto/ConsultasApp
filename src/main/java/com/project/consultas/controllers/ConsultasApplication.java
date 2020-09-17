package com.project.consultas.controllers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan({"com.project.consultas.entities","com.project.consultas.dto","com.project.consultas.", "com.project.consultas.utils", "com.project.consultas.security", "com.project.consultas.repositories", "com.project.consultas.notifications", "com.project.consultas.threads", "com.project.consultas.tests"})
@EntityScan("com.project.consultas.entities")
@EnableJpaRepositories("com.project.consultas.repositories")
@EnableCaching
public class ConsultasApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConsultasApplication.class, args);
	}
}









