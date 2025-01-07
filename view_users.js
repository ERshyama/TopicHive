
let url = "http://localhost:40111/topichive";

angular.module("userApp", [])
    .controller("UserController", function($scope, $http) {
        $scope.users = [];

        // Fetch all users
        function fetchUsers() {
            $http.post(url, { action: "getAllUsers" })
                .then(function(response) {
                    $scope.users = response.data; // Users array with isBlocked and isAdmin status
                })
                .catch(function(error) {
                    console.error("Error fetching users:", error);
                });
        }

        // Ban a user
        $scope.banUser = function(email) {
            $http.post(url, { action: "banUser", infoClass1: email })
                .then(function(response) {
                    if (response.data.success) {
                        alert("User banned successfully.");
                        fetchUsers(); // Refresh user list
                    } else {
                        alert("Failed to ban user: " + response.data.message);
                    }
                })
                .catch(function(error) {
                    console.error("Error banning user:", error);
                });
        };

        // Remove ban from a user
        $scope.removeBan = function(email) {
            $http.post(url, { action: "removeBan", infoClass1: email })
                .then(function(response) {
                    if (response.data.success) {
                        alert("User ban removed successfully.");
                        fetchUsers(); // Refresh user list
                    } else {
                        alert("Failed to remove ban: " + response.data.message);
                    }
                })
                .catch(function(error) {
                    console.error("Error removing ban:", error);
                });
        };

        // Upgrade a regular user to admin
        $scope.upgradeToAdmin = function(email) {
            $http.post(url, { action: "upgradeToAdmin", infoClass1: email })
                .then(function(response) {
                    if (response.data.success) {
                        alert("User upgraded to admin successfully.");
                        fetchUsers(); // Refresh user list
                    } else {
                        alert("Failed to upgrade user to admin: " + response.data.message);
                    }
                })
                .catch(function(error) {
                    console.error("Error upgrading user to admin:", error);
                });
        };

        // Initial fetch
        fetchUsers();
    });
