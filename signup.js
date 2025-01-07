let url = "http://localhost:40111/topichive";

angular.module("signupApp", [])
    .controller("SignupController", function($scope) {
        $scope.user = {};

        $scope.register = function() {
            if (validateInput($scope.user)) {
                let dataToServer = {
                    infoClass1: JSON.stringify($scope.user),
                    action: "signup"
                };

                sendDataToServer(dataToServer);
            } else {
                println("Invalid input. Please fill out all fields correctly.");
            }
        };

        function validateInput(user) {
            return user.email && user.password && user.displayname && user.firstname && user.lastname;
        }

        function sendDataToServer(data) {
            let req = new XMLHttpRequest();
            req.addEventListener("load", requestListener);
            req.open("POST", url);
            req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            req.send(JSON.stringify(data));
            console.log("Sent to server: json=" + JSON.stringify(data));
        }

        function requestListener() {
            let response = JSON.parse(this.responseText);
            if (response.success) {
                println("Account created successfully! Redirecting to login...");
                setTimeout(() => window.location.href = "login.html", 2000);
            } else {
                println("Error creating account: " + response.message);
            }
        }

        function println(outputStr) {
            document.getElementById("output").innerHTML += outputStr + "<br>";
        }
    });
