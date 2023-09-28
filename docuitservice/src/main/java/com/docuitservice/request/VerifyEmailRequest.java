package com.docuitservice.request;

import lombok.Data;

@Data
public class VerifyEmailRequest {

	private String email;
	private String code;

}
