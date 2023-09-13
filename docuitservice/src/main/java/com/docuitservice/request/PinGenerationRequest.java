package com.docuitservice.request;

import lombok.Data;

@Data
public class PinGenerationRequest {

	private String phone;
	private String pinNumber;

}
