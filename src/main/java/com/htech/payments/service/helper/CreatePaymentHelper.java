package com.htech.payments.service.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.htech.payments.constant.Constant;
import com.htech.payments.http.HttpRequest;
import com.htech.payments.pojo.CreatePaymentRequest;
import com.htech.payments.pojo.LineItem;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CreatePaymentHelper {

	@Value("${stripe.secret.key}")
	private String secretKey;
	
	

	public HttpRequest prepareStripeCreateSessionRequest(CreatePaymentRequest createPaymentReq) {
		log.info("CreatePaymentHelper.prepareStripeCreateSessionRequest...createPaymentReq: {} ",createPaymentReq);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(
				secretKey,"");
		
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		
		
		// Form data
		MultiValueMap<String, String> requestBody =
		        new LinkedMultiValueMap<>();

		requestBody.add("success_url", createPaymentReq.getSuccessUrl());
		requestBody.add("cancel_url", createPaymentReq.getCancelUrl());
		requestBody.add("mode", "payment");


        // Line items
        if (createPaymentReq.getLineItems() != null && !createPaymentReq.getLineItems().isEmpty()) {

            for (int i = 0; i < createPaymentReq.getLineItems().size(); i++) {

                LineItem item = createPaymentReq.getLineItems().get(i);

                String baseKey = Constant.LINE_ITEMS + Constant.OPEN_BRACKET + i + Constant.CLOSE_BRACKET;

                requestBody.add(baseKey + Constant.OPEN_BRACKET + Constant.QUANTITY + Constant.CLOSE_BRACKET, String.valueOf(item.getQuantity()));
                requestBody.add(baseKey + Constant.OPEN_BRACKET + Constant.PRICE_DATA + Constant.CLOSE_BRACKET + Constant.OPEN_BRACKET + Constant.CURRENCY + Constant.CLOSE_BRACKET, item.getCurrency());
                requestBody.add(baseKey + Constant.OPEN_BRACKET + Constant.PRICE_DATA + Constant.CLOSE_BRACKET + Constant.OPEN_BRACKET + Constant.UNIT_AMOUNT + Constant.CLOSE_BRACKET, String.valueOf(item.getUnitAmount()));
                requestBody.add(baseKey + Constant.OPEN_BRACKET + Constant.PRICE_DATA + Constant.CLOSE_BRACKET + Constant.OPEN_BRACKET + Constant.PRODUCT_DATA + Constant.CLOSE_BRACKET + Constant.OPEN_BRACKET + Constant.NAME + Constant.CLOSE_BRACKET, item.getProductName());
            }
        }
        
        log.info("CreatePaymentHel.perprepareStripeCreateSessionRequest..."
        		+ " Prepared request body for Stripe create session: {} ",requestBody);
		
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl("https://api.stripe.com/v1/checkout/sessions");
		httpRequest.setRequestData(requestBody);
		httpRequest.setHeaders(headers);
		log.info("CreatePaymentHel.perprepareStripeCreateSessionRequest..."
				+ "Prepared HttpRequest for Stripe create session: {} ",httpRequest);
		
		return httpRequest;
		
	}
}
