package ru.goncharenko.market.core.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.goncharenko.market.core.exception.ResourceNotFoundException;
import ru.goncharenko.market.core.exception.ValidationException;
import ru.goncharenko.market.core.response.MessageApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public MessageApiResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
		return MessageApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND.value());
	}

	@ExceptionHandler(ValidationException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
	public MessageApiResponse handleValidationException(ValidationException ex) {
		return MessageApiResponse.error(ex.getMessage(), HttpStatus.UNPROCESSABLE_CONTENT.value());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public MessageApiResponse handleGenericException(Exception ex) {
		return MessageApiResponse.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public MessageApiResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
		StringBuilder textErrors = new StringBuilder();
		ex.getBindingResult().getAllErrors()
				.forEach(error -> textErrors.append(error.getDefaultMessage()).append(". "));

		return MessageApiResponse
				.error(String.join(". ", textErrors.toString().trim()), HttpStatus.BAD_REQUEST.value());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
	@ResponseBody
	public MessageApiResponse handleConstraintViolationException(ConstraintViolationException ex) {
		return MessageApiResponse.error(ex.getMessage(), HttpStatus.UNPROCESSABLE_CONTENT.value());
	}
}
