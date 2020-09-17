package com.project.consultas.dto;

import java.util.Set;

public class SubscriptionDTO {
	
	private String userDeviceToken;
	private Set<String> subscriptionList;

	public SubscriptionDTO(String userDeviceToken, Set<String> subscriptionList) {
		this.userDeviceToken = userDeviceToken;
		this.subscriptionList = subscriptionList;
	}

	public String getUserDeviceToken() {
		return userDeviceToken;
	}

	public void setUserId(String userDeviceToken) {
		this.userDeviceToken = userDeviceToken;
	}

	public Set<String> getSubscriptionList() {
		return subscriptionList;
	}

	public void setSubscriptionList(Set<String> subscriptionList) {
		this.subscriptionList = subscriptionList;
	}
}
