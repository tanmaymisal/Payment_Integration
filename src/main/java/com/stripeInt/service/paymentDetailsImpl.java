package com.stripeInt.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stripeInt.dto.payStatus;
import com.stripeInt.entity.paymentDetails;
import com.stripeInt.repository.IpaymentDetails;

import jakarta.transaction.Transactional;
@Service
public class paymentDetailsImpl implements IpaymentDetailsService {

	
	private IpaymentDetails paymentRepo;
	public paymentDetailsImpl(IpaymentDetails paymentRepo) {
		this.paymentRepo = paymentRepo;
	}
	@Transactional
	@Override
	public void updatePaymentStatus(String sessionId, String paymentIntentId, String mail, String status) {
		// TODO Auto-generated method stub
		
		paymentDetails payment = paymentRepo.findBySessionId(sessionId);
	   		if(payment != null) {
			payment.setPaymentIntentId(paymentIntentId);
			payment.setCustomerEmail(mail);
			payment.setStatus(status.equalsIgnoreCase("success") ? payStatus.SUCCESS : payStatus.FAILED);
			paymentRepo.save(payment);
		}
		
	}
}


