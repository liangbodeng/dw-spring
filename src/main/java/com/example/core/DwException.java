package com.example.core;

import com.google.gson.JsonObject;

public class DwException extends RuntimeException {
	public static final int INTERNAL_SYSTEM_ERROR = 400;
	public final static int INVALID_PARAMETER = 201;
	public final static int RESOURCE_NOT_FOUND = 203;

	private final int errorCode;

	public DwException(final int errorCode, String description) {
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

	public String toJson(String parameterName) {
		JsonObject json = new JsonObject();
		json.addProperty("code", errorCode);
		json.addProperty("message", "Invalid value for parameter: '" + parameterName + "', " + this.getMessage());
		return json.toString();
	}
}
