package com.docuitservice.request;

import lombok.Data;

@Data
public class VerifyMobileRequest {

	private String phone;
	private String otp;

}
