package com.example.auth;

import com.google.gson.JsonObject;

public class UserException extends RuntimeException {
	public final static int PERMISSION_DENIED = 200;

	private final int errorCode;

	public UserException(int errorCode, String description) {
		super(description);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("code", errorCode);
		json.addProperty("message", getMessage());
		return json.toString();
	}
}
