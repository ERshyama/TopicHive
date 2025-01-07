
angular.module("chatApp", [])
    .controller("ChatController", function($scope, $http, $interval) {
        $scope.chatroomName = "Loading...";
        $scope.messages = [];
        $scope.newMessage = "";
        const chatroomId = new URLSearchParams(window.location.search).get("id");
        const userEmail = sessionStorage.getItem("EMAIL");

        if (!chatroomId || !userEmail) {
            alert("Invalid chatroom or user session. Redirecting to home.");
            window.location.href = "home.html";
            return;
        }

        // Fetch chatroom name
        function fetchChatroomDetails() {
            const dataToServer = {
                infoClass1: chatroomId,
                action: "getChatroomDetails"
            };

            $http.post("http://localhost:40111/topichive", dataToServer)
                .then(function(response) {
                    if (response.data.error) {
                        alert(response.data.error);
                        window.location.href = "home.html";
                    } else {
                        $scope.chatroomName = response.data.name;
                    }
                })
                .catch(function(error) {
                    console.error("Error fetching chatroom details:", error);
                });
        }

        // Fetch chatroom messages
        function fetchMessages() {
            const dataToServer = {
                infoClass1: chatroomId,
                action: "retrieveChats"
            };

            $http.post("http://localhost:40111/topichive", dataToServer)
                .then(function(response) {
                    $scope.messages = response.data; // Messages array
                })
                .catch(function(error) {
                    console.error("Error fetching messages:", error);
                });
        }

        // Fetch messages periodically
        fetchMessages();
        $interval(fetchMessages, 5000); // Refresh every 5 seconds

        // Fetch chatroom details on load
        fetchChatroomDetails();

        // Send a new message
        $scope.sendMessage = function() {
            if (!$scope.newMessage.trim()) {
                return;
            }

            const dataToServer = {
                infoClass1: chatroomId,
                infoClass2: JSON.stringify({
                    text: $scope.newMessage,
                    email: userEmail
                    email: userEmail
                }),
                action: "sendMessage"
            };

            $http.post("http://localhost:40111/topichive", dataToServer)
                .then(function() {
                    $scope.newMessage = ""; // Clear input box
                    fetchMessages(); // Refresh messages
                })
                .catch(function(error) {
                    console.error("Error sending message:", error);
                });
        };
    });

