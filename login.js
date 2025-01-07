// URL to servlet:
let url = "http://localhost:40111/topichive";

// What we will send over.
let DataToServer = {
    infoClass1: null,
    infoClass2: null,
    action: null
};

let jsonDataFromServer = {
    email: null
}

angular.module("myApp",[])
    .controller("myController", function($scope) {
    $scope.submit=function () {
        if (checkInput($scope)) {
            DataToServer.infoClass1 = $scope.user.email;
            DataToServer.infoClass2 = $scope.user.password;
            DataToServer.action = "login";
            sendDataToServer();
        } else {
             println ("invalid input");
        }
    };
    $scope.signup=function () {
        window.location.href = "http://localhost:40111/signup.html";
    }
});

function checkInput ($scope) {
//TODONE: ensure that the the username and password are valid
    //inputs
    if ($scope.user.email == null || $scope.user.password == null) {
        return false;
    }
    if ($scope.user.email == "" || $scope.user.password == "") {
        return false;
    }
    if ($scope.user.email == " " || $scope.user.password == " ") {
        return false;
    }
    return true;
}
function sendDataToServer ($scope) {
    let req = new XMLHttpRequest();
    req.addEventListener("load", requestListener);
    req.open("POST", url);
    req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    req.send(JSON.stringify(DataToServer));
    console.log("Sent to server: json=" + JSON.stringify(DataToServer));
}

function requestListener () {
    let jsonObject = JSON.parse (this.responseText);
    jsonDataFromServer.email = jsonObject.email;
    if (jsonDataFromServer.email == null) {
        println ("Email does not exist or combination of email/password is incorrect.");
    } else if (jsonDataFromServer.email == "blocked") {
        println ("You have been blocked from TopicHive! To know why, please contact the admin at admin1@topichive.com");
    } else {
        console.log(jsonDataFromServer.email)
        setEmail(jsonDataFromServer.email);
        window.location.href = "http://localhost:40111/home.html";
    }
    console.log (jsonObject);
}

function println (outputStr) {
    document.getElementById("output").innerHTML += outputStr + "<br>";
}

function setEmail (email) {
    email = email.toString();
    sessionStorage.setItem("EMAIL", email);
}