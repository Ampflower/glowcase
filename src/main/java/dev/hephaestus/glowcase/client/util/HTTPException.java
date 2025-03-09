package dev.hephaestus.glowcase.client.util;

public class HTTPException extends RuntimeException {
	private final int code;

	public HTTPException(String message, int code) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
