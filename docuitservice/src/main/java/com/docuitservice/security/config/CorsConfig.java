package com.docuitservice.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/swagger-ui/**").allowedOrigins("http://localhost:8081")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH").allowedHeaders("*").exposedHeaders("*")
				.allowCredentials(false).maxAge(3600);
	}
}
