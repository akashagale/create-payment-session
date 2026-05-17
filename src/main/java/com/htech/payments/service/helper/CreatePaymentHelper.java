package com.htech.payments.service.helper;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.htech.payments.constant.Constant;
import com.htech.payments.constant.ErrorCodeEnum;
import com.htech.payments.exception.StripeProviderException;
import com.htech.payments.http.HttpRequest;
import com.htech.payments.pojo.CheckoutSessionResponse;
import com.htech.payments.pojo.CreatePaymentRequest;
import com.htech.payments.pojo.LineItem;
import com.htech.payments.stripe.StripeError;
import com.htech.payments.stripe.StripeErrorResponse;
import com.htech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreatePaymentHelper {

	@Value("${stripe_secret_key}")
	private String secretKey;
	
	private final JsonUtil jsonUtil;

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

	public CheckoutSessionResponse processStripeResponse(ResponseEntity<String> response) {
		
		log.info("CreatePaymentHelper.processStripeResponse...response: {} ",response);
		
		if(response.getStatusCode().is2xxSuccessful()) {
			CheckoutSessionResponse checkoutSessionResponse= jsonUtil.convertJsonToObject(response.getBody(), CheckoutSessionResponse.class);
			
			log.info("Converted CheckoutSessionResponse: {}", checkoutSessionResponse);
			if(checkoutSessionResponse != null && checkoutSessionResponse.getUrl() != null) {
				log.info("Stripe checkout session created successfully. Session ID: {}, Hosted Page URL: {}", 
						checkoutSessionResponse.getId(), checkoutSessionResponse.getUrl());
				return checkoutSessionResponse;
			}
			log.error("Failed to create Stripe checkout session. Invalid response body: {}", response.getBody());
		}
		
		if(response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
			log.error("API call Failed. Status code: {}, Response body: {}", 
					response.getStatusCode(), response.getBody());
			
			// convert error response body to StripeErrorResponse object and log the error details
			StripeErrorResponse stripeError = jsonUtil.convertJsonToObject(response.getBody(), StripeErrorResponse.class);
			if (stripeError != null && stripeError.getError() != null) {
				log.error("Stripe API error details: Type: {}, Code: {}, Message: {}", 
						stripeError.getError().getType(), 
						stripeError.getError().getCode(), 
						stripeError.getError().getMessage());
				
				
				String stripeConcatinatedErrorMessage = prepareStripeErrorMessage(stripeError);
				log.error("Prepared Stripe error message: {}", stripeConcatinatedErrorMessage);
				
				throw new StripeProviderException(
						ErrorCodeEnum.STRIPE_API_ERROR.getErrorCode(),// DONE
						stripeConcatinatedErrorMessage,// DONE
						HttpStatus.valueOf(response.getStatusCode().value()));// DONE
			}
			
			log.error("Stripe API call failed with non-JSON error response. Status code: {}, Response body: {}", 
					response.getStatusCode(), response.getBody());
		}
		// success object conversion failed
				// no url then also 
				// unable to parse error response body to StripeErrorResponse object

				throw new StripeProviderException(
						ErrorCodeEnum.INVALID_STRIPE_RESPONSE.getErrorCode(),
						ErrorCodeEnum.INVALID_STRIPE_RESPONSE.getErrorMessage(),
						HttpStatus.BAD_GATEWAY);// since stripe gave incorrect response, we can consider it as bad gateway. Its not our fault, its stripe's fault.
	}
	
	
	private String prepareStripeErrorMessage(StripeErrorResponse stripeErrorResponse) {

	    StripeError error = stripeErrorResponse.getError();

	    return Stream.of(
	                error.getType(),      // always present
	                error.getMessage(),
	                error.getParam(),
	                error.getCode()
	            )
	            .filter(Objects::nonNull)
	            .map(String::trim)
	            .filter(s -> !s.isEmpty())
	            .collect(Collectors.joining(" | "));
	}
}
