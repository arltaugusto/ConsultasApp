package com.project.consultas.notifications;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.project.consultas.utils.FileManager;

public class NewProfessorPasswordEmailNotification extends EmailNotificationMessage {

    private String name;
    private String password;
    private String legajo;

    public NewProfessorPasswordEmailNotification(String name, String password, String destinationEmail, String legajo) throws IOException {
        super(200, destinationEmail, "Contrasena para la app de consultas");
        this.name = name;
        this.password = password;
        this.legajo = legajo;
    }

    @Override
    public void buildMessage() throws IOException {
        String message = FileManager.readMessageFromTemplate("src/main/resources/templates/passwordEmailTemplate.txt");
        setMessage(message.replace("${professorName}", name)
            .replace("${legajo}", legajo)
    		.replace("${password}", password));
    }
}
