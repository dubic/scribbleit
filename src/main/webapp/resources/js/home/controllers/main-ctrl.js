/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('mainCtrl', function($scope, $http, $rootScope, $timeout, services, postsPath) {

    $scope.dialog = function() {
//            alert('ok');
        BootstrapDialog.alert('I want banana!');
    };

    $scope.startNewPost = function() {
        if ($rootScope.isAuthenticated)
            $scope.openpost = !$scope.openpost;
        else
            $rootScope.route('login');
    };

    $scope.Post = {};
    $scope.newtag = false;
    $scope.tagcloud = ['akpos', 'politics', 'football'];


    $scope.newpost = function() {
        if (angular.isUndefined($scope.Post.msg))
            return;

        $scope.newposting = true;
        var p = {
            title: $scope.Post.title,
//                    id: 3,
//                    dislikes: 0,
//                    likes: 8,
//                    duration: '5 jan 2015',
            post: $scope.Post.msg,
            source: $scope.Post.source,
//                    poster: 'Bob Nilson',
//                    imageURL: '/scribbleit/posts/img/male.jpg',
//                    commentsLength: 0,
//                    comments: [],
            tags: $scope.Post.tags ? $scope.Post.tags.split(",") : []
        };
        var TYPE;
        if ($rootScope.activePage === 'home.jokes') TYPE = 'JOKE';
        else if ($rootScope.activePage === 'home.proverbs') TYPE = 'PROVERB';
        else if ($rootScope.activePage === 'home.quotes') TYPE = 'QUOTE';
        $http.post(postsPath + '/new/' + TYPE, p).success(function(resp) {
            $scope.newposting = false;
            if (resp.code === 0) {
                services.notify("post saved successfully", $rootScope);
                $rootScope.$broadcast('newPostBroadcast', resp.post);
                $scope.Post = {};
                $scope.openpost = !$scope.openpost;
            }
            else if (resp.code === 500)
                services.notify("Unexpected Error", $rootScope);
            else if (resp.code === 403)
                services.notify("Login to continue", $rootScope);
        }).error(function(r) {
            $scope.newposting = false;
            services.notify("Service unavailable", $rootScope);
        });
    };

});