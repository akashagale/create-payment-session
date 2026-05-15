package com.htech.payments.pojo;

import lombok.Data;

@Data
public class LineItem {

	private String currency;
	private String productName;
	private int unitAmount;
	private int quantity;
}