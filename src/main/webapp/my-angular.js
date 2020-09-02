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
		templateUrl:"html/account.html",
		controller:"accountHandler"
	});
});

app.controller("MainCtrl", function($rootScope, $scope, $http, $location, $cookies, $sanitize) {
	if(!$rootScope.userInfo)
		$rootScope.userInfo = JSON.parse(localStorage.getItem("userInfo"));
	$scope.logout = function() {
		localStorage.clear();
		$http.get("MainServlet/logout");
		$location.path("/login");
	}
	$rootScope.navItems = ["", "", "", ""];
	$rootScope.toggleNav = function(index) {
		for(let i = 0; i < $rootScope.navItems.length; i++) {
			if (i == index) 
				$rootScope.navItems[i] = "active";
			 else 
				$rootScope.navItems[i] = "";
		}
	}
	
	$rootScope.handleFailure = function(response) {
		if(response == null) {
			console.log("Null Failure Response");
			return;
		}
		if (response.status == 500) {
        	$rootScope.displayError("Server exception thrown",response.data);
        }
		else if (response.status == 403) {
	    	$rootScope.displayError("403 Forbidden", response.data);
	    }
		else if (response.status == 401) {
			console.log("you are not signed in");
			$location.path("/login");
		}
	}
	
	$rootScope.displayError = function(error_type, error_message) {
		$rootScope.error = error_type;
		$rootScope.error_message = $sanitize(error_message);
		$(".alert-danger").show();
	}

	$http.get("MainServlet/isloggedin").then(function success(response){
		if(response.data == "true") {
			console.log($rootScope.userInfo)
			if($rootScope.userInfo)
			$location.path("/"+$rootScope.userInfo.type);
			else
				$scope.logout();
		}
		else {
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
    $scope.validate = function() {
    	var data1 = {username: $scope.username, password: $scope.password}
        $http.post('MainServlet/login', data1).then(function success(response) {
			if(response.data != "unsuccessful") {
				$rootScope.userInfo = response.data;
				localStorage.setItem("userInfo", JSON.stringify(response.data));
				$location.path("/"+$rootScope.userInfo.type);
			} else {
				$scope.login_message = "login unsuccessful";
			}
		}, function failure(response) {
        	$rootScope.handleFailure(response);
        })
    }
});
app.controller("employeeHandler", function($rootScope, $scope, $cookies) {
	$rootScope.toggleNav(0);
});

app.controller("managerHandler", function($rootScope, $scope, $http) {
	$rootScope.toggleNav(0);
	$scope.refresh = function() {
		$http.get("MainServlet/getAllRequests").then(function success(response) {
			$scope.requests = response.data;
			var total = response.data.length;
			var approved = response.data.filter(r_req => r_req.status == "APPROVED").length;
			var rejected = response.data.filter(r_req => r_req.status == "REJECTED").length;
			var pending = response.data.filter(r_req => r_req.status == "PENDING").length;
			if(pending > 0)
				$('.alert').alert()
			var percentages = {"Approved": approved / total, "Rejected":rejected/total,"Pending": pending/total};
			$scope.piechartData = {};
			var cummulativePercentage = 0;
			for(var r_type in percentages){
			var currentLocation = Math.cos(2*Math.PI*cummulativePercentage) + " "
				+   Math.sin(2*Math.PI*cummulativePercentage);
				cummulativePercentage += percentages[r_type];
				$scope.piechartData[r_type] = "M 0 0" + // Move
    			"L "+ currentLocation+ // Line
    			"A 1 1 0 "+(percentages[r_type] > 0.5?1:0)+" 1 "+
				 Math.cos(2*Math.PI*cummulativePercentage) + " "
				+   Math.sin(2*Math.PI*cummulativePercentage);// Arc
				$scope.piechartData[r_type]["end"] = currentLocation;
				
			}
			$scope.approvedPathData = "M200 200 L 400 200 A 200 200 0 0 0 "
				 +( 200+ (200 * Math.cos(2*Math.PI*approved/total))) + " "
				+ ( 200+ (200 * Math.sin(2*Math.PI*approved/total)));
		}, function failure(response){
	    	$rootScope.handleFailure(response);
		})
	}
	$scope.refresh();
});

app.controller("requestHandler", function($rootScope, $scope, $http){
	$rootScope.toggleNav(1);
	$http.get("MainServlet/getRequests").then(function success(response) {
		$scope.Requests = response.data;
	}, function failure(response){
		$rootScope.handleFailure(response);
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
        	$rootScope.handleFailure(response);
		});
	}
});

app.controller("manageRequests", function($rootScope, $scope, $http) {
	$rootScope.toggleNav(2);
	$scope.refresh = function() {
		$http.get("MainServlet/getAllRequests").then(function success(response) {
			$scope.requests = response.data;
			console.log(response.data);
		}, function failure(response){
	    	$rootScope.handleFailure(response);
		})
	}
	$scope.refresh();
	$scope.approve = function(id) {
		if(confirm("Do you wish to approve this request?")) {
		$http.get("MainServlet/approveRequest?id=" + id).then(function success(response){
			console.log("Approve Request Successful!");
			$scope.refresh();
		}, function failure(response){
        	$rootScope.handleFailure(response);
		})
		}
	}
	
	$scope.deny = function(id) {
		let reason = prompt("Enter a reason if there is any.");
		$http.get("MainServlet/denyRequest?id=" + id + "&reason="+reason).then(function success(response){
			console.log("Deny Request Successful!");
			$scope.refresh();
		}, function failure(response){
        	$rootScope.handleFailure(response);
		})
	}
});
app.controller("accountHandler", function($rootScope, $scope, $http) {
	$scope.password1 = "";
	$scope.password2 = "";
	$rootScope.toggleNav(3);
	$scope.refresh = function() {
		$http.get("MainServlet/getMyAccount").then(function success(response) {
			$scope.user = response.data;
		}, function failure(response) {
			$rootScope.handleFailure(response);
		})
	}
	$scope.updateUser = function() {
		
		if($scope.password1 != "" || $scope.password2 != "") {
			if($scope.password1 == $scope.password2) {
				$scope.user.password = $scope.password1;
				console.log($scope.password1 + " = " + $scope.password2);
			} else {
				console.log($scope.password1 + " != " + $scope.password2);
				$rootScope.displayError("Invalid Password", "The passwords must match");
				return;
			}
		} else {
			console.log($scope.password1 + " both empty " + $scope.password2);
			$scope.user.password = null;
		}
		$http.post("MainServlet/updateUser", $scope.user).then(function success(response) {
			console.log("success");
		}, function failure(response) {
			$rootScope.handleFailure(response);
		})
	}
	$scope.refresh();
});