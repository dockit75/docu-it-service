package com.docuitservice.request;

import lombok.Data;

@Data
public class VerifyPinRequest {

	private String phone;
	private String verifyPin;

}
