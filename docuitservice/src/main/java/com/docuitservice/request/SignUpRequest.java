package com.docuitservice.request;

import lombok.Data;

@Data
public class SignUpRequest {

	private String name;
	private String email;
	private String gender;
	private String phone;
	private String deviceId;

}