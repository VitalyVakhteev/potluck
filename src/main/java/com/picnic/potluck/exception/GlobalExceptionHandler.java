package com.picnic.potluck.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
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

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<Map<String, Object>> handleApi(ApiException ex) {
		return ResponseEntity.status(ex.status())
				.body(Map.of(
						"code", ex.code(),
						"error", ex.getMessage(),
						"status", ex.status().value(),
						"timestamp", java.time.LocalDateTime.now()
				));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
		String code = "CONFLICT";
		String field = null;

		var cause = ex.getCause();
		if (cause instanceof org.hibernate.exception.ConstraintViolationException cve) {
			String cn = cve.getConstraintName();
			if ("uc_users_username".equalsIgnoreCase(cn)) { code = "USERNAME_TAKEN"; field = "username"; }
			else if ("uc_users_email".equalsIgnoreCase(cn)) { code = "EMAIL_TAKEN"; field = "email"; }
			else if ("uc_users_phone".equalsIgnoreCase(cn)) { code = "PHONE_TAKEN"; field = "phone"; }
		}

		var body = new java.util.LinkedHashMap<String, Object>();
		body.put("code", code);
		body.put("error", "Duplicate value");
		if (field != null) body.put("field", field);
		body.put("status", 409);
		body.put("timestamp", java.time.LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
	}

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
	public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
		String code = switch (ex.getMessage()) {
			case "CURRENT_PASSWORD_INCORRECT" -> "CURRENT_PASSWORD_INCORRECT";
			case "PASSWORD_UNCHANGED" -> "PASSWORD_UNCHANGED";
			case "PASSWORD_TOO_SIMILAR" -> "PASSWORD_TOO_SIMILAR";
			default -> "ILLEGAL_ARGUMENT";
		};
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
				"code", code,
				"error", "Illegal Argument Passed",
				"status", 400,
				"timestamp", java.time.LocalDateTime.now()
		));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
		var fields = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.collect(Collectors.toMap(
						FieldError::getField,
						fe -> {
							String msg = fe.getDefaultMessage();
							return (msg != null && !msg.isBlank()) ? msg : "Invalid value";
						},
						(a, _) -> a
				));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Map.of(
						"code", "VALIDATION_FAILED",
						"error", "Validation Failed",
						"fields", fields,
						"status", 400,
						"timestamp", java.time.LocalDateTime.now()
				));
	}

	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<Map<String, Object>> handleAuth(AuthenticationException exception) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of(
						"code", "INVALID_CREDENTIALS",
						"error", "Invalid username or password",
						"status", 401,
						"timestamp", java.time.LocalDateTime.now()
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
