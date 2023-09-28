package com.docuitservice.service;

import org.springframework.web.multipart.MultipartFile;

import com.docuitservice.request.LoginRequest;
import com.docuitservice.request.SignUpRequest;
import com.docuitservice.request.UpdateProfileRequest;
import com.docuitservice.util.Response;

import jakarta.validation.Valid;

public interface AuthService {

	public Response signUpUser(SignUpRequest signUpRequest) throws Exception;

	public Response verifyEmail(String email, String code) throws Exception;

	public Response resendCode(String email) throws Exception;

	public Response pinGeneration(String phone, String pinNumber) throws Exception;

	public Response verifyMobileOtp(String phone, String otp) throws Exception;

	public Response login(String phoneNumber, String password) throws Exception;

	public Response generateToken(String email) throws Exception;

	public Response updateProfile(@Valid UpdateProfileRequest updateProfileRequest) throws Exception;

	public Response forgotPin(String phoneNumber) throws Exception;

	public Response verifyPin(String phone, String verifyPin) throws Exception;

	public Response changePin(String phoneNumber, String pinNumber) throws Exception;

	public Response refreshToken(String token) throws Exception;

	public Response adminLogin(@Valid LoginRequest loginRequest) throws Exception;

	public Response resendOtp(String phone) throws Exception;
	
}
