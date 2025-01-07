//angular.module("myApp",[])
//    .controller("myController", function($scope) {
//    $scope.profile=function() {
//        window.location.href = "http://localhost:40111/profile.html";
//    };
//    $scope.bookclub=function() {
//        window.location.href = "http://localhost:40111/bookclub.html";
//    };
//    $scope.editprofile=function() {
//        window.location.href = "http://localhost:40111/editprofile.html";
//    };
//});

angular.module("myApp", [])
    .controller("ChatroomController", function($scope, $http) {
        $scope.chatrooms = [];

        // Retrieve the logged-in user's email from sessionStorage
        const userEmail = sessionStorage.getItem("EMAIL");
        if (!userEmail) {
            alert("You are not logged in. Redirecting to login page.");
            window.location.href = "login.html";
            return;
        }

        // Define data to send to the server
        const dataToServer = {
            infoClass1: userEmail,
            action: "getChatrooms"
        };

        // Fetch chatrooms for the user
        $http.post("http://localhost:40111/topichive", dataToServer)
            .then(function(response) {
                $scope.chatrooms = response.data; // Expecting a list of chatrooms
            })
            .catch(function(error) {
                console.error("Error fetching chatrooms:", error);
            });

        $scope.openChatroom = function(chatroomId) {
            window.location.href = `/chatroom.html?id=${chatroomId}`;
        };
    });


