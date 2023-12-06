package com.docuitservice.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.docuitservice.exception.BusinessException;
import com.docuitservice.helper.ResponseHelper;
import com.docuitservice.model.Document;
import com.docuitservice.model.ExternalInvite;
import com.docuitservice.model.User;
import com.docuitservice.model.UserRanking;
import com.docuitservice.repository.DocumentRepository;
import com.docuitservice.repository.ExternalInviteRepository;
import com.docuitservice.repository.UserRankingRepository;
import com.docuitservice.repository.UserRepository;
import com.docuitservice.request.ExternalInviteAcceptRequest;
import com.docuitservice.request.ExternalInviteRequest;
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
	
	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;	
	
	@Autowired
	private FamilyService familyService;
	
	@Autowired
	private ExternalInviteRepository externalInviteRepository;
	
	@Autowired
	DocumentRepository documentRepository;
	
	@Autowired
	UserRankingRepository userRankingRepository;

	@Override
	public Response signUpUser(SignUpRequest signUpRequest) throws Exception {
		logger.info("AuthServiceImpl signUpUser ---Start---");
		Util.validateRequiredField(signUpRequest.getName(), ErrorConstants.NAME_IS_REQUIRED);
		Util.validateRequiredField(signUpRequest.getEmail(), ErrorConstants.EMAIL_IS_REQUIRED);
		Util.validateRequiredField(signUpRequest.getGender(), ErrorConstants.GENDER_IS_REQUIRED);
		Util.validateRequiredField(signUpRequest.getPhone(), ErrorConstants.MOBILE_NUMBER_IS_REQUIRED);
//		Util.validateRequiredField(signUpRequest.getDeviceId(), ErrorConstants.DEVICE_ID_IS_REQUIRED);
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
		if (signUpRequest.getEmail() != null || signUpRequest.getPhone() != null) {
			if (userRepository.existsByEmailAndStatus(signUpRequest.getEmail(), DockItConstants.ACTIVE)) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.EMAIL_ALREADY_EXIST,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			if (userRepository.existsByPhoneAndStatus(signUpRequest.getPhone(), DockItConstants.ACTIVE)) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL,
						ErrorConstants.MOBILE_NUMBER_ALREADY_REGISTERED, ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
//			if (userRepository.existsByDeviceIdAndStatus(signUpRequest.getDeviceId(), DockItConstants.ACTIVE)) {
//				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DEVICE_ID_ALREADY_REGISTERED,
//						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
//			}
			User user = createUser(signUpRequest);
			userRepository.save(user);
			userService.sendVerificationCode(user.getEmail(), user.getOtp(), user.getName());
			if (user.getPhone() != null && !user.getPhone().isEmpty()) {
				userService.sendVerificationOTP(user.getPhone(), user.getOtp());
			}
			validateExternalInviteAndAccept(user);
			userCreateRanking(user);
		} else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_REQUEST,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("AuthServiceImpl signUpUser ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.USER_REGISTERED_SUCCESSFULLY, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	private void userCreateRanking(User user) {
		logger.info("AuthServiceImpl userCreateRanking ---Start---");
		UserRanking userRanking = userRankingRepository.findByUserId(user.getId());
		if (userRanking == null) {
			UserRanking userRankingVO = new UserRanking();
			userRankingVO.setId(UUID.randomUUID().toString());
			userRankingVO.setUserId(user.getId());
			userRankingVO.setInsuranceDocument(0);
			userRankingVO.setHealthDocument(0);
			userRankingVO.setAssertDocument(0);
			userRankingVO.setFinanceDocument(0);
			userRankingVO.setReferralInvite(20);
			userRankingRepository.save(userRankingVO);
		}
		logger.info("AuthServiceImpl userCreateRanking ---End---");
	}

	/**
	 * @param user
	 */
	private void validateExternalInviteAndAccept(User user) {
		ExternalInviteRequest externalInviteRequest =  new ExternalInviteRequest();
		if (user.getPhone() != null && !user.getPhone().isEmpty()) {
		externalInviteRequest.setPhone(user.getPhone());
		}
		/*
		 * if (user.getEmail() != null && !user.getEmail().isEmpty()) {
		 * externalInviteRequest.setEmail(user.getEmail()); }
		 */
		List<ExternalInvite> externalInvites = new ArrayList<ExternalInvite>();
		String inviteId = null;
		if(user.getPhone() != null && !user.getPhone().isEmpty()){
			externalInvites = externalInviteRepository.findByPhoneAndStatus(externalInviteRequest.getPhone(),true);
			}
		/*
		 * if(user.getEmail() != null && !user.getEmail().isEmpty()){ externalInvites =
		 * externalInviteRepository.findByEmailAndStatus(externalInviteRequest.getEmail(
		 * ),true); }
		 */
		if(!externalInvites.isEmpty() && externalInvites.size()>0) {
			for(ExternalInvite externalInvite: externalInvites)
			{
				if(StringUtils.hasText(externalInvite.getPhone()) && StringUtils.hasText(user.getId())) {
					ExternalInviteAcceptRequest	externalInviteAcceptRequest =  new ExternalInviteAcceptRequest();
					externalInviteAcceptRequest.setExternalInviteId(externalInvite.getId());
					externalInviteAcceptRequest.setUserId(user.getId());
					familyService.externalInviteAccept(externalInviteAcceptRequest);
				}
			}
		}
	}
	
	
	private User createUser(SignUpRequest signUpRequest) {
//		User userDetails = userRepository.findByDeviceId(signUpRequest.getDeviceId());
		User userDetails = userRepository.findByPhone(signUpRequest.getPhone());
		User user = new User();
		Date currentTimeStamp = new Date(System.currentTimeMillis());
		if (userDetails == null) {
			user.setId(UUID.randomUUID().toString());
		} else {
			user.setId(userDetails.getId());
			user.setEmail(userDetails.getEmail());
			user.setName(userDetails.getName());
			user.setGender(userDetails.getGender());
			user.setCreatedAt(userDetails.getCreatedAt());
			user.setUpdatedAt(currentTimeStamp);
			user.setStatus(userDetails.getStatus());
			user.setAdmin(userDetails.isAdmin());
			user.setStatus(DockItConstants.USER_CREATED);
			user.setDeviceId(userDetails.getDeviceId());
			user.setAdmin(false);
			user.setAccountVerified(false);
			user.setOtpCreatedAt(currentTimeStamp);
		}
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
		user.setOtpCreatedAt(currentTimeStamp);
		return user;
	}


	@Override
	public Response verifyEmail(String email, String code) throws Exception {
		logger.info("AuthServiceImpl verifyEmail ---Start---");
		Util.validateRequiredField(email, ErrorConstants.EMAIL_IS_REQUIRED);
		Util.validateRequiredField(code, ErrorConstants.CODE_IS_REQUIRED);
		if (!Util.isValidEmailIdFormat(email)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_EMAIL_ID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (!Util.isValidCodeFormat(code)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_CODE,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Optional<User> userDetail = userRepository.findByEmail(email);
		if (!userDetail.isPresent()) {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, DockItConstants.EMAIL_NOT_FOUND,
					DockItConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (email != null && code != null) {
			User user = userDetail.get();
			Date currentTimeStamp = new Date(System.currentTimeMillis());
			if (!user.getOtp().equals(code)) {
				throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.INVALID_CODE,
						DockItConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			boolean isCodeExpired = Util.validateOtpExpired(user.getOtpCreatedAt());
			if (!isCodeExpired) {
				user.setStatus(DockItConstants.EMAIL_OTP_VERIFIED);
			} else {
				user.setStatus(DockItConstants.MAIL_OTP_EXPIRED);
				throw new BusinessException(DockItConstants.RESPONSE_FAIL, DockItConstants.EMAIL_OTP_IS_EXPIRED,
						DockItConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			user.setUpdatedAt(currentTimeStamp);
			userRepository.save(user);		
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
				user.setOtp("" + Util.getRandomNumber());
				user.setOtpCreatedAt(currentTimeStamp);
				user.setUpdatedAt(currentTimeStamp);
				user.setStatus(DockItConstants.EMAIL_CODE_RESEND_STATUS);
				userRepository.save(user);
				userService.sendVerificationCode(user.getEmail(), user.getOtp(), user.getName());
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
	public Response resendOtp(String phone) throws Exception {
		Util.validateRequiredField(phone, ErrorConstants.PHONE_NUMBER_IS_REQUIRED);
		if (!Util.isValidPhoneNumberFormat(phone)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_PHONE_NUMBER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		User user = userRepository.findByPhone(phone);
		Date currentTimeStamp = new Date(System.currentTimeMillis());
		if (user != null) {
			user.setOtp("" + Util.getRandomNumber());
			user.setOtpCreatedAt(currentTimeStamp);
			user.setUpdatedAt(currentTimeStamp);
			user.setStatus(DockItConstants.MOBILE_OTP_RESEND_STATUS);
			userRepository.save(user);
			userService.sendVerificationOTP(user.getPhone(), user.getOtp());
		} else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.PHONE_NUMBER_NOT_FOUND,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("AuthServiceImpl resendCode ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.MOBILE_OTP_RESEND, "", 200,
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
		user.setPassword(bcryptEncoder.encode(pinNumber));
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
			boolean isOtpExpired = Util.validateOtpExpired(user.getOtpCreatedAt());
			if (!isOtpExpired) {
				user.setStatus(DockItConstants.MOBILE_OTP_VERIFIED);
			} else {
				user.setStatus(DockItConstants.MOBILE_OTP_EXPIRED);
				throw new BusinessException(DockItConstants.RESPONSE_FAIL, DockItConstants.MAIL_OTP_IS_EXPIRED,
						DockItConstants.RESPONSE_EMPTY_DATA, 1001);
			}
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
		return ResponseHelper.getSuccessResponse(DockItConstants.MOBILE_VERIFIED, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response login(String phoneNumber, String password) throws Exception {
		logger.info("AuthServiceImpl login ---Start---"); // Updated log message to "Start"
		Util.validateRequiredField(phoneNumber, ErrorConstants.PHONE_NUMBER_IS_REQUIRED);
		Util.validateRequiredField(password, ErrorConstants.PIN_NUMBER_IS_REQUIRED);
		if (!Util.isValiPinNumber(password)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_PIN_NUMBER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (!Util.isValidPhoneNumberFormat(phoneNumber)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_PHONE_NUMBER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Map<String, Object> responseObjectsMap = new HashMap<>();
		User user = userRepository.findByPhone(phoneNumber);
		if (user != null) {
			if (!user.isAdmin()) {
				if (!user.isAccountVerified()) {
					throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.ACCOUNT_NOT_VERIFIED,
							ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
				}
				if (user.getPassword() != null && bcryptEncoder.matches(password, user.getPassword())) {
					String token = jwtConfiguration.generateToken(user.getEmail());
					if (token != null) {
						responseObjectsMap.put("token", token);
						responseObjectsMap.put("userDetails", user);
					} else {
						throw new BusinessException(DockItConstants.RESPONSE_FAIL,
								ErrorConstants.TOKEN_GENERATION_FAILED, ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
					}
				} else {
					throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.INVALID_CREDENTIALS,
							ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
				}
			} else {
				throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.THIS_USER_NOT_ADMIN,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		} else {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.PHONE_NUMBER_NOT_FOUND,
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
		Util.validateRequiredField(updateProfileRequest.getUserId(), ErrorConstants.USER_ID_IS_REQUIRED);
		Util.validateRequiredField(updateProfileRequest.getGender(), ErrorConstants.GENDER_IS_REQUIRED);
//		Util.validateRequiredField(updateProfileRequest.getImageUrl(), ErrorConstants.PROFILE_IMAGE_FILE_IS_REQUIRED);		
		if (!Util.isValidNameFormat(updateProfileRequest.getName())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_NAME,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (!Util.isValidGender(updateProfileRequest.getGender())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_GENDER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Map<String, Object> responseObjectsMap = new HashMap<>();
		User user = userRepository.findById(updateProfileRequest.getUserId());
		if (user != null) {
			Date currentTimeStamp = new Date(System.currentTimeMillis());
			user.setName(updateProfileRequest.getName());
			user.setGender(updateProfileRequest.getGender());
			user.setUpdatedAt(currentTimeStamp);
			user.setImageUrl(updateProfileRequest.getImageUrl());
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
			String forgotPin = String.valueOf(Util.getRandomPinNumber());
			user.setPassword(bcryptEncoder.encode(forgotPin));
			user.setUpdatedAt(currentTimeStamp);
			userRepository.save(user);
			userService.sendVerificationCode(user.getEmail(), forgotPin, user.getName());
			// send OTP to registered mobile number here
			if (user.getPhone() != null && !user.getPhone().isEmpty()) {
				userService.sendVerificationOTP(user.getPhone(), forgotPin);
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
			if (user.getPassword() != null && bcryptEncoder.matches(verifyPin, user.getPassword())) {
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
			user.setPassword(bcryptEncoder.encode(pinNumber));
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
		if (!bcryptEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_CREDENTIALS,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
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

	@Override
	public Response getUserRanking(String userId) throws Exception {
		logger.info("AuthServiceImpl getUserRanking ---Start---");
		Util.validateRequiredField(userId, ErrorConstants.USER_ID_IS_REQUIRED);
		Integer userRanking = 0;
		UserRanking userRankingEntity = userRankingRepository.findByUserId(userId);
		if (userRankingEntity != null) {
			userRanking = userRankingRepository.calculateUserRanking(userId);
		} else {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.USER_DETAIL_NOT_FOUND,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Map<String, Object> responseObjectsMap = new HashMap<>();
		responseObjectsMap.put("userRanking", userRanking);
		logger.info("AuthServiceImpl getUserRanking ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, responseObjectsMap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}
	
	@Override
	public Response signUpUserRegistration(SignUpRequest signUpRequest) throws Exception {
		logger.info("AuthServiceImpl signUpUserRegistration ---Start---");
		Util.validateRequiredField(signUpRequest.getName(), ErrorConstants.NAME_IS_REQUIRED);
		Util.validateRequiredField(signUpRequest.getEmail(), ErrorConstants.EMAIL_IS_REQUIRED);
		Util.validateRequiredField(signUpRequest.getGender(), ErrorConstants.GENDER_IS_REQUIRED);
		Util.validateRequiredField(signUpRequest.getPhone(), ErrorConstants.MOBILE_NUMBER_IS_REQUIRED);

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
		if (signUpRequest.getEmail() != null || signUpRequest.getPhone() != null) {
			User userData = userRepository.findByEmailAndPhone(signUpRequest.getEmail(), signUpRequest.getPhone());
			if (userData != null) {
				if (!userData.getStatus().equalsIgnoreCase(DockItConstants.ACTIVE)) {
					Date currentTimeStamp = new Date(System.currentTimeMillis());
					userData.setUpdatedAt(currentTimeStamp);
					userData.setOtp(String.valueOf(Util.getRandomNumber()));
					userData.setOtpCreatedAt(currentTimeStamp);
					userRepository.save(userData);
					userService.sendVerificationCode(userData.getEmail(), userData.getOtp(), userData.getName());
					if (userData.getPhone() != null && !userData.getPhone().isEmpty()) {
						userService.sendVerificationOTP(userData.getPhone(), userData.getOtp());
					}
				} else {
					throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.THIS_USER_ALREADY_EXIST,
							ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
				}
			} else {
				User userSameWithPhone = userRepository.findByPhone(signUpRequest.getPhone());
				if (userSameWithPhone != null) {
					throw new BusinessException(ErrorConstants.RESPONSE_FAIL,
							ErrorConstants.MOBILE_NUMBER_ALREADY_REGISTERED, ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
				}
				Optional<User> userSameWithEmail = userRepository.findByEmail(signUpRequest.getEmail());
				if (userSameWithEmail.isPresent()) {
					User existingUserWithEmail = userSameWithEmail.get();
					if (existingUserWithEmail != null) {
						throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.EMAIL_ALREADY_EXIST,
								ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
					}
				}
				User user = userRegistration(signUpRequest);
				userRepository.save(user);
				userService.sendVerificationCode(user.getEmail(), user.getOtp(), user.getName());
				if (user.getPhone() != null && !user.getPhone().isEmpty()) {
					userService.sendVerificationOTP(user.getPhone(), user.getOtp());
				}
				validateExternalInviteAndAccept(user);
				userCreateRanking(user);
			}
		} else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_REQUEST,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("AuthServiceImpl signUpUser ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.USER_REGISTERED_SUCCESSFULLY, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}
	
	private User userRegistration(SignUpRequest signUpRequest) {
		User userDetails = userRepository.findByPhone(signUpRequest.getPhone());
		User user = new User();
		Date currentTimeStamp = new Date(System.currentTimeMillis());
		if (userDetails == null) {
			user.setId(UUID.randomUUID().toString());
		} else {
			user.setId(userDetails.getId());
			user.setEmail(userDetails.getEmail());
			user.setName(userDetails.getName());
			user.setGender(userDetails.getGender());
			user.setCreatedAt(userDetails.getCreatedAt());
			user.setUpdatedAt(currentTimeStamp);
			user.setStatus(userDetails.getStatus());
			user.setAdmin(userDetails.isAdmin());
			user.setStatus(DockItConstants.USER_CREATED);
			user.setDeviceId(userDetails.getDeviceId());
			user.setAdmin(false);
			user.setAccountVerified(false);
			user.setOtpCreatedAt(currentTimeStamp);
		}
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
		user.setOtpCreatedAt(currentTimeStamp);
		return user;
	}

}
