package com.htech.payments.pojo;

import lombok.Data;

@Data
public class PaymentResponse {

	private String stripeSeesionId;
	private String hostedPageUrl;
	
}