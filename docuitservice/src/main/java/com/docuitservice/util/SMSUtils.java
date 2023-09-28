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

@Component
public class SMSUtils {

	private static final Logger logger = LoggerFactory.getLogger(SMSUtils.class);

	@Value("${aws.sns.message.accessKey}")
	private String messageAccessKey;

	@Value("${aws.sns.message.secretKey}")
	private String messageSecretKey;

	@Value("${aws.sns.message.senderId}")
	public String senderId;

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
			logger.info("UserServiceImpl sendVerificationOTP----starts----result: "+ result);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
