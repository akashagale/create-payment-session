package com.htech.payments.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.htech.payments.http.HttpRequest;
import com.htech.payments.http.HttpServiceEngine;
import com.htech.payments.pojo.CreatePaymentRequest;
import com.htech.payments.service.ValidationService;
import com.htech.payments.service.helper.CreatePaymentHelper;
import com.htech.payments.service.interfaces.PaymentService;
import com.htech.payments.stripe.CheckoutSessionResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	
	private final CreatePaymentHelper createPaymentHelper;
	
	private final HttpServiceEngine httpServiceEngine;
	
	private final ValidationService validationService;

	@Override
	public ResponseEntity<String> createPayment(CreatePaymentRequest createPaymentRequest) {
		log.info("PaymentServiceImpl.createPayment...createPaymentRequest: {} ",createPaymentRequest);
		
		// TODO - validate createPaymentRequest
		validationService.isValid(createPaymentRequest);
		
		
		HttpRequest httpRequest = createPaymentHelper.prepareStripeCreateSessionRequest(createPaymentRequest);
		
		
		ResponseEntity<String> response = httpServiceEngine.makeHttpCall(httpRequest);
		
		CheckoutSessionResponse checkoutSessionResponse= createPaymentHelper.processStripeResponse(response);
		
		return response;
	}
}
