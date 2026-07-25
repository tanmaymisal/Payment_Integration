package com.stripeInt.repository;


import org.springframework.data.jpa.repository.JpaRepository;


import com.stripeInt.entity.productDetails;

public interface IproductDetails extends JpaRepository<productDetails,Long> {
	
	
}
