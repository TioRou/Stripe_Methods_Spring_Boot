var orderData = {
  paymentMethod: "pm_1Hw1A2KCq6yWzLOg1NSsX4iE"
};

fetch("/delete-payment-method", {
	  method: "POST",
	  headers: {
	    "Content-Type": "application/json"
	  },
	  body: JSON.stringify(orderData)
	});