package com.docuitservice.util;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.docuitservice.model.MailProperties;

@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailConfig {

	@Autowired
	private MailProperties mailProperties;

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(mailProperties.getHost());
		mailSender.setPort(mailProperties.getPort());
		mailSender.setUsername(mailProperties.getUsername());
		mailSender.setPassword(mailProperties.getPassword());

		Properties properties = mailSender.getJavaMailProperties();
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.from", mailProperties.getFrom());
		properties.setProperty("mail.alias", mailProperties.getAlias());
		return mailSender;
	}
}
