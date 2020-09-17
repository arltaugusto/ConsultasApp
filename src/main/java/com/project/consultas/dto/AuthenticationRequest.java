package com.project.consultas.dto;

public class AuthenticationRequest {

    private String legajo;
    private String password;
    private String deviceToken;

    public AuthenticationRequest() {}

    public AuthenticationRequest(String email, String password) {
        this.legajo = email;
        this.password = password;
    }

    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
}