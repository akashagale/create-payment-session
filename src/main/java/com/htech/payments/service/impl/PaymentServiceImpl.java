package com.htech.payments.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.htech.payments.http.HttpRequest;
import com.htech.payments.http.HttpServiceEngine;
import com.htech.payments.pojo.CreatePaymentRequest;
import com.htech.payments.pojo.PaymentResponse;
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
	public PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest) {
		log.info("PaymentServiceImpl.createPayment...createPaymentRequest: {} ",createPaymentRequest);
		
		// TODO - validate createPaymentRequest
		validationService.isValid(createPaymentRequest);
		
		
		HttpRequest httpRequest = createPaymentHelper.prepareStripeCreateSessionRequest(createPaymentRequest);
		
		
		ResponseEntity<String> response = httpServiceEngine.makeHttpCall(httpRequest);
		
		CheckoutSessionResponse checkoutSession= createPaymentHelper.processStripeResponse(response);
		log.info("Processed Stripe response and obtained CheckoutSessionResponse: {}", checkoutSession);
		PaymentResponse paymentResponse = mapCheckoutSessionToPaymentResponse(checkoutSession);
		log.info("Mapped PaymentResponse: {}", paymentResponse);

		return paymentResponse;
	}
	
	
	/**
	 * Write a map method to take CheckoutSessionResponse 
	 * and convert it to PaymentResponse which is 
	 * our internal response object. This way we are not
	 */
	public PaymentResponse mapCheckoutSessionToPaymentResponse(
			CheckoutSessionResponse checkoutSession) {

		if (checkoutSession == null) {
			log.warn("mapCheckoutSessionToPaymentResponse called with null checkoutSession");
			return null;
		}

		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setStripeSeesionId(checkoutSession.getId());
		paymentResponse.setHostedPageUrl(checkoutSession.getUrl());

		log.info("Mapped CheckoutSessionResponse to PaymentResponse: {}", paymentResponse);
		return paymentResponse;
	}
}
