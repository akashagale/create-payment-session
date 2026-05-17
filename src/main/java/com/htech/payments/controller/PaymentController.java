package com.htech.payments.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.htech.payments.pojo.CreatePaymentRequest;
import com.htech.payments.pojo.PaymentResponse;
import com.htech.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/payments")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
	
	private final PaymentService paymentService;

	@PostMapping
	public PaymentResponse createPayment(@RequestBody CreatePaymentRequest createPaymentRequest) {
		log.info("Creating payment createPaymentRequest: {} ",createPaymentRequest);
		ResponseEntity<String> payment = paymentService.createPayment(createPaymentRequest);
		log.info("Creating payment: {}" ,payment);
		return null;
	}
}
