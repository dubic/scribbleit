/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('profileCtrl', function($scope, $http, $rootScope, $timeout, services, $stateParams, imagePath) {
    $scope.imagePath = imagePath;
    $scope.Profile = {};
 

    $scope.load = function() {
//        console.log($stateParams);
        if (angular.isUndefined($stateParams.user))
            return;
        $rootScope.loading = true;
        $timeout(function() {
            $scope.Profile.isMe = false;
            $scope.Profile.sceenName = 'dubic';
            $scope.Profile.name = $stateParams.user;
            $scope.Profile.date = '7 oct 2014';
            $scope.Profile.jokes = Math.floor((Math.random() * 10000) + 1);
            $scope.Profile.proverbs = Math.floor((Math.random() * 1000) + 1);
            $scope.Profile.quotes = Math.floor((Math.random() * 1000) + 1);
            $rootScope.loading = false;
        }, 1000);
    };
    $scope.load();
    $rootScope.navigate('profile.activity');
});