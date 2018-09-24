var app = angular.module("MainApp", ["ngRoute", 'ngCookies', 'ngSanitize']);
app.config(function($routeProvider){
	$routeProvider.when("/login", {
		templateUrl:"html/login.html",
		controller:"loginHandler"
	}).when("/employee", {
		templateUrl:"html/employeeDashBoard.html",
		controller:"employeeHandler"
	}).when("/manager", {
		templateUrl:"html/managerDashBoard.html",
		controller:"managerHandler"
	}).when("/requests", {
		templateUrl:"html/request.html",
		controller:"requestHandler"
	}).when("/submitRequest", {
		templateUrl:"html/subRequest.html",
		controller:"submitRequest"
	}).when("/manage", {
		templateUrl:"html/manage.html",
		controller:"manageRequests"
	}).when("/account", {
		templateUrl:"html/account.html"
	})
});

app.controller("MainCtrl", function($rootScope, $scope, $http, $location, $cookies, $sanitize) {

	$rootScope.type = $cookies.get("type");
	$scope.logout = function() {
		$http.get("MainServlet/logout");
		$location.path("/login");
	}
	$rootScope.navItems = ["", "", "", ""];
	$rootScope.toggleNav = function(index) {
		for(let i = 0; i < $rootScope.navItems.length; i++) {
			if (i == index) {
				$rootScope.navItems[i] = "active";
			} else {
				$rootScope.navItems[i] = "";
			}
		}
	}
	$rootScope.displayError = function(data) {
		$rootScope.error = "Server expection thrown";
		$rootScope.error_message = $sanitize(data);
	}

	$http.get("MainServlet/isloggedin").then(function success(response){
		if(response.data == "true") {
			$location.path("/"+$rootScope.type);
		}
		else {
			$cookies.remove("type");
			$rootScope.type = "login";
			$location.path("/login");
		}
		console.log($location.absUrl());
	}, function error(response) {
		console.log("error " + response.status);
	});
});
app.controller("loginHandler", function($rootScope, $cookies, $scope, $http, $location) {
	$http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
	$rootScope.type = "login"
    $scope.validate = function() {
    	console.log("sending " + $scope.username + " " + $scope.password);
    	var data1 = {username: $scope.username, password: $scope.password}
        $http.post('MainServlet/login', data1).then(function success(response) {
			if(response.data == "success") {
				$rootScope.type = $cookies.get("type");
				console.log("cookie type=" + $rootScope.type);
				console.log(document.cookie);
				$location.path("/"+$rootScope.type);
			} else {
				$scope.login_message = "login unsuccessful";
			}
		}, function failure(response) {
        	if (response.status == 500) {
        		$rootScope.displayError(response.data);
        	}
        })
    }
})
app.controller("employeeHandler", function($rootScope, $scope, $cookies) {
	$rootScope.toggleNav(0);
	
});

app.controller("managerHandler", function($rootScope, $scope, $cookies) {
	$rootScope.toggleNav(0);
	
});

app.controller("requestHandler", function($rootScope, $scope, $http){
	$rootScope.toggleNav(1);
	$http.get("MainServlet/getRequests").then(function success(response) {
		$scope.Requests = response.data;
	})
})

app.controller("submitRequest", function($rootScope, $scope, $location, $http){
		$scope.submitRequest = function() {
		$http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
		let data = {amount:$scope.amount, reason:$scope.reason};
		$http.post("MainServlet/submitRequest", data).then(function success(response){
			console.log("Submit Request Success");
			$location.path("/requests");
		}, function failure(response){
        	if (response.status == 500) {
        		$rootScope.displayError(response.data);
        	}
		});
	}
})

app.controller("manageRequests", function($rootScope, $scope, $http) {
	$rootScope.toggleNav(2);
	$scope.refresh = function() {
		$http.get("MainServlet/getAllRequests?field=status&value=PENDING").then(function success(response) {
			$scope.pRequests = response.data;
			console.log(response.data);
		}, function failure(response){
	    	if (response.status == 500) {
	    		$rootScope.displayError(response.data);
	    	}
	    	if (response.status == 403) {
	    		$rootScope.displayError("You do not have the permission to do that");
	    	}
		})
		$http.get("MainServlet/getAllRequests?field=status&value=APPROVED").then(function success(response) {
			$scope.aRequests = response.data;
			console.log(response.data);
		}, function failure(response){
	    	if (response.status == 500) {
	    		$rootScope.displayError(response.data);
	    	}
		})
		$http.get("MainServlet/getAllRequests?field=status&value=REJECTED").then(function success(response) {
			$scope.rRequests = response.data;
			console.log(response.data);
		}, function failure(response){
	    	if (response.status == 500) {
	    		$rootScope.displayError(response.data);
	    	}
		})
	}
	$scope.refresh();
	$scope.approve = function(id) {
		if(confirm("Do you wish to approve this request?")) {
		$http.get("MainServlet/approveRequest?id=" + id).then(function success(response){
			console.log("Approve Request Successful!");
			$scope.refresh();
		}, function failure(response){
        	if (response.status == 500) {
        		$rootScope.displayError(response.data);
        	}
		})
		}
	}
	
	$scope.deny = function(id) {
		let reason = prompt("Enter a reason if there is any.");
		$http.get("MainServlet/denyRequest?id=" + id + "&reason="+reason).then(function success(response){
			console.log("Deny Request Successful!");
			$scope.refresh();
		}, function failure(response){
        	if (response.status == 500) {
        		$rootScope.displayError(response.data);
        	}
		})
	}
})