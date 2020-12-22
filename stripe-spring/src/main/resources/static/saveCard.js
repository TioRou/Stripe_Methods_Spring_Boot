// A reference to Stripe.js
var stripe;

// EndPoint params to save a card linked to an email customer
// Email is passed from the front-end. In our case, we're using a fictitious email 
var orderData = {
  email: "fictitious@gmail.com"
};

fetch("/save-card", {
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
      saveCard(stripe, card, clientSecret);
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

// Here we save a Card into Stripe System using "confirmCardSetup" method
var saveCard = function(stripe, card, clientSecret) {
  var data = {
    card: card,
    billing_details: {}
  };

  stripe
    .confirmCardSetup(clientSecret, { 
    	payment_method: data
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
    	alert("Card saved!!!");
        
      }
    });
};