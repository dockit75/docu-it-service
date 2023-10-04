package com.docuitservice.service;

public interface UserService {

	public void sendVerificationCode(String email, String otp, String name) throws Exception;

	public void sendVerificationOTP(String phone, String otp) throws Exception;
		
	public void sendEmailInvite(String email,String Message) throws Exception;

	public void sendSmsInvite(String phone, String Message) throws Exception;
}
