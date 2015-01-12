/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//angular.module('controllers', [])

ctrls.controller('RegController', ['$scope', '$http', function($scope, $http) {
        $scope.register = function() {
            $scope.reg.posting = true;
            $http.post('/scribbleit/users/register', $scope.user).success(function(resp) {
                $scope.reg.posting = false;
                console.log(resp);
                $scope.reg.build(resp);
            });
        };
    }])



  