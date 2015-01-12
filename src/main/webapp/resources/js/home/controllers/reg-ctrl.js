/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('loginCtrl', ['$scope', '$http', 'usersPath', 'services', function($scope, $http, usersPath, services) {
        $scope.login = function() {
            $scope.alerts = services.buildAlerts([{code: 500, msg: 'bad credentials'}]);
        };
    }])

.controller('signupCtrl', ['$scope', '$http', 'usersPath', 'services', '$timeout', function($scope, $http, usersPath, services, $timeout) {
        $scope.register = function() {
            console.log($scope.user);
            $scope.loading = true;
            $timeout(function() {
                $scope.alerts = services.buildAlerts([{code: 500, msg: 'user ' + $scope.user.screenName + ' created. check your email to activate your account'}]);
                $scope.loading = false;
            }, 1000);
        };
    }])

.controller('fpasswordCtrl', ['$scope', '$http', 'usersPath', 'services','$timeout', function($scope, $http, usersPath, services, $timeout) {
        $scope.isCollapsed = true;
        $scope.loading = false;

        $scope.forgetToken = function() {
            $scope.loading = true;
            $timeout(function() {
                $scope.alerts = services.buildAlerts([{code: 0, msg: 'password reset token is : '+Math.floor((Math.random() * 100000) + 1) }]);
                $scope.loading = false;
                $scope.isCollapsed = false;
            }, 1000);
//            $http.get('/selfcare/users/password/forgot/' + $scope.User.mdn).success(function(resp) {
//                $scope.loading = false;
//                if (resp.status)
//                    $scope.isCollapsed = false;
//                $scope.alerts = services.buildAlerts(resp.msgs);
//            });
        };

        $scope.resetPassword = function() {
            $timeout(function() {
                $scope.alerts = services.buildAlerts([{code: 0, msg: 'password reset successful'}]);
                $scope.loading = false;
                $scope.isCollapsed = true;
            }, 1000);
//            if (confirm('reset password?') === false)
//                return;
//            $scope.loading = true;
//            $http.post('/selfcare/users/password/reset', $scope.User).success(function(resp) {
//                $scope.loading = false;
//                $scope.alerts = aServices.buildAlerts(resp.msgs);
//                if (resp.status)
//                    $scope.isCollapsed = true;
//            });
        };
    }]);