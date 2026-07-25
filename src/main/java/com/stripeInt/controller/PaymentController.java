package com.stripeInt.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripeInt.dto.payment;
import com.stripeInt.dto.product;
import com.stripeInt.entity.paymentDetails;
import com.stripeInt.entity.productDetails;
import com.stripeInt.repository.IpaymentDetails;
import com.stripeInt.repository.IproductDetails;
import com.stripeInt.service.IpaymentDetailsService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class PaymentController {

	private final IpaymentDetails paymentRepo;
	private final IproductDetails productRepo;
	private final IpaymentDetailsService paymentService;

	@Autowired
	public PaymentController(IpaymentDetails paymentDetailsRepo, IproductDetails productDetailsRepo, IpaymentDetailsService paymentService) {
		this.paymentRepo = paymentDetailsRepo;
		this.productRepo = productDetailsRepo;
		this.paymentService = paymentService;
	}
	
@Value("${stripe.webhook.secret}")
	private String stripeWebhookScret;

	@Value("${stripe.api.key}") // reads stripe.api.key property or STRIPE_API_KEY env var if property missing
	private String stripeApiKey;
	@PostMapping("/create-checkout-session")
	public ResponseEntity<Map<String,Object>> createSession
	(@RequestBody Map<String, List<product>> payload)
	{
		List<product> prod = payload.get("products"); 
		if(prod == null || prod.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("error", "No products provided"));
		}
		List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
		for(product p : prod) 
		{
			SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
					.setPriceData(SessionCreateParams.LineItem.PriceData.builder()
							.setCurrency("INR")
							.setUnitAmount(p.getPrice()) // Stripe expects amount in cents, so we already converted it in the setPrice method of the product class where we use the formula (INR / 0.92) * 100 to convert the price to Euro cents.
							.setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
									.setName(p.getName())
									.build())
							.build())
					.setQuantity(p.getQuntity())
					.build();
			lineItems.add(lineItem);
		}
		
		SessionCreateParams params = SessionCreateParams.builder()
	            .setMode(SessionCreateParams.Mode.PAYMENT)
	            .setSuccessUrl("https://localhost:8080/success?session_id={CHECKOUT_SESSION_ID}")
	            .setCancelUrl("https://localhost:8080/cancel")
	            .addAllLineItem(lineItems)
	            .build();

		
		
		try {
			// Ensure an API key is configured before calling Stripe
			if (stripeApiKey == null || stripeApiKey.isBlank()) {
				return ResponseEntity.status(500).body(Collections.singletonMap("error",
					"No API key provided. Configure stripe.api.key in application.properties or set STRIPE_API_KEY environment variable."));
			}
			Stripe.apiKey = this.stripeApiKey;
            Session session = Session.create(params);
            Map<String, Object> response = new HashMap<>();
            response.put("url", session.getUrl());
            if(session != null && session.getUrl() != null) {
            	
               
            	payment p = new payment();
            	p.setSessionId(session.getId());
            	p.setTotalAmount(session.getAmountTotal());
            	System.out.println("Total Amount: " + session.getAmountTotal());
            	p.setCurrency(session.getCurrency());
            	
            	//p.setCustomerEmail(session.getCustomerDetails().getEmail());
            	paymentDetails paydt = new paymentDetails();
            	paydt.setSessionId(p.getSessionId());
            	paydt.setTotalAmount(p.getTotalAmount());
            	paydt.setCurrency(p.getCurrency());
            	paydt.setStatus(p.getStatus());    
            	paymentRepo.save(paydt);
            	
            	 for(product pr : prod) {
                 	productDetails prdt = new productDetails();
 					prdt.setProduct_name(pr.getName());
 					prdt.setProduct_price(pr.getPrice());
 					prdt.setQuntity(pr.getQuntity());
 					prdt.setPayment(paydt);
 					productRepo.save(prdt);
 				}
				response.put("message", "Checkout session created successfully");
			} else {
				response.put("message", "Failed to create checkout session");
			}
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
        }
	}
	@PostMapping("/webhooks")
	public ResponseEntity<String> handleStripeWebhook(HttpServletRequest  request,@RequestBody String payload)
	
	{
		String sigHeader = request.getHeader("Stripe-Signature");	
		Event event = null;
		try 
		{
			event = Webhook.constructEvent(payload,sigHeader,stripeWebhookScret);
			
		}
		catch(Exception e) 
		{
			return ResponseEntity.status(400).body("Webhook error: " + e.getMessage());
		}
		//handle the event
		if("checkout.session.completed".equals(event.getType())) 
		{
			Session session = (Session) event.getDataObjectDeserializer().getObject().get();
			// Update payment status in the database
			paymentService.updatePaymentStatus(session.getId(), session.getPaymentIntent(), session.getCustomerDetails().getEmail(), "success");
			System.out.println("Payment successful for session: " + session.getId());
		}
		return ResponseEntity.ok("Webhook received");
	}
	
	
}