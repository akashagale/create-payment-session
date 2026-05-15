package com.htech.payments.service;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.htech.payments.constant.ErrorCodeEnum;
import com.htech.payments.exception.StripeProviderException;
import com.htech.payments.pojo.CreatePaymentRequest;
import com.htech.payments.pojo.LineItem;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ValidationService {

	public void isValid(CreatePaymentRequest request) {
		
		if(request == null) {
			throw new StripeProviderException(
					ErrorCodeEnum.CREATE_PAYMENT_REQUEST_NULL.getErrorCode(), 
					ErrorCodeEnum.CREATE_PAYMENT_REQUEST_NULL.getErrorMessage(), 
					HttpStatus.BAD_REQUEST);
		}
		
		if (request.getSuccessUrl() == null || request.getSuccessUrl().isEmpty()) {
			
			throw new StripeProviderException(
			 ErrorCodeEnum.SUCCESS_URL_MISSING.getErrorCode(),
			 ErrorCodeEnum.SUCCESS_URL_MISSING.getErrorMessage(), 
			 HttpStatus.BAD_REQUEST);
		}
		
		
		if (request.getCancelUrl() == null || request.getCancelUrl().isEmpty()) {
			
			throw new StripeProviderException(
			 ErrorCodeEnum.CANCEL_URL_MISSING.getErrorCode(),
			 ErrorCodeEnum.CANCEL_URL_MISSING.getErrorMessage(), 
			 HttpStatus.BAD_REQUEST);
		}
		
		
		if(!isValidHttpUrl(request.getSuccessUrl())) {
			throw new StripeProviderException(
					ErrorCodeEnum.INVALID_SUCCESS_URL_FORMAT.getErrorCode(),
					 ErrorCodeEnum.INVALID_SUCCESS_URL_FORMAT.getErrorMessage() + " - Invalid URL format",
					HttpStatus.BAD_REQUEST);
		}
		
		if(!isValidHttpUrl(request.getCancelUrl())) {
			throw new StripeProviderException(
					ErrorCodeEnum.INVALID_CANCEL_URL_FORMAT.getErrorCode(),
					 ErrorCodeEnum.INVALID_CANCEL_URL_FORMAT.getErrorMessage() + " - Invalid URL format",
					HttpStatus.BAD_REQUEST);
		}
		
		
		 List<LineItem> items = request.getLineItems();
		 if (items == null || items.isEmpty()) {
			 
			 throw new StripeProviderException(
					 ErrorCodeEnum.LINE_ITEMS_MISSING.getErrorCode(),
					 ErrorCodeEnum.LINE_ITEMS_MISSING.getErrorMessage(), 
					 HttpStatus.BAD_REQUEST);
		 }
		 int index = 0;
		 for (LineItem item : items) {
			 index++;
			 if (item == null) {
	                throw new StripeProviderException(
	                        ErrorCodeEnum.LINE_ITEMS_MISSING.getErrorCode(),
	                        ErrorCodeEnum.LINE_ITEMS_MISSING.getErrorMessage() + " at index " + (index - 1),
	                        HttpStatus.BAD_REQUEST);
	         }
			 
			 if (item.getQuantity() <= 0) {
				 throw new StripeProviderException(
						 ErrorCodeEnum.QUANTITY_INVALID.getErrorCode(),
						 ErrorCodeEnum.QUANTITY_INVALID.getErrorMessage()+ " at index " + (index - 1), 
						 HttpStatus.BAD_REQUEST);
			 }
			 
			 if (item.getUnitAmount() <= 0) {
				 throw new StripeProviderException(
						 ErrorCodeEnum.UNIT_AMOUNT_INVALID.getErrorCode(),
						 ErrorCodeEnum.UNIT_AMOUNT_INVALID.getErrorMessage()+ " at index " + (index - 1), 
						 HttpStatus.BAD_REQUEST);
			 }
			 
			 if (item.getCurrency() == null || item.getCurrency().isEmpty()) {
				 throw new StripeProviderException(
						 ErrorCodeEnum.CURRENCY_MISSING.getErrorCode(),
						 ErrorCodeEnum.CURRENCY_MISSING.getErrorMessage()+ " at index " + (index - 1), 
						 HttpStatus.BAD_REQUEST);
			 }
			 if (item.getProductName() == null || item.getProductName().isEmpty()) {
				 throw new StripeProviderException(
						 ErrorCodeEnum.PRODUCT_NAME_MISSING.getErrorCode(),
						 ErrorCodeEnum.PRODUCT_NAME_MISSING.getErrorMessage()+ " at index " + (index - 1), 
						 HttpStatus.BAD_REQUEST);
			 }
		 }
		 
		 
		 log.info("ValidationService.isValid...validation successful for CreatePaymentRequest: {} ",request);
	}
	
	// Helper: ensure URL is a valid http or https URL with a host
    private boolean isValidHttpUrl(String url) {
        if (url == null) return false;
        try {
            URI uri = new URI(url.trim());
            String scheme = uri.getScheme();
            String host = uri.getHost();
            return scheme != null && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    && host != null && !host.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

}
