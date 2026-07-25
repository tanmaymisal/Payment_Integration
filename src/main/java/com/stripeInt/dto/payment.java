package com.stripeInt.dto;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class payment {

	@NotNull
	private String sessionId;
	
	private String paymentIntentId;
	@NotNull
	private String customerEmail;
	@NotNull
	private Long totalAmount;
	@NotNull
	private String currency;
/* here for creating payment we are setting default
 * status as pending and after payment is successful
 * we will update the status to success and 
 * if payment is failed then we will update the status to failed
 * */
	private payStatus status = payStatus.PENDING;
}
