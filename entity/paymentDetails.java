package com.stripeInt.entity;


import java.time.Instant;
import java.util.List;

import com.stripeInt.dto.payStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "payment_details")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class paymentDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NonNull
	@Column(name = "session_id", unique = true, nullable = false)
	private String sessionId;
	@Column(unique = true, nullable = true)
	private String paymentIntentId;
	@Column(name = "customer_email", nullable = true)
	private String customerEmail;
	@NonNull
	@Column(name = "total_amount", nullable = false)
	private Long totalAmount;
	@NonNull
	@Column(nullable = false)
	private String currency;
	
	@Enumerated(EnumType.STRING)
	private payStatus status;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
	@OneToMany(mappedBy = "payment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	  private List<productDetails> items;
	@PrePersist
	    protected void onCreate() {
	        Instant now = Instant.now();
	        this.createdAt = now;
	        this.updatedAt = now;
	    }

	@PreUpdate
	    protected void onUpdate() {
	        this.updatedAt = Instant.now();
	    }

	  
}