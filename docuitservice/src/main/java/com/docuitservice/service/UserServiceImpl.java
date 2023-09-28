package com.docuitservice.service;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.docuitservice.exception.BusinessException;
import com.docuitservice.helper.NotificationHelper;
import com.docuitservice.util.DockItConstants;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
@PropertySource(ignoreResourceNotFound = false, value = "classpath:mail.properties")
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private Configuration config;

	@Value("${mail.verifyEmailCode.subject}")
	private String verifyMailCodeSubject;

	@Value("${mail.verifyEmailCode.template}")
	private String verifyMailOtpTemplate;
	
	@Autowired
	NotificationHelper notificationHelper;

	@Value("${sms.verifyOTP.template}")
	private String verificationOTPTemplate;
	@Override
	public void sendVerificationCode(String email, String otp, String name) throws Exception {
		logger.info("UserServiceImpl sendVerificationCode----starts---- email: {}, otp: {}, name: {}", email, otp,
				name);
		Map<String, Object> mailMap = new HashMap<>();
		Map<String, Object> notificationMap = new HashMap<>();
		mailMap.put("name", name);
		mailMap.put("otp", otp);
		Template mailTemplate = config.getTemplate(verifyMailOtpTemplate);
		String mailTemplateHtml = FreeMarkerTemplateUtils.processTemplateIntoString(mailTemplate, mailMap);
		notificationMap.put("userMail", email);
		notificationMap.put("subject", verifyMailCodeSubject);
		notificationMap.put("html", mailTemplateHtml);
		boolean isMailSent = notificationHelper.sendNotification(DockItConstants.NOTIFICATION_MAIL_TYPE,
				notificationMap);
		if (!isMailSent) {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, DockItConstants.SERVER_ERROR,
					DockItConstants.RESPONSE_EMPTY_DATA, 500);
		}
		logger.info("UserServiceImpl sendVerificationCode----ends----");
	}

	@Override
	public void sendVerificationOTP(String phone, String otp) throws Exception {
		logger.info("UserServiceImpl sendVerificationOTP----starts----phone: {}, otp: {}", phone, otp);
		Map<String, Object> mailMap = new HashMap<>();
		mailMap.put("otp", otp);
		Template mailTemplate = config.getTemplate(verificationOTPTemplate);
		String message = FreeMarkerTemplateUtils.processTemplateIntoString(mailTemplate, mailMap);
		Map<String, Object> notificationMap = new HashMap<>();
		notificationMap.put("message", message);
		notificationMap.put("phoneNumber", phone);
		notificationMap.put("smsType", DockItConstants.SMS_TRANSCTION_TYPE);
		boolean isSmsSent = notificationHelper.sendNotification(DockItConstants.NOTIFICATION_SMS_TYPE, notificationMap);
		if (!isSmsSent) {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, DockItConstants.SERVER_ERROR,
					DockItConstants.RESPONSE_EMPTY_DATA, 500);
		}
		logger.info("UserServiceImpl sendVerificationOTP----ends----");
	}
}
