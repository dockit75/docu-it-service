package com.docuitservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.docuitservice.helper.ResponseHelper;
import com.docuitservice.model.User;
import com.docuitservice.repository.UserRepository;
import com.docuitservice.util.DockItConstants;

import freemarker.template.Configuration;
import freemarker.template.Template;
import com.docuitservice.util.Response;

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
	
	@Value("${mail.externalInvite.subject}")
	private String externalInviteSubject;

	@Value("${mail.externalInvite.template}")
	private String externalInviteEmailTemplate;
	
	@Value("${sms.externalInvite.template}")
	private String externalInviteSmsTemplate;
	
	@Autowired
	private UserRepository userRepository;
	
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
	
	@Override
	public void sendEmailInvite(String email, String invitedBy) throws Exception {
		logger.info("UserServiceImpl sendEmailInvite----starts---- email: {}, invitedBy: {}", email, invitedBy);
		Map<String, Object> mailMap = new HashMap<>();
		Map<String, Object> notificationMap = new HashMap<>();
		mailMap.put("invitedBy", invitedBy);
		Template mailTemplate = config.getTemplate(externalInviteEmailTemplate);
		String mailTemplateHtml = FreeMarkerTemplateUtils.processTemplateIntoString(mailTemplate, mailMap);
		notificationMap.put("userMail", email);
		notificationMap.put("subject", externalInviteSubject);
		notificationMap.put("html", mailTemplateHtml);
		boolean isMailSent = notificationHelper.sendNotification(DockItConstants.NOTIFICATION_MAIL_TYPE,
				notificationMap);
		if (!isMailSent) {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, DockItConstants.SERVER_ERROR,
					DockItConstants.RESPONSE_EMPTY_DATA, 500);
		}
		logger.info("UserServiceImpl sendEmailInvite----ends----");
	}

	@Override
	public void sendSmsInvite(String phone, String invitedBy) throws Exception {
		logger.info("UserServiceImpl sendSmsInvite----starts----phone: {}, invitedBy: {}", phone, invitedBy);
		Map<String, Object> smsMap = new HashMap<>();
		smsMap.put("invitedBy", invitedBy);
		Template smsTemplate = config.getTemplate(externalInviteSmsTemplate);
		String message = FreeMarkerTemplateUtils.processTemplateIntoString(smsTemplate, smsMap);
		Map<String, Object> notificationMap = new HashMap<>();
		notificationMap.put("message", message);
		notificationMap.put("phoneNumber", phone);
		notificationMap.put("smsType", DockItConstants.SMS_TRANSCTION_TYPE);
		boolean isSmsSent = notificationHelper.sendNotification(DockItConstants.NOTIFICATION_SMS_TYPE, notificationMap);
		if (!isSmsSent) {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, DockItConstants.SERVER_ERROR,
					DockItConstants.RESPONSE_EMPTY_DATA, 500);
		}
		logger.info("UserServiceImpl sendSmsInvite----ends----");
		
	}
	
		public Response getUserDetails() throws Exception{
		List<User> user = new ArrayList<User>();
		user = userRepository.findByStatusAndIsAdmin(DockItConstants.ACTIVE,false);
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, user, 200,
				DockItConstants.RESPONSE_SUCCESS);
		}
	
}
