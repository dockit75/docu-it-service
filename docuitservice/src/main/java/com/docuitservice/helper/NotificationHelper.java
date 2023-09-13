package com.docuitservice.helper;

import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.docuitservice.util.DockItConstants;
import com.docuitservice.util.MailUtils;
import com.docuitservice.util.SMSUtils;

@Component
public class NotificationHelper {

	@Autowired
	MailUtils mailUtils;

	@Autowired
	SMSUtils smsUtils;

	public boolean sendNotification(String type, Map<String, Object> notificationMap) throws IOException {
		if (type.equalsIgnoreCase(DockItConstants.NOTIFICATION_MAIL_TYPE)) {
			return mailUtils.sendMail(notificationMap);

		} else if (type.equalsIgnoreCase(DockItConstants.NOTIFICATION_SMS_TYPE)) {
			return smsUtils.sendSMS(notificationMap);
		}
		return false;
	}
}
