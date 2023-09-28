package com.docuitservice.service;

public interface UserService {

	public void sendVerificationCode(String email, String otp, String name) throws Exception;

	public void sendVerificationOTP(String phone, String otp) throws Exception;
}
