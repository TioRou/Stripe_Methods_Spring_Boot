// A reference to Stripe.js
var stripe;

var orderData = {
  email: "canti@gmail.com",
  precio: 34, // precio del profesor pasado a minutos
  currency: "eur"
};

fetch("/hold-payment-intent", {
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
  .then(function({ stripe, paymentMethod, clientSecret }) {
      pay(stripe, paymentMethod, clientSecret);    
  });

// Set up Stripe.js and Elements to use in checkout form
var setupElements = function(data) {
  stripe = Stripe(data.publicKey);

  var paymentMethod = "pm_1HxUIrKCq6yWzLOgnjozd7GG";

  return {
    stripe,
    paymentMethod,
    clientSecret: data.clientSecret
  };
};

/*
 * Calls stripe.confirmCardPayment which creates a pop-up modal to
 * prompt the user to enter  extra authentication details without leaving your page
 */
var pay = function(stripe, paymentMethod, clientSecret) {
  // Initiate the payment.
  // If authentication is required, confirmCardPayment will display a modal
  stripe
    .confirmCardPayment(clientSecret, { 
    	payment_method: paymentMethod
    })
    .then(function(result) {
      if (result.error) {
        //changeLoadingState(false);
        var errorMsg = document.querySelector(".sr-field-error");
        errorMsg.textContent = result.error.message;
        setTimeout(function() {
          errorMsg.textContent = "";
        }, 4000);
      } else {      
    	console.log("Hold realizado.");
        
      }
    });
};