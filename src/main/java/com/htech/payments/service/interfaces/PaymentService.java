package com.htech.payments.service.interfaces;

import org.springframework.http.ResponseEntity;

import com.htech.payments.pojo.CreatePaymentRequest;

public interface PaymentService {
	ResponseEntity<String> createPayment(CreatePaymentRequest createPaymentRequest);

}
