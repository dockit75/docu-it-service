package com.docuitservice.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "mail")
@Data
public class MailProperties {

	private String host;
	private int port;
	private String username;
	private String password;
	private String from;
	private String alias;

}
