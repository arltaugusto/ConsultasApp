package com.project.consultas.utils;

import com.project.consultas.entities.Clase;
import com.project.consultas.repositories.ClaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@EnableAsync
public class ScheduledTasks {

    @Autowired
    private ClaseRepository claseRepository;

    @Async
    @Scheduled(fixedRate = 29000)
    public void terminateClasses() {
        Set<Clase> clases = claseRepository.findDayClasses();
        LocalDateTime currentTime = LocalDateTime.now();
        for (Clase clase : clases) {
            if (clase.getStatus().equalsIgnoreCase("confirmada")) {
                clase.setStatus("Cancelada");
            } else {
                clase.setStatus("Finalizada");
            }
        }
        claseRepository.saveAll(clases);
    }
}
