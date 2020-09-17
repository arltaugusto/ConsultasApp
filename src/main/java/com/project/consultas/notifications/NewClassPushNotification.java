package com.project.consultas.notifications;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.project.consultas.utils.FileManager;

public class NewClassPushNotification extends PushNotificationMessage {

	private String subject;
	private String professor;
	private LocalDateTime time;
	
	public NewClassPushNotification(String topic, String professor, String subject, LocalDateTime time, Map<String, String> data) {
		super(200, StringUtils.EMPTY, StringUtils.EMPTY, topic, data);
		this.professor = professor;
		this.time = time;
		this.subject = subject;
	}
	
	@Override
	public void buildMessage() throws IOException {
        String message = FileManager.readMessageFromTemplate("src/main/resources/templates/newClassTemplate.txt");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String[] formatDate = time.format(formatter).split(StringUtils.SPACE);
        setTitle("Nueva consulta de " + subject);
        setMessage(message.replace("${date}", formatDate[0])
    		.replace("${time}", formatDate[1])
    		.replace("${professor}", professor));
	}
}
