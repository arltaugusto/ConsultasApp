package com.project.consultas.notifications;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.project.consultas.entities.Professor;
import com.project.consultas.utils.FileManager;

public class NewCommentNotification extends PushNotificationMessage {
	
	private String subject;
	private LocalDateTime time;
	private String professor;
	private String comment;
	
	private static final  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	
	public NewCommentNotification(String topic, String subject, LocalDateTime time, String professor, String comment, Map<String, String> data) {
		super(200, StringUtils.EMPTY, StringUtils.EMPTY, topic, data);
		this.subject = subject;
		this.time = time;
		this.professor = professor;
		this.comment = comment;
	}

	@Override
	public void buildMessage() throws IOException {
		setTitle("Mensaje de " + professor);
        String message = FileManager.readMessageFromTemplate("src/main/resources/templates/newCommentTemplate.txt");
        String[] formatDate = time.format(formatter).split(StringUtils.SPACE);
        setMessage(message.replace("${subject}", subject)
    		.replace("${date}", formatDate[0])
    		.replace("${time}", formatDate[1])
    		.replace("${message}", comment));
	}
}
