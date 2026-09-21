package com.htech.payments.http;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.htech.payments.constant.ErrorCodeEnum;
import com.htech.payments.exception.StripeProviderException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpServiceEngine {
	
	private final RestClient restClient;
	
	public ResponseEntity<String> makeHttpCall(HttpRequest httpRequest) {
		log.info("HttpServiceEngine.makeHttpCall...making call to external service "+httpRequest.getUrl());
		
		try {
			ResponseEntity<String> httpResponse = restClient
			.method(httpRequest.getHttpMethod())
			.uri(httpRequest.getUrl())
			.headers(headers -> headers.addAll(httpRequest.getHeaders()))
			.body(httpRequest.getRequestData())
			.retrieve()
			.toEntity(String.class);
			log.info("HttpServiceEngine.makeHttpCall...received response from external service "+httpResponse.getStatusCode());
			return httpResponse;
			
//			TODO - handle HttpClientErrorException and HttpServerErrorException 
		}catch (HttpClientErrorException | HttpServerErrorException ex) {
			log.error("HTTP error occurred while making HTTP call: Status code: {}, Response body: {}", 
					ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
			
			if(ex.getStatusCode()==HttpStatus.SERVICE_UNAVAILABLE ||
			   ex.getStatusCode()==HttpStatus.GATEWAY_TIMEOUT) {
				log.error("Stripe service is unavailable. Status code: {}, Response body: {}", 
						ex.getStatusCode(), ex.getResponseBodyAsString());
					
				throw new StripeProviderException(
						ErrorCodeEnum.ERROR_CONNECTING_TO_EXTERNAL_SERVICE.getErrorCode(),
						ErrorCodeEnum.ERROR_CONNECTING_TO_EXTERNAL_SERVICE.getErrorMessage(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}

			// prepare ResponseEntity with error details from the exception and return to the caller.
			ResponseEntity<String> errorResponse = ResponseEntity
					.status(ex.getStatusCode())
					.body(ex.getResponseBodyAsString());
			log.info("HttpServiceEngine.makeHttpCall...returning error response to caller "+errorResponse);
			return errorResponse;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Error occurred while making HTTP call: ", e);
			throw new StripeProviderException(
					ErrorCodeEnum.ERROR_CONNECTING_TO_EXTERNAL_SERVICE.getErrorCode(),
					ErrorCodeEnum.ERROR_CONNECTING_TO_EXTERNAL_SERVICE.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}