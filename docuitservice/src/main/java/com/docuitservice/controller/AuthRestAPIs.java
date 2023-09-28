package com.docuitservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.docuitservice.repository.UserRepository;
import com.docuitservice.request.LoginMobileRequest;
import com.docuitservice.request.LoginRequest;
import com.docuitservice.request.PinGenerationRequest;
import com.docuitservice.request.RefreshToken;
import com.docuitservice.request.SignUpRequest;
import com.docuitservice.request.UpdateProfileRequest;
import com.docuitservice.request.VerifyEmailRequest;
import com.docuitservice.request.VerifyMobileRequest;
import com.docuitservice.request.VerifyPinRequest;
import com.docuitservice.service.AuthService;
import com.docuitservice.util.Response;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthRestAPIs {

	public static final Logger logger = LoggerFactory.getLogger(AuthRestAPIs.class);

	@Autowired
	private AuthService authService;

	@Autowired
	UserRepository userRepository;

	@PostMapping("/signUp")
	public Response signUpUser(@RequestBody @Valid SignUpRequest signUpRequest) throws Exception {
		return authService.signUpUser(signUpRequest);
	}

	@PostMapping("/verifyEmail")
	public Response verifyEmail(@RequestBody VerifyEmailRequest verifyEmailRequest) throws Exception {
		return authService.verifyEmail(verifyEmailRequest.getEmail(), verifyEmailRequest.getCode());
	}

	@PostMapping("/resendCode")
	public Response resendCode(@RequestParam String email) throws Exception {
		return authService.resendCode(email);
	}

	@PostMapping("/resendOtp")
	public Response resendOtp(@RequestParam String phone) throws Exception {
		return authService.resendOtp(phone);
	}
	
	@PostMapping("/pinGeneration")
	public Response pinGeneration(@RequestBody @Valid PinGenerationRequest pinGenerationRequest) throws Exception {
		return authService.pinGeneration(pinGenerationRequest.getPhone(), pinGenerationRequest.getPinNumber());
	}

	@PostMapping("/verifyMobileOtp")
	public Response verifyMobileOtp(@RequestBody VerifyMobileRequest verifyMobileRequest) throws Exception {
		return authService.verifyMobileOtp(verifyMobileRequest.getPhone(), verifyMobileRequest.getOtp());
	}

	@PostMapping("/login")
	public Response login(@RequestBody @Valid LoginMobileRequest loginMobileRequest) throws Exception {
		return authService.login(loginMobileRequest.getPhoneNumber(), loginMobileRequest.getPassword());
	}

	@GetMapping("/generateToken")
	public Response generateToken(@RequestParam String email) throws Exception {
		return authService.generateToken(email);
	}

	@PostMapping("/refreshToken")
	public Response refreshToken(@RequestBody RefreshToken refreshToken) throws Exception {
		return authService.refreshToken(refreshToken.getToken());
	}

	@PutMapping("/updateProfile")
	public Response updateProfile(@RequestBody @Valid UpdateProfileRequest updateProfileRequest) throws Exception {
		return authService.updateProfile(updateProfileRequest);
	}

	@PostMapping("/forgotPin")
	public Response forgotPin(@RequestParam("phoneNumber") String phoneNumber) throws Exception {
		return authService.forgotPin(phoneNumber);
	}

	@PostMapping("/verifyPin")
	public Response verifyPin(@RequestBody @Valid VerifyPinRequest verifyPinRequest) throws Exception {
		return authService.verifyPin(verifyPinRequest.getPhone(), verifyPinRequest.getVerifyPin());
	}

	@PostMapping("/changePin")
	public Response changePin(@RequestBody @Valid PinGenerationRequest pinGenerationRequest) throws Exception {
		return authService.changePin(pinGenerationRequest.getPhone(), pinGenerationRequest.getPinNumber());
	}

	@PostMapping("/adminLogin")
	public Response adminLogin(@RequestBody @Valid LoginRequest loginRequest) throws Exception {
		return authService.adminLogin(loginRequest);
	}

}
