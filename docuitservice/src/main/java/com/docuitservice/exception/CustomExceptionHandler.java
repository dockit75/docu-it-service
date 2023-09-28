package com.docuitservice.exception;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.docuitservice.util.DockItConstants;
import com.docuitservice.util.Response;

@SuppressWarnings({ "unchecked", "rawtypes" })
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public final ResponseEntity<Object> handleAllExceptions(BusinessException ex) {
		CustomException exceptionResponse = new CustomException(ex.getStatus(), ex.getMessage(), ex.getData(),
				ex.getCode());
		return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
	}

	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", status.value());
		List<String> value = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
				.collect(Collectors.toList());
		List<String> key = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getField())
				.collect(Collectors.toList());
		body.put("key", key);
		body.put("errors", value);

		return new ResponseEntity<>(body, headers, status);

	}

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Object> failsException(Exception exception) {
		exception.printStackTrace();
		Response response = new Response();
		response.setMessage("Something Went Wrong.");
		response.setStatus(DockItConstants.RESPONSE_FAIL);
		response.setCode(500);
		response.setResponse(Collections.<String, Object>emptyMap());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
