package com.project.consultas.notifications;

import java.util.Map;

public abstract class 	PushNotificationMessage extends NotificationMessage {
	
	private String title;
	private String destinationToken;
    private String topic;
    private Map<String, String> data;

	
	public PushNotificationMessage(int status, String title, String destinationToken, String topic, Map<String, String> data) {
		super(status);
		this.title = title;
		this.destinationToken = destinationToken;
		this.topic = topic;
		this.data = data;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDestinationToken() {
		return destinationToken;
	}

	public void setDestinationToken(String destinationToken) {
		this.destinationToken = destinationToken;
	}

	public String getTopic() {
		return topic;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
}
