package com.docuitservice.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Component
public class SMSUtils {

	private static final Logger logger = LoggerFactory.getLogger(SMSUtils.class);

	@Value("${aws.sns.message.accessKey}")
	private String messageAccessKey;

	@Value("${aws.sns.message.secretKey}")
	private String messageSecretKey;

	@Value("${aws.sns.message.senderId}")
	public String senderId;
	
	@Value("${twilio.accessKey}")
	private String tiwlioAccessKey;

	@Value("${twilio.secretKey}")
	private String tiwlioSecretKey;
	
	@Value("${twilio.phoneNumber}")
	private String tiwlioPhoneNumber;
	
	@SuppressWarnings("deprecation")
	public boolean sendAWSSMS(Map<String, Object> notificationMap) {
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
			logger.info("UserServiceImpl sendVerificationOTP----starts----result: "+ result);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean sendSMS(Map<String, Object> notificationMap) {
		try {
			
			String message = (String) notificationMap.get("message");			
			String phoneNumber = DockItConstants.SMS_DEFAULT_COUNTRY_CODE + (String) notificationMap.get("phoneNumber");
			Twilio.init(tiwlioAccessKey, tiwlioSecretKey);

			Message.creator(new PhoneNumber(phoneNumber),
					new PhoneNumber(tiwlioPhoneNumber),message).create();

			logger.info("UserServiceImpl sendVerificationOTP----starts----result: ");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
