package com.docuitservice.exception;

public class CustomException {
	private static final long serialVersionUID = 1L;
	private String status;
	private String message;
	private Object data;
	private Integer code;

	public CustomException() {

	}

	public CustomException(String status, String message, Object data, Integer code) {

		this.status = status;
		this.message = message;
		this.data = data;
		this.code = code;

	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
