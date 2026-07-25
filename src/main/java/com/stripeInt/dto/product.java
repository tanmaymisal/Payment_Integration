package com.stripeInt.dto;


import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class product {
  private String name;
  
  private long price;
  // i want the quantity cant be negative or zero so i will set the default value to 1	so i am using the validation annotation @min(1) 
  // to ensure that the quantity is always at least 1.
  @Min(1)
  private Long quntity;
  
  /**
   * Converts INR input to Euro Cents
   * Formula: (INR / 0.92) * 100
   */
  public void setPrice(double inputInr) {
      if (inputInr < 0) {
          throw new IllegalArgumentException("Price cannot be negative.");
      }

      // Use BigDecimal for the entire calculation to maintain precision
      BigDecimal inr = BigDecimal.valueOf(inputInr);
      BigDecimal exchangeRate = new BigDecimal("1");
      BigDecimal multiplier = new BigDecimal("100");

      // (inr / 0.92) * 100 // 499.99/1 * 100 = 
      BigDecimal result = inr
          .divide(exchangeRate, 10, RoundingMode.HALF_UP)
          .multiply(multiplier)
          .setScale(0, RoundingMode.HALF_UP); // Stripe needs whole numbers

      this.price = result.longValue();
  }
  
  
	
}

