package com.htech.payments.service.interfaces;

import org.springframework.http.ResponseEntity;

import com.htech.payments.pojo.CreatePaymentRequest;
import com.htech.payments.pojo.PaymentResponse;

public interface PaymentService {
	PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest);

}
