package com.stripeInt.entity;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchased_products")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class productDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "payment_id",nullable = false)
	private paymentDetails payment;
	
	@Column(nullable = false)
	private String product_name;
	
	@Column(nullable = false)
	private Long product_price;
	@Column(nullable = false)
	private Long quntity;
}
