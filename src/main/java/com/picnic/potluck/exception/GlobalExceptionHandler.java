package com.picnic.potluck.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.slf4j.LoggerFactory.getLogger;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
		getLogger(getClass()).error("Unhandled exception", ex);
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of(
						"error", "Internal Server Error",
						"status", 500,
						"timestamp", LocalDateTime.now()
				));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException exception) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(Map.of(
						"error", "Illegal Argument Passed",
						"status", 400,
						"timestamp", LocalDateTime.now()
				));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
//		var fields = ex.getBindingResult()
//				.getFieldErrors()
//				.stream()
//				.collect(Collectors.toMap(
//						FieldError::getField,
//						// IDE warns that function may return null, might need looking into if it causes issues
//						DefaultMessageSourceResolvable::getDefaultMessage,
//						(a, b) -> a));
//      Until I can make sure that getDefaultMessage never returns null, I'm commenting this
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(Map.of(
						"error", "Validation Failed",
//						"fields", fields,
						"status", 400,
						"timestamp", LocalDateTime.now()
				));
	}

	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException exception) {
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of(
						"error", "Unauthorized Request",
						"status", 401,
						"timestamp", LocalDateTime.now()
				));
	}


	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException exception) {
		return ResponseEntity
				.status(HttpStatus.FORBIDDEN)
				.body(Map.of(
						"error", "Access Denied",
						"status", 403,
						"timestamp", LocalDateTime.now()
				));
	}

	@ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class, ResourceNotFoundException.class})
	public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException exception) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(Map.of(
						"error", "Requested Object Not Found",
						"status", 404,
						"timestamp", LocalDateTime.now()
				));
	}
}
