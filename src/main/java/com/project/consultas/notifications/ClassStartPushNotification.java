package com.project.consultas.notifications;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class ClassStartPushNotification extends PushNotificationMessage {
	
	private String subject;

	public ClassStartPushNotification(String subject, String topic, Map<String, String> data) {
		super(200, StringUtils.EMPTY, StringUtils.EMPTY, topic, data);
		this.subject = subject;
	}
	
	@Override
	public void buildMessage() throws IOException {
		setTitle(String.format("La clase de %s comenzo", subject));
	}

}
