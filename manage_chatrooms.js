let url = "http://localhost:40111/topichive";

angular.module("chatroomApp", [])
    .controller("ChatroomController", function($scope, $http) {
        $scope.chatrooms = [];
        $scope.newChatroom = { name: "", description: "" };

        // Fetch all chatrooms
        function fetchChatrooms() {
            $http.post(url, { action: "getAllChatrooms" })
                .then(function(response) {
                    $scope.chatrooms = response.data;
                })
                .catch(function(error) {
                    console.error("Error fetching chatrooms:", error);
                });
        }

        // Create a new chatroom
        $scope.createChatroom = function() {
            if ($scope.newChatroom.name && $scope.newChatroom.description) {
                $http.post(url, {
                    action: "createChatroom",
                    infoClass1: JSON.stringify($scope.newChatroom)
                }).then(function(response) {
                    if (response.data.success) {
                        alert("Chatroom created successfully.");
                        fetchChatrooms();
                        $scope.newChatroom = { name: "", description: "" }; // Reset form
                    } else {
                        alert("Failed to create chatroom: " + response.data.message);
                    }
                }).catch(function(error) {
                    console.error("Error creating chatroom:", error);
                });
            } else {
                alert("Please provide both name and description.");
            }
        };

        // Delete a chatroom
        $scope.deleteChatroom = function(chatroomId) {
            $http.post(url, { action: "deleteChatroom", infoClass1: chatroomId })
                .then(function(response) {
                    if (response.data.success) {
                        alert("Chatroom deleted successfully.");
                        fetchChatrooms();
                    } else {
                        alert("Failed to delete chatroom: " + response.data.message);
                    }
                }).catch(function(error) {
                    console.error("Error deleting chatroom:", error);
                });
        };

        // Initial fetch of chatrooms
        fetchChatrooms();
    });
