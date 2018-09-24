window.onload = function() {
	getHome();
}
function getHome() {
	getPage("GET", "/home", null);
}
function getLogin() {
	getPage("GET", "/login", null);
}
var login = function() {
	let username = document.getElementById('username').value;
	let password = document.getElementById("password").value;
	let body = 'name=' + username + '&password=' + password;
	getPage('POST', '/login', body);
}
function logout() {
	getPage("GET", "/logout", null);
}
function getPage(method, uri, body) {
	let xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		console.log(this.status + " " + this.readyState);
		if(this.readyState == 4 && this.status == 200) {
			let page = JSON.parse(this.responseText).body;
			let nav = JSON.parse(this.responseText).nav;
			document.getElementById("main-container").innerHTML = page;
			document.getElementById("navbar").innerHTML = nav;
		}
	}
	xhttp.open(method, "/Project1/MainServlet" + uri);
	if(body == null) {
		xhttp.send();
	} else {
		xhttp.send(body);
	}
}