package com.htech.payments.util;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JsonUtil {
	
	private final ObjectMapper objectMapper;

	public <T> T convertJsonToObject(String json, Class<T> clazz) {
		if (json == null || json.isEmpty()) {
			return null;
		}
		
		if (clazz == null) {
			return null;
		}
		
		try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Failed to convert JSON to {}: {}", clazz.getSimpleName(), e.getMessage(), e);
            return null;
        }
		
	}
}