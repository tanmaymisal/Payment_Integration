package com.stripeInt.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.stripeInt.entity.paymentDetails;

public interface IpaymentDetails extends JpaRepository<paymentDetails,Long> 
{

	public paymentDetails findBySessionId(String sessionId);         
	    
          
}