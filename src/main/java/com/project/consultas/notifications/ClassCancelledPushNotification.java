package com.project.consultas.notifications;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.project.consultas.utils.BasicEntityUtils;
import com.project.consultas.utils.FileManager;

public class ClassCancelledPushNotification extends PushNotificationMessage {

	private String subject;
	private LocalDateTime time;
	private String professor;
	
	public ClassCancelledPushNotification(String topic, String subject, String professor, LocalDateTime time, Map<String, String> data) {
		super(200, "Clase Cancelada", StringUtils.EMPTY, topic, data);
		this.subject = subject;
		this.professor = professor;
		this.time = time;
	}
	
	@Override
	public void buildMessage() throws IOException {
        String message = FileManager.readMessageFromTemplate("src/main/resources/templates/classCancelationTemplate.txt");
        String[] date = BasicEntityUtils.dateFormat(time);
		setMessage(message.replace("${subject}", subject)
			.replace("${professor}", professor)
			.replace("${date}", date[0])
			.replace("${time}", date[1]));
	}
}
