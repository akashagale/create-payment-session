package com.htech.payments.constant;

import org.springframework.http.HttpStatus;

public enum ErrorCodeEnum {

	ERROR_CONNECTING_TO_EXTERNAL_SERVICE("30001", "Error connecting to external service",HttpStatus.INTERNAL_SERVER_ERROR), 
	CREATE_PAYMENT_REQUEST_NULL("30002", "CreatePaymentRequest is null", HttpStatus.BAD_REQUEST), 
	SUCCESS_URL_MISSING("30003", "Success URL is missing", HttpStatus.BAD_REQUEST),
	CANCEL_URL_MISSING("30004", "Cancel URL is missing", HttpStatus.BAD_REQUEST),
	LINE_ITEMS_MISSING("30005", "Line items are missing", HttpStatus.BAD_REQUEST), 
	CURRENCY_MISSING("30006", "Currency is missing in line item", HttpStatus.BAD_REQUEST), 
	QUANTITY_INVALID("30007", "Quantity is invalid in line item", HttpStatus.BAD_REQUEST),
	UNIT_AMOUNT_INVALID("30008", "Unit amount is invalid in line item", HttpStatus.BAD_REQUEST), 
	PRODUCT_NAME_MISSING("30009", "Product name is missing in line item", HttpStatus.BAD_REQUEST), 
	INVALID_SUCCESS_URL_FORMAT("30010", "Invalid URL format", HttpStatus.BAD_REQUEST),
	INVALID_CANCEL_URL_FORMAT("30010", "Invalid URL format", HttpStatus.BAD_REQUEST);
	
	
	

	private final String errorCode;
	private final String errorMessage;
	private final HttpStatus httpStatus;

	ErrorCodeEnum(String errorCode, String errorMessage, HttpStatus httpStatus) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.httpStatus = httpStatus;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}