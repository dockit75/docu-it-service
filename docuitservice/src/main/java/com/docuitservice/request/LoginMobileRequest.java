package com.docuitservice.request;

import lombok.Data;

@Data
public class LoginMobileRequest {

	private String phoneNumber;
	private String password;

}
