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

ctrls.controller('activityCtrl', function($scope, $http, $rootScope, $timeout, services, $stateParams, imagePath) {
    $scope.imagePath = imagePath;
    $scope.Profile = {};
    $scope.Activity = {};


    $scope.load = function() {
        console.log($stateParams);
        $scope.Profile.isMe = false;
        $scope.Activity.posts = [
            {
                date: '23-jan-2015',
                title: 'one bright summer morning',
                text: 'The following sections will give an overview of common tasks that can be performed via Facebook and its sub-APIs. For complete details on all of the operations available, refer to the JavaDoc.',
                readMore: true
            },
            {
                date: '20-jan-2015',
                title: 'to damascus',
                text: 'You can retrieve the authenticated user’s Facebook profile data using the Facebook#userOperations.getUserProfile() method',
                readMore: false
            }
        ];

    };
    $scope.load();

});

ctrls.controller('accountCtrl', function($scope, $http, $rootScope, $timeout, services, $stateParams, imagePath) {
    $scope.imagePath = imagePath;
    $scope.Account = {};
    $scope.Profile = {};

    $scope.load = function() {
        if (angular.isUndefined($stateParams.user))
            return;
        $rootScope.loading = true;
        $timeout(function() {
            $scope.Profile.isMe = true;
            $scope.Account.firstname = 'krusty';
            $scope.Account.lastname = 'mc cloughklin';
            $scope.Account.email = 'udubic@gmail.com';
            $scope.Account.screenName = $stateParams.user;
            $scope.Account.date = '7 oct 2014';
            $rootScope.loading = false;
        }, 1000);
    };
    $scope.load();

});

ctrls.controller('pwordCtrl', function($scope, $http, $rootScope, $timeout, services) {
    $scope.savePassword = function() {
        $scope.saving = true;
        $timeout(function() {
            $scope.pwordalerts = services.buildAlerts([{code: 0, msg: 'password changed successfully'}]);
            $scope.saving = false;
        }, 1000);
    };

});