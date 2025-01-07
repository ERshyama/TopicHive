angular.module("myApp", [])
    .controller("HomeController", function($scope, $http) {
        $scope.chatrooms = [];
        $scope.isAdmin = false;

        // Retrieve the logged-in user's email from sessionStorage
        const userEmail = sessionStorage.getItem("EMAIL");
        if (!userEmail) {
            alert("You are not logged in. Redirecting to login page.");
            window.location.href = "login.html";
            return;
        }

        // Logout the user
        $scope.logout = function() {
            console.log("Logout button clicked"); // Debugging log
            sessionStorage.removeItem("EMAIL"); // Clear session data
            alert("You have been logged out.");
            window.location.href = "login.html"; // Redirect to login page
        };

        // Check if the user is an admin
        $http.post("http://localhost:40111/topichive", {
            infoClass1: userEmail,
            action: "checkAdmin"
        }).then(function(response) {
            $scope.isAdmin = response.data.isAdmin;
        }).catch(function(error) {
            console.error("Error checking admin status:", error);
        });

        // Fetch chatrooms the user is a member of
        function fetchChatrooms() {
            $http.post("http://localhost:40111/topichive", {
                infoClass1: userEmail,
                action: "getChatrooms"
            }).then(function(response) {
                $scope.chatrooms = response.data;
            }).catch(function(error) {
                console.error("Error fetching user chatrooms:", error);
            });
        }

        // Initial fetch of chatrooms
        fetchChatrooms();

        $scope.browseChatrooms = function() {
            window.location.href = "browse_chatrooms.html";
        };

        $scope.openChatroom = function(chatroomId) {
            window.location.href = `/chatroom.html?id=${chatroomId}`;
        };

        $scope.viewAllUsers = function() {
            console.log("I am in the javascript file. tadadada");
            window.location.href = "view_users.html";
        };

        $scope.manageChatrooms = function() {
            window.location.href = "manage_chatrooms.html";
        };

        $scope.leaveChatroom = function(chatroomId) {
                    $http.post("http://localhost:40111/topichive", {
                        infoClass1: chatroomId,
                        infoClass2: userEmail,
                        action: "leaveChatroom"
                    }).then(function(response) {
                        if (response.data.success) {
                            alert("You have left the chatroom.");
                            fetchChatrooms(); // Refresh the chatrooms list
                        } else {
                            alert("Failed to leave the chatroom.");
                        }
                    }).catch(function(error) {
                        console.error("Error leaving chatroom:", error);
                    });
        };

    });

