package com.project.consultas.notifications;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.project.consultas.utils.BasicEntityUtils;
import com.project.consultas.utils.FileManager;

public class NoStudentPushNotification extends PushNotificationMessage {

	private String subject;
	private LocalDateTime time;
	
	public NoStudentPushNotification(String destinationToken, String subject, LocalDateTime time, Map<String, String> data) {
		super(200, StringUtils.EMPTY, destinationToken, StringUtils.EMPTY, data);
		this.subject = subject;
		this.time = time;
	}

	@Override
	public void buildMessage() throws IOException {
		 String message = FileManager.readMessageFromTemplate("src/main/resources/templates/noStudentsTemplate.txt");
	     String[] date = BasicEntityUtils.dateFormat(time);	
	     setTitle("Consulta de" + subject);
	     setMessage(message.replace("${subject}", subject)
    		 .replace("${date}", date[0])
    		 .replace("${time}", date[1]));
	}
}
