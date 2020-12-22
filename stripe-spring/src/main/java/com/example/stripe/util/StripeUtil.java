package com.example.stripe.util;

import com.google.gson.annotations.SerializedName;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethodCollection;

public class StripeUtil {
	
    public static class SaveCardBody {
    	@SerializedName("email")
        String email;
                
        public String getEmail() {
            return email;
        }
        
    }    
    
    public static class SaveCardResponse {
        private String publicKey;
        private String clientSecret;

        public SaveCardResponse(String publicKey, String clientSecret) {
            this.publicKey = publicKey;
            this.clientSecret = clientSecret;
        }
        
    }

    public static class ListCardBody {
    	@SerializedName("email")
        String email;
                
        public String getEmail() {
            return email;
        }
    }    
    
    public static class ListCardResponse {
        private PaymentMethodCollection paymentMethods;

        public ListCardResponse(PaymentMethodCollection pmc) {
            this.paymentMethods = pmc;
        }
        
    }    

    public static class DeletePaymentMethodBody {
        @SerializedName("paymentMethod")
        String paymentMethod;
        
        public String getPaymentMethod() {
            return paymentMethod;
        }
        
    }
    
    public static class HoldCardBody {
    	@SerializedName("email")
        String email;

       	@SerializedName("precio")
        Integer precio;
       	
       	@SerializedName("currency")
        String currency;
       	
        public String getEmail() {
            return email;
        }

        public Integer getPrecio() {
            return precio;
        }
        
        public String getCurrency() {
            return currency;
        }
    }    
    
    public static class HoldCardResponse {
        private String publicKey;
        private String clientSecret;
        private String id;

        public HoldCardResponse(String publicKey, String clientSecret, String id) {
            this.publicKey = publicKey;
            this.clientSecret = clientSecret;
            this.id = id;
        }
        
    }

    public static class CapturePayBody {
    	@SerializedName("email")
        String email;

       	@SerializedName("precio")
        Integer precio;
       	
       	@SerializedName("minutos")
        Integer minutos;
       	
        public String getEmail() {
            return email;
        }

        public Integer getPrecio() {
            return precio;
        }
        
        public Integer getMinutos() {
            return minutos;
        }
    }    
    
    public static class CapturePayResponse {
        private PaymentIntent paymentIntent;

        public CapturePayResponse(PaymentIntent paymentIntent) {
            this.paymentIntent = paymentIntent;
        }
        
    }

    public static class SaveHoldCardBody {
    	@SerializedName("email")
        String email;

       	@SerializedName("precio")
        Integer precio;
       	
       	@SerializedName("currency")
        String currency;
       	
        public String getEmail() {
            return email;
        }

        public Integer getPrecio() {
            return precio;
        }
        
        public String getCurrency() {
            return currency;
        }
    }    
    
    public static class SaveHoldCardResponse {
        private String publicKey;
        private String clientSecret;
        private String id;

        public SaveHoldCardResponse(String publicKey, String clientSecret, String id) {
            this.publicKey = publicKey;
            this.clientSecret = clientSecret;
            this.id = id;
        }
        
    }
    
}
