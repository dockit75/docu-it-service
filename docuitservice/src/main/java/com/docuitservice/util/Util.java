package com.docuitservice.util;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.docuitservice.exception.BusinessException;

import io.micrometer.common.util.StringUtils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class Util {

	private static final Logger logger = LoggerFactory.getLogger(Util.class);

	private static final Random random = new Random();

	public static int getRandomNumber() {
		return random.nextInt(90000) + 10000;
	}

	public static int getRandomPinNumber() {
		return random.nextInt(9000) + 1000;
	}

	public static Timestamp getCurrentTimeStamp() {
		java.util.Date date = new java.util.Date();
		return new Timestamp(date.getTime());
	}

	public static boolean validateOtpExpired(Date createdAt) {
		int codeExpirationTimeInMinutes = 60; // Set the expiration time to 1 hour (60 minutes)
		boolean isExpired = false;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(createdAt.getTime());
		cal.add(Calendar.MINUTE, codeExpirationTimeInMinutes);
		Timestamp codeExpireTime = new Timestamp(cal.getTime().getTime());
		Timestamp currentTime = getCurrentTimeStamp();
		if (currentTime.after(codeExpireTime)) {
			isExpired = true;
		}
		logger.info("isExpired: {}", isExpired);
		return isExpired;
	}

	public static void validateRequiredField(String field, String errorMessage) throws BusinessException {
		if (StringUtils.isEmpty(field) || StringUtils.isBlank(field) || field == null) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, errorMessage, ErrorConstants.RESPONSE_EMPTY_DATA,
					1001);
		}
	}

	public static boolean isValidNameFormat(String name) {
		Pattern pattern = Pattern.compile("^[a-zA-Z\\s]*$");
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();
	}

	public static boolean isValidPhoneNumberFormat(String phone) {
		Pattern pattern = Pattern.compile("^[0-9]{10,11}$");
		Matcher matcher = pattern.matcher(phone);
		return matcher.matches();
	}

	public static boolean isValidOTPFormat(String phone) {
		Pattern pattern = Pattern.compile("^[0-9]{5}$");
		Matcher matcher = pattern.matcher(phone);
		return matcher.matches();
	}

	public static boolean isValidEmailIdFormat(String email) {
		Pattern pattern = Pattern.compile("^[A-Za-z0-9_.]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public static boolean isValidGender(String gender) {
		return "Male".equalsIgnoreCase(gender) || "Female".equalsIgnoreCase(gender);
	}

	public static boolean isValidCodeFormat(String code) {
		Pattern pattern = Pattern.compile("^[0-9]{5}$");
		Matcher matcher = pattern.matcher(code);
		return matcher.matches();
	}

	public static boolean isValiPinNumber(String pinNumber) {
		Pattern pattern = Pattern.compile("^[0-9]{4}$");
		Matcher matcher = pattern.matcher(pinNumber);
		return matcher.matches();
	}
	
	 public static boolean isAlphaNumeric(String s) {
	        return s != null && s.matches("^[a-zA-Z0-9]*$");
	    }
}
