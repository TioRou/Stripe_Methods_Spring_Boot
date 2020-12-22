// A reference to Stripe.js
var stripe;

var orderData = {
  email: "cantinflas@gmail.com",
  precio: 34, // precio del profesor pasado a minutos
  currency: "eur"
};

fetch("/save-hold-payment-intent", {
  method: "POST",
  headers: {
    "Content-Type": "application/json"
  },
  body: JSON.stringify(orderData)
})
  .then(function(result) {
    return result.json();
  })
  .then(function(data) {
    return setupElements(data);
  })
  .then(function({ stripe, card, clientSecret }) {
    document.querySelector("#submit").addEventListener("click", function(evt) {
	  evt.preventDefault();
	  // Initiate payment
	      pay(stripe, card, clientSecret);
	    });   
  });

// Set up Stripe.js and Elements to use in checkout form
var setupElements = function(data) {
  stripe = Stripe(data.publicKey);
  var elements = stripe.elements();
  var style = {
    base: {
      color: "#32325d",
      fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
      fontSmoothing: "antialiased",
      fontSize: "16px",
      "::placeholder": {
        color: "#aab7c4"
      }
    },
    invalid: {
      color: "#fa755a",
      iconColor: "#fa755a"
    }
  };

  var card = elements.create("card", { style: style, hidePostalCode: true });
  card.mount("#card-element");

  return {
    stripe,
    card,
    clientSecret: data.clientSecret
  };
};

/*
 * Calls stripe.confirmCardPayment which creates a pop-up modal to
 * prompt the user to enter  extra authentication details without leaving your page
 */
var pay = function(stripe, card, clientSecret) {
  // Initiate the payment.
  // If authentication is required, confirmCardPayment will display a modal
  var isSavingCard = document.querySelector("#save-card").checked;
  
  var data = {
    card: card,
    billing_details: {}
  };

  // Initiate the payment.
  // If authentication is required, confirmCardPayment will display a modal
  stripe
    .confirmCardPayment(clientSecret, { 
    	payment_method: data,
    	setup_future_usage: isSavingCard ? "off_session" : ""
    })
    .then(function(result) {
      if (result.error) {
        var errorMsg = document.querySelector(".sr-field-error");
        errorMsg.textContent = result.error.message;
        setTimeout(function() {
          errorMsg.textContent = "";
        }, 4000);
      } else {      
    	console.log(result);
        
      }
    });
};