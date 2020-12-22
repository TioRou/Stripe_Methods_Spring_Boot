package com.example.stripe.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.exception.*;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.model.SetupIntent;
import com.stripe.param.SetupIntentCreateParams;

import io.github.cdimascio.dotenv.Dotenv;

import com.example.stripe.util.StripeUtil.*;

import com.google.gson.Gson;

@Controller
public class StripeController {
	static Dotenv dotenv = Dotenv.load();
	
	static final String STRIPE_PUBLISHABLE_KEY=dotenv.get("STRIPE_PUBLISHABLE_KEY");
    static final String STRIPE_SECRET_KEY=dotenv.get("STRIPE_SECRET_KEY");
    static final String STRIPE_WEBHOOK_SECRET=dotenv.get("STRIPE_WEBHOOK_SECRET");
	
	static final Integer MINUTOS_HOLDED = 60;
	
    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/list")
    public String listView(){
        return "list";
    }

    @GetMapping("/delete")
    public String deleteView(){
        return "delete";
    }

    @GetMapping("/hold")
    public String holdView(){
        return "hold";
    }

    @GetMapping("/capture")
    public String captureView(){
        return "capture";
    }
    
    @GetMapping("/saveHold")
    public String saveHoldView(){
        return "saveHold";
    }
    
    @PostMapping(path = "/save-card", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String saveCard(@RequestBody SaveCardBody postBody) throws StripeException {
    	
    	//Stripe.apiKey = dotenv.get("STRIPE_SECRET_KEY");
    	Stripe.apiKey = STRIPE_SECRET_KEY;

    	Customer customer;
    	
    	Map<String, Object> options = new HashMap<>();
    	options.put("email", postBody.getEmail());
    	List<Customer> customers = Customer.list(options).getData();
        //Check if customer exists
    	if (customers.size() > 0) {
    	    customer = customers.get(0);
          		  
    	}else {
            CustomerCreateParams customerCreateParams = new CustomerCreateParams.Builder()
            .setEmail(postBody.getEmail())
            .build();
            
            customer = Customer.create(customerCreateParams);
    	}
        
    	//Create a setupIntent
        SetupIntentCreateParams params = new SetupIntentCreateParams.Builder()
                .setCustomer(customer.getId())
                .build();
        
        SetupIntent setupIntent = SetupIntent.create(params);
    	
    	Gson gson = new Gson();
    	
    	return gson.toJson(new SaveCardResponse(STRIPE_PUBLISHABLE_KEY, setupIntent.getClientSecret()));    	
    }
    
    @PostMapping(path = "/delete-payment-method", consumes = "application/json")
    @ResponseBody
    public void deletePaymentMethod(@RequestBody DeletePaymentMethodBody postBody) throws StripeException {
    	
    	Stripe.apiKey = STRIPE_SECRET_KEY;

    	PaymentMethod pm = PaymentMethod.retrieve(postBody.getPaymentMethod());
    	
    	pm.detach();
    }    
    
    @PostMapping(path = "/list-all-cards", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String listAllCards(@RequestBody ListCardBody postBody) throws StripeException {
    	
    	Stripe.apiKey = STRIPE_SECRET_KEY;
    	
    	Customer customer;
    	
    	PaymentMethodCollection paymentMethods = null;
    	
    	Map<String, Object> options = new HashMap<>();
    	options.put("email", postBody.getEmail());
    	List<Customer> customers = Customer.list(options).getData();
        
    	if (customers.size() > 0) {
    	    customer = customers.get(0);
    	    
            Map<String, Object> params = new HashMap<>();
            params.put("customer", customer.getId());
            params.put("type", "card");

            paymentMethods = PaymentMethod.list(params);
          		  
    	}
    	
    	Gson gson = new Gson();
    	
    	return gson.toJson(new ListCardResponse(paymentMethods));
    	
    }    
      
    @PostMapping(path = "/hold-payment-intent", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String HoldPaymentResponse(@RequestBody HoldCardBody postBody) throws StripeException {
    	
    	Stripe.apiKey = dotenv.get("STRIPE_SECRET_KEY");
        
    	Map<String, Object> options = new HashMap<>();
    	options.put("email", postBody.getEmail());
    	List<Customer> customers = Customer.list(options).getData();
        
    	Customer customer = customers.get(0);
        
    	PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
    	        .setCurrency(postBody.getCurrency())
    	        .setAmount(new Long(calculateOrderAmount(MINUTOS_HOLDED, postBody.getPrecio())))
    	        .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
    	        .setCustomer(customer.getId())
        .build(); 
    	
    	PaymentIntent intent = PaymentIntent.create(createParams);
    	
    	Gson gson = new Gson();
    	
    	return gson.toJson(new HoldCardResponse(dotenv.get("STRIPE_PUBLISHABLE_KEY"), intent.getClientSecret(),
                intent.getId()));
    	
    }

    @PostMapping(path = "/capture-payment-intent", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String CapturePaymentResponse(@RequestBody CapturePayBody postBody) throws StripeException {
    	
    	Stripe.apiKey = dotenv.get("STRIPE_SECRET_KEY");
        
    	Map<String, Object> options = new HashMap<>();
    	options.put("email", postBody.getEmail());
    	List<Customer> customers = Customer.list(options).getData();
        
    	Customer customer = customers.get(0);
    	
    	Map<String, Object> params = new HashMap<>();
    	params.put("customer", customer.getId());

    	PaymentIntentCollection paymentIntents =
    	  PaymentIntent.list(params);
    	
    	String paymentIntentId = null;
    	
    	for(PaymentIntent pi: paymentIntents.getData()) {    		
    		if(pi.getStatus().equals("requires_capture")) {
    			paymentIntentId = pi.getId();
    		}
    	}
    	
    	PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
    	
    	Map<String, Object> captureParams = new HashMap<>();
    	captureParams.put("amount", new Long(calculateOrderAmount(postBody.getMinutos(), postBody.getPrecio())));
    	
    	PaymentIntent capturedPaymentIntent =
    			  paymentIntent.capture(captureParams);
    	
    	Gson gson = new Gson();
    	
    	return gson.toJson(new CapturePayResponse(capturedPaymentIntent));
    	
    }
    
    @PostMapping(path = "/save-hold-payment-intent", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String saveHoldPaymentResponse(@RequestBody SaveHoldCardBody postBody) throws StripeException {
    	
    	Stripe.apiKey = dotenv.get("STRIPE_SECRET_KEY");
        
    	Customer customer;
    	
    	Map<String, Object> options = new HashMap<>();
    	options.put("email", postBody.getEmail());
    	List<Customer> customers = Customer.list(options).getData();
        
    	if (customers.size() > 0) {
    	    customer = customers.get(0);
          		  
    	}else {
            CustomerCreateParams customerCreateParams = new CustomerCreateParams.Builder()
            .setEmail(postBody.getEmail())
            .build();
            
            customer = Customer.create(customerCreateParams);
    	}
        
    	PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
    	        .setCurrency(postBody.getCurrency())
    	        .setAmount(new Long(calculateOrderAmount(MINUTOS_HOLDED, postBody.getPrecio())))
    	        .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
    	        .setCustomer(customer.getId())
        .build(); 
    	
    	PaymentIntent intent = PaymentIntent.create(createParams);
    	
    	Gson gson = new Gson();
    	
    	return gson.toJson(new SaveHoldCardResponse(dotenv.get("STRIPE_PUBLISHABLE_KEY"), intent.getClientSecret(),
                intent.getId()));
    	
    }
    
    static int calculateOrderAmount(Integer minutos, Integer precio) {
    	return minutos * precio;
    }    
    
}
