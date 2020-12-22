var orderData = {
  email: "cantinflas@gmail.com", // id usuario hay que recuperarlo desde front
  precio: 34,
  minutos: 60 // minutos que haya estado el alumno conectado.
};

fetch("/capture-payment-intent", {
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
	console.log(data);
});