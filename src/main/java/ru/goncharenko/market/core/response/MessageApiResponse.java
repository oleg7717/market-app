package ru.goncharenko.market.core.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageApiResponse implements ApiResponse {
	private String message;
	private int statusCode;

	public static MessageApiResponse error(String message, int statusCode) {
		return MessageApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.build();
	}

	public static MessageApiResponse success(String message) {
		return MessageApiResponse.builder()
				.message(message)
				.statusCode(200)
				.build();
	}
}
