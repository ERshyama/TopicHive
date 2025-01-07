angular.module("browseApp", [])
    .controller("BrowseController", function($scope, $http) {
        $scope.chatrooms = [];
        const userEmail = sessionStorage.getItem("EMAIL");

        if (!userEmail) {
            alert("You are not logged in. Redirecting to login page.");
            window.location.href = "login.html";
            return;
        }

        // Fetch chatrooms the user is not a member of
        $http.post("http://localhost:40111/topichive", {
            infoClass1: userEmail,
            action: "browseChatrooms"
        }).then(function(response) {
            $scope.chatrooms = response.data;
        });

//        $scope.logout = function() {
//                    console.log("Logout button clicked"); // Debugging log
//                    sessionStorage.removeItem("EMAIL"); // Clear session data
//                    alert("You have been logged out.");
//                    window.location.href = "login.html"; // Redirect to login page
//        };

        // Join a chatroom
        $scope.joinChatroom = function(chatroomId) {
            $http.post("http://localhost:40111/topichive", {
                infoClass1: chatroomId.toString(),
                infoClass2: userEmail,
                action: "joinChatroom"
            }).then(function() {
                alert("You have joined the chatroom!");
                window.location.href = "home.html";
            });
        };
    });
