/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('headerCtrl', ['$scope', '$http', function($scope, $http) {
        $scope.fullname = 'dubic uzuegbu';
        $scope.pop = false;
        $scope.testDialog = function() {
            $scope.dopen = true;
        };
    }]);