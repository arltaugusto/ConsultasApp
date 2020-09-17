package com.project.consultas.notifications;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.project.consultas.utils.FileManager;

import javax.print.DocFlavor;

public class FirstStudentSubscribedPushNotification extends PushNotificationMessage {

	private String subject;
	private LocalDateTime time;
	private static final  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
	public FirstStudentSubscribedPushNotification(String subject, LocalDateTime time, String professorToken, String topic, Map<String, String> data) {
		super(200, "Alumno inscripto en consulta", professorToken, topic, data);
		this.subject = subject;
		this.time = time;
	}
	
	@Override
	public void buildMessage() throws IOException {
        String message = FileManager.readMessageFromTemplate("src/main/resources/templates/firstStudentSubscribedTemplate.txt");
        String[] formatDate = time.format(formatter).split(StringUtils.SPACE);
        setMessage(message
        	.replace("${subject}", subject)
    		.replace("${date}", formatDate[0])
    		.replace("${time}", formatDate[1]));
	}
}
