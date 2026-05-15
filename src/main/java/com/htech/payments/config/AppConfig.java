package com.htech.payments.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AppConfig {

	@Bean
	public RestClient getRestClient(RestClient.Builder builder) {
		log.info(builder.toString());
		return builder.build();
	}
}
