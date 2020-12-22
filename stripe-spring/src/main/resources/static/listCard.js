var orderData = {
  email: "fictitious@gmail.com"
};

fetch("/list-all-cards", {
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
		setupElements(data);  
	  });

var setupElements = function(data) {
	// For each payment method (card) retrieved, we get some info as last 4 numbers or payment method id
	if(data.paymentMethods != null){
		let list = document.getElementById("cardList");
		let li = document.createElement("li");
		 
		data.paymentMethods.data.forEach(function(item){
			li.appendChild(document.createTextNode(item.id + ", " + item.card.last4));
			list.appendChild(li);
		})
	
	}else{
		console.log("No tiene tarjetas asignadas");
	}
}
