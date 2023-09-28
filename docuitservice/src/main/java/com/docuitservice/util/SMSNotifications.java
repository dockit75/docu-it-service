package com.docuitservice.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

public class SMSNotifications {

	public static void main(String[] args) {
        String accessKey = "AKIAYNN3IWMXH2ABQH77";
        String secretKey = "FGDsCjqJf1Mqut8IUXjUtlBIyrQpQfzxWkX1FMjh";
        String phoneNumber = "+918608711939"; // Replace with the recipient's phone number
        String message = "Hello from Amazon SNS!";        
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonSNS snsClient = AmazonSNSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_SOUTH_1) // Replace with the appropriate AWS region
                .build();        
        PublishRequest publishRequest = new PublishRequest()
                .withPhoneNumber(phoneNumber)
                .withMessage(message);        
        PublishResult publishResult = snsClient.publish(publishRequest);
        System.out.println("Message sent. Message ID: " + publishResult.getMessageId());        
    }
	
}
