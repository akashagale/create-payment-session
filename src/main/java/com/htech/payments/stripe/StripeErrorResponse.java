package com.htech.payments.stripe;

import lombok.Data;

@Data
public class StripeErrorResponse {
	
	private StripeError error;

}
