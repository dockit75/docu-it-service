package com.docuitservice.util;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Configuration
public class MailUtils {

	private static final Logger logger = LoggerFactory.getLogger(MailUtils.class);
	
	@Autowired
	private JavaMailSender sender;

	@Value("${mail.from}")
	public String fromAddress;

	@Value("${mail.alias}")
	public String alias;
	@Value("${aws.sns.message.accessKey}")
	private String messageAccessKey;
	@Value("${aws.sns.message.secretKey}")
	private String messageSecretKey;
	@Value("${aws.sns.message.senderId}")
	public String senderId;

	public boolean sendMail(Map<String, Object> notificationMap) {
		String userMail = (String) notificationMap.get("userMail");
		String subject = (String) notificationMap.get("subject");
		String html = (String) notificationMap.get("html");
		String cc = (String) notificationMap.get("cc");
		MimeMessage message = sender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			helper.setTo(userMail);
			if (cc != null) {
				helper.setCc(cc);
			}
			helper.setText(html, true);
			helper.setSubject(subject);
			message.setFrom(new InternetAddress(fromAddress, alias));
			sender.send(message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@SuppressWarnings("deprecation")
	public boolean sendSMS(Map<String, Object> notificationMap) {
		try {
			AWSCredentials awsCredentials = new BasicAWSCredentials(messageAccessKey, messageSecretKey);
			AmazonSNSClient snsClient = new AmazonSNSClient(awsCredentials);
			String message = (String) notificationMap.get("message");
			String phoneNumber = DockItConstants.SMS_DEFAULT_COUNTRY_CODE + (String) notificationMap.get("phoneNumber");
			Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
			smsAttributes.put("AWS.SNS.SMS.SenderID",
					new MessageAttributeValue().withStringValue(senderId).withDataType("String"));
			smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
					.withStringValue((String) notificationMap.get("smsType")).withDataType("String"));
			PublishResult result = snsClient.publish(new PublishRequest().withMessage(message)
					.withPhoneNumber(phoneNumber).withMessageAttributes(smsAttributes));
			logger.info("UserServiceImpl sendVerificationOTP----starts----result: {}", result);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
