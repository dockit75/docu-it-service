package com.docuitservice.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.docuitservice.exception.BusinessException;
import com.docuitservice.helper.ResponseHelper;
import com.docuitservice.model.User;
import com.docuitservice.repository.UserRepository;
import com.docuitservice.request.LoginRequest;
import com.docuitservice.request.SignUpRequest;
import com.docuitservice.request.UpdateProfileRequest;
import com.docuitservice.security.config.JwtConfiguration;
import com.docuitservice.util.DockItConstants;
import com.docuitservice.util.ErrorConstants;
import com.docuitservice.util.Response;
import com.docuitservice.util.Util;
import jakarta.validation.Valid;

@Service
public class AuthServiceImpl implements AuthService {

	private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtConfiguration jwtConfiguration;

	@Autowired
	private UserService userService;

	@Override
	public Response signUpUser(SignUpRequest signUpRequest) throws Exception {
		logger.info("AuthServiceImpl signUpUser ---Start---");
		Util.validateRequiredField(signUpRequest.getName(), ErrorConstants.NAME_IS_REQUIRED);
		Util.validateRequiredField(signUpRequest.getEmail(), ErrorConstants.EMAIL_IS_REQUIRED);
		Util.validateRequiredField(signUpRequest.getGender(), ErrorConstants.GENDER_IS_REQUIRED);
		Util.validateRequiredField(signUpRequest.getPhone(), ErrorConstants.MOBILE_NUMBER_IS_REQUIRED);
		Util.validateRequiredField(signUpRequest.getDeviceId(), ErrorConstants.DEVICE_ID_IS_REQUIRED);
		if (!Util.isValidNameFormat(signUpRequest.getName())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_NAME,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (!Util.isValidPhoneNumberFormat(signUpRequest.getPhone())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_PHONE_NUMBER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (!Util.isValidEmailIdFormat(signUpRequest.getEmail())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_EMAIL_ID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (!Util.isValidGender(signUpRequest.getGender())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_GENDER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (signUpRequest.getEmail() != null
				|| signUpRequest.getPhone() != null && signUpRequest.getDeviceId() != null) {
			if (userRepository.existsByEmail(signUpRequest.getEmail())) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.EMAIL_ALREADY_EXIST,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			if (userRepository.findByPhone(signUpRequest.getPhone()) != null) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL,
						ErrorConstants.MOBILE_NUMBER_ALREADY_REGISTERED, ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			if (userRepository.findByDeviceId(signUpRequest.getDeviceId()) != null) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DEVICE_ID_ALREADY_REGISTERED,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			// Create and save user
			User user = createUser(signUpRequest);
			userRepository.save(user);
			userService.sendVerificationCode(user.getEmail(), user.getOtp(), user.getName());
			if (user.getPhone() != null && !user.getPhone().isEmpty()) {
				userService.sendVerificationOTP(user.getPhone(), user.getOtp());
			}
		} else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_REQUEST,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}

		logger.info("AuthServiceImpl signUpUser ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.USER_REGISTERED_SUCCESSFULLY, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	private User createUser(SignUpRequest signUpRequest) {
		User user = new User();
		Date currentTimeStamp = new Date(System.currentTimeMillis());
		user.setId(UUID.randomUUID().toString());
		user.setEmail(signUpRequest.getEmail());
		user.setName(signUpRequest.getName());
		user.setGender(signUpRequest.getGender());
		user.setPhone(signUpRequest.getPhone());
		user.setCreatedAt(currentTimeStamp);
		user.setUpdatedAt(currentTimeStamp);
		user.setStatus(DockItConstants.USER_CREATED);
		user.setOtp(String.valueOf(Util.getRandomNumber()));
		user.setAdmin(false);
		user.setAccountVerified(false);
		user.setDeviceId(signUpRequest.getDeviceId());
		return user;
	}

	@Override
	public Response verifyEmail(String email, String code) throws Exception {
		logger.info("AuthServiceImpl verifyEmail ---Start---");
		Util.validateRequiredField(email, ErrorConstants.EMAIL_IS_REQUIRED);
		Util.validateRequiredField(code, ErrorConstants.CODE_IS_REQUIRED);
		Optional<User> userDetail = userRepository.findByEmail(email);
		if (!Util.isValidEmailIdFormat(email)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_EMAIL_ID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (!Util.isValidCodeFormat(code)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_CODE,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (!userDetail.isPresent()) {
			throw new BusinessException(DockItConstants.RESPONSE_SUCCESS, DockItConstants.EMAIL_NOT_FOUND,
					DockItConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (email != null && code != null) {
			User user = userDetail.get();
			if (!user.getOtp().equals(code)) {
				throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.INVALID_CODE,
						DockItConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			boolean isCodeExpired = Util.validateOtpExpired(user.getCreatedAt());
			if (isCodeExpired) {
				return ResponseHelper.getSuccessResponse(DockItConstants.OTP_EXPIRED,
						DockItConstants.RESPONSE_EMPTY_DATA, 200, DockItConstants.RESPONSE_SUCCESS);
			}
		} else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_REQUEST,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("AuthServiceImpl verifyEmail ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.EMAIL_VERIFIED, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response resendCode(String email) throws Exception {
		logger.info("AuthServiceImpl resendCode ---Start---");
		Util.validateRequiredField(email, ErrorConstants.EMAIL_IS_REQUIRED);
		if (!Util.isValidEmailIdFormat(email)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_EMAIL_ID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Optional<User> userDetail = userRepository.findByEmail(email);
		if (userDetail.isPresent()) {
			Date currentTimeStamp = new Date(System.currentTimeMillis());
			User user = userDetail.get();
			if (user != null) {
				boolean isCodeExpired = Util.validateOtpExpired(user.getCreatedAt());
				if (!isCodeExpired) {
					user.setCreatedAt(currentTimeStamp);
					user.setUpdatedAt(currentTimeStamp);
					userRepository.save(user);
					userService.sendVerificationCode(user.getEmail(), user.getOtp(), user.getName());
				} else {
					user.setOtp("" + Util.getRandomNumber());
					user.setCreatedAt(currentTimeStamp);
					user.setUpdatedAt(currentTimeStamp);
					userRepository.save(user);
					userService.sendVerificationCode(user.getEmail(), user.getOtp(), user.getName());
				}
			}
		} else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_DETAILS_NOT_FOUND,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("AuthServiceImpl resendCode ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.EMAIL_OTP_RESEND, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response pinGeneration(String phone, String pinNumber) throws Exception {
		logger.info("AuthServiceImpl pinGeneration --- Start ---");
		Util.validateRequiredField(phone, ErrorConstants.PHONE_NUMBER_IS_REQUIRED);
		Util.validateRequiredField(pinNumber, ErrorConstants.PIN_NUMBER_IS_REQUIRED);
		if (!Util.isValidPhoneNumberFormat(phone)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_PHONE_NUMBER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (!Util.isValiPinNumber(pinNumber)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_PIN_NUMBER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		User user = userRepository.findByPhone(phone);
		if (user == null) {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.PHONE_NUMBER_NOT_FOUND,
					DockItConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (user.getPassword() != null) {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.PIN_NUMBER_ALREADY_REGISTERED,
					DockItConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Date currentTimeStamp = new Date(System.currentTimeMillis());
		user.setPassword(pinNumber);
		user.setStatus(DockItConstants.ACTIVE);
		user.setAccountVerified(true);
		user.setUpdatedAt(currentTimeStamp);
		userRepository.save(user);

		logger.info("AuthServiceImpl pinGeneration ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.PIN_GENERATED_SUCCESSFULLY, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response verifyMobileOtp(String phone, String otp) throws Exception {
		logger.info("AuthServiceImpl verifyMobileOtp --- Start ---");
		Util.validateRequiredField(phone, ErrorConstants.PHONE_NUMBER_IS_REQUIRED);
		Util.validateRequiredField(otp, ErrorConstants.OTP_IS_REQUIRED);
		if (!Util.isValidPhoneNumberFormat(phone)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_PHONE_NUMBER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (!Util.isValidCodeFormat(otp)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_OTP,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		User user = userRepository.findByPhone(phone);
		if (user != null) {
			Date currentTimeStamp = new Date(System.currentTimeMillis());
			user.setStatus(DockItConstants.OTP_VERIFIED);
			user.setUpdatedAt(currentTimeStamp);
			userRepository.save(user);
		} else {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.PHONE_NUMBER_NOT_FOUND,
					DockItConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (!user.getOtp().equalsIgnoreCase(otp)) {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.INVALID_OTP,
					DockItConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("AuthServiceImpl verifyMobileOtp ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.OTP_VERIFIED, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response login(String deviceId, String password) throws Exception {
		logger.info("AuthServiceImpl login ---End---");
		Util.validateRequiredField(deviceId, ErrorConstants.DEVICE_ID_IS_REQUIRED);
		Util.validateRequiredField(password, ErrorConstants.PIN_NUMBER_IS_REQUIRED);
		if (!Util.isValiPinNumber(password)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_PIN_NUMBER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Map<String, Object> responseObjectsMap = new HashMap<>();
		User user = userRepository.findByDeviceIdAndPassword(deviceId, password);
		if (user != null && !user.isAdmin()) {
			if (!user.isAccountVerified()) {
				throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.ACCOUNT_NOT_VERIFIED,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			if (user.getPassword() != null && !user.getPassword().equals(password)) {
				throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.INVALID_CREDENTIALS,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			String token = jwtConfiguration.generateToken(user.getEmail());
			if (token != null) {
				responseObjectsMap.put("token", token);
				responseObjectsMap.put("userDetails", user);
			} else {
				throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.TOKEN_GENERATION_FAILED,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		} else {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.INVALID_CREDENTIALS,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("AuthServiceImpl login ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, responseObjectsMap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response generateToken(String email) throws Exception {
		logger.info("AuthServiceImpl generateToken ---Start---");
		Util.validateRequiredField(email, ErrorConstants.EMAIL_IS_REQUIRED);
		if (!Util.isValidEmailIdFormat(email)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_EMAIL_ID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Map<String, Object> responseObjectsMap = new HashMap<>();
		Optional<User> userDetail = userRepository.findByEmail(email);
		if (userDetail.isPresent()) {
			User user = userDetail.get();
			if (user != null) {
				String refreshToken = jwtConfiguration.generateToken(email);
				responseObjectsMap.put("token", refreshToken);
			}
		} else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_DETAILS_NOT_FOUND,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("AuthServiceImpl generateToken ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, responseObjectsMap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response updateProfile(@Valid UpdateProfileRequest updateProfileRequest) throws Exception {
		logger.info("AuthServiceImpl updateProfile ---Start---");
		Util.validateRequiredField(updateProfileRequest.getName(), ErrorConstants.NAME_IS_REQUIRED);
		Util.validateRequiredField(updateProfileRequest.getEmail(), ErrorConstants.EMAIL_IS_REQUIRED);
		if (!Util.isValidNameFormat(updateProfileRequest.getName())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_NAME,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (!Util.isValidEmailIdFormat(updateProfileRequest.getEmail())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_EMAIL_ID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Map<String, Object> responseObjectsMap = new HashMap<>();
		Optional<User> userDetail = userRepository.findByEmail(updateProfileRequest.getEmail());
		if (userDetail.isPresent()) {
			User user = userDetail.get();
			Date currentTimeStamp = new Date(System.currentTimeMillis());
			user.setName(updateProfileRequest.getName());
			user.setEmail(updateProfileRequest.getEmail());
			user.setUpdatedAt(currentTimeStamp);
			userRepository.save(user);
			responseObjectsMap.put("userDetails", user);
		} else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_DETAILS_NOT_FOUND,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("AuthServiceImpl updateProfile ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, responseObjectsMap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response forgotPin(String phoneNumber) throws Exception {
		logger.info("AuthServiceImpl forgotPin ---Start---");
		Util.validateRequiredField(phoneNumber, ErrorConstants.PHONE_NUMBER_IS_REQUIRED);
		if (!Util.isValidPhoneNumberFormat(phoneNumber)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_PHONE_NUMBER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		User user = userRepository.findByPhone(phoneNumber);
		if (user != null) {
			Date currentTimeStamp = new Date(System.currentTimeMillis());
			user.setPassword(String.valueOf(Util.getRandomPinNumber()));
			user.setUpdatedAt(currentTimeStamp);
			userRepository.save(user);
			userService.sendVerificationCode(user.getEmail(), user.getPassword(), user.getName());
			// send OTP to registered mobile number here
			if (user.getPhone() != null && !user.getPhone().isEmpty()) {
				userService.sendVerificationOTP(user.getPhone(), user.getPassword());
			}
		} else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.PHONE_NUMBER_NOT_FOUND,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("AuthServiceImpl forgotPin ---Start---");
		return ResponseHelper.getSuccessResponse(DockItConstants.FORGOT_PIN_RESET_SUCCESSFULLY, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response verifyPin(String phone, String verifyPin) throws Exception {
		logger.info("AuthServiceImpl verifyPin ---Start---");
		Util.validateRequiredField(phone, ErrorConstants.PHONE_NUMBER_IS_REQUIRED);
		Util.validateRequiredField(verifyPin, ErrorConstants.VERIFY_PIN_IS_REQUIRED);
		if (!Util.isValidPhoneNumberFormat(phone)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_PHONE_NUMBER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (!Util.isValiPinNumber(verifyPin)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_VERIFY_PIN_NUMBER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (phone != null && verifyPin != null) {
			User user = userRepository.findByPhone(phone);
			if (user == null) {
				throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.PHONE_NUMBER_NOT_FOUND,
						DockItConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			if (user.getPassword().equals(verifyPin)) {
				logger.info("AuthServiceImpl verifyPin ---End---");
				return ResponseHelper.getSuccessResponse(DockItConstants.PIN_VERIFIED_SUCCESSFULLY, "", 200,
						DockItConstants.RESPONSE_SUCCESS);
			} else {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, DockItConstants.INVALID_PIN,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		} else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_REQUEST,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
	}


	@Override
	public Response changePin(String phoneNumber, String pinNumber) throws Exception {
		logger.info("AuthServiceImpl changePin ---Start---");
		Util.validateRequiredField(phoneNumber, ErrorConstants.PHONE_NUMBER_IS_REQUIRED);
		Util.validateRequiredField(pinNumber, ErrorConstants.NEW_PIN_IS_REQUIRED);
		if (!Util.isValidPhoneNumberFormat(phoneNumber)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_PHONE_NUMBER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (!Util.isValiPinNumber(pinNumber)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_VERIFY_PIN_NUMBER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		User user = userRepository.findByPhone(phoneNumber);
		if (user != null && !user.isAdmin()) {
			Date currentTimeStamp = new Date(System.currentTimeMillis());
			user.setPassword(pinNumber);
			user.setUpdatedAt(currentTimeStamp);
			userRepository.save(user);
		} else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.PHONE_NUMBER_NOT_FOUND,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("AuthServiceImpl changePin ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.PIN_CHANGED_SUCCESSFULLY, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response refreshToken(String token) {
		logger.info("AuthServiceImpl refreshToken ---Start---");
		Util.validateRequiredField(token, ErrorConstants.TOKEN_IS_REQUIRED);
		Map<String, Object> responseObjectsMap = new HashMap<>();
		boolean isTokenExpired = true;
		try {
			isTokenExpired = jwtConfiguration.isTokenExpired(token);
			responseObjectsMap.put("tokenExpired", isTokenExpired);
		} catch (Exception ex) {
			responseObjectsMap.put("tokenExpired", isTokenExpired);
			return ResponseHelper.getErrorResponse(DockItConstants.FETCH_DATA, responseObjectsMap, 1001,
					DockItConstants.RESPONSE_FAIL);
		}
		logger.info("AuthServiceImpl refreshToken ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, responseObjectsMap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response adminLogin(@Valid LoginRequest loginRequest) throws Exception {
		logger.info("AuthServiceImpl adminLogin ---Start---");
		Util.validateRequiredField(loginRequest.getEmail(), ErrorConstants.EMAIL_IS_REQUIRED);
		Util.validateRequiredField(loginRequest.getPassword(), ErrorConstants.PASSWORD_IS_REQUIRED);
		Optional<User> userDetail = userRepository.findByEmail(loginRequest.getEmail());
		if (!Util.isValidEmailIdFormat(loginRequest.getEmail())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_EMAIL_ID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (userDetail.isEmpty()) {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.USER_DETAIL_NOT_FOUND,
					DockItConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		User user = userDetail.get();
		if (!user.getPassword().equals(loginRequest.getPassword())) {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.INVALID_CREDENTIALS,
					DockItConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (!user.isAdmin()) {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.THIS_USER_NOT_ADMIN,
					DockItConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		String token = jwtConfiguration.generateToken(user.getEmail());
		if (token == null) {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.TOKEN_GENERATION_FAILED,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Map<String, Object> responseObjectsMap = new HashMap<>();
		responseObjectsMap.put("token", token);
		responseObjectsMap.put("userDetails", user);
		logger.info("AuthServiceImpl adminLogin ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, responseObjectsMap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

}
