/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('mainCtrl', ['$scope', '$http', '$rootScope', '$timeout', 'services', function($scope, $http, $rootScope, $timeout, services) {
//        $timeout(function() {
//            $rootScope.$broadcast('$testBroadcast', 'next');
//        }, 2000);
        $scope.dialog = function() {
//            alert('ok');
            BootstrapDialog.alert('I want banana!');
        };
        $scope.Post = {};
        $scope.newtag = false;
        $scope.tagcloud = ['akpos', 'politics', 'football'];

        
        $scope.newpost = function() {
            if (angular.isUndefined($scope.Post.msg))
                return;
            $scope.newposting = true;
            $timeout(function() {
                $rootScope.$broadcast('newJokeBroadcast', {
                    title: $scope.Post.title,
                    id: 3,
                    dislikes: 0,
                    likes: 8,
                    duration: '5 jan 2015',
                    post: $scope.Post.msg,
                    poster: 'Bob Nilson',
                    imageURL: '/scribbleit/posts/img/male.jpg',
                    commentsLength: 0,
                    comments: [],
                    tags: $scope.Post.tags ? $scope.Post.tags.split(",") : []
                });
                $scope.newposting = false;
                $scope.Post = {};
                $scope.openpost = !$scope.openpost;
                services.notify("post saved successfully",$rootScope);
            }, 1000);
        };
    }]);