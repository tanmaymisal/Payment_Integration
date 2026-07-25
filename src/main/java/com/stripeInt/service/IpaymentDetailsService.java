package com.stripeInt.service;

public interface IpaymentDetailsService {

	
	public void updatePaymentStatus(String sessionId,String paymentIntentId, String mail,String status);
}
