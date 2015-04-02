/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('jokesCtrl', function($scope, $http, services, $rootScope, $timeout, spinner,postsPath) {
    $scope.$on('newPostBroadcast', function(e, j) {
        console.log(j);
        $scope.jokes.unshift(j);
    });
    
    $scope.spinner = spinner;
    $scope.jokes = [];

    $scope.like = function(index) {
        var joke = $scope.jokes[index];
        joke.liking = true;
        $timeout(function() {
            joke.liked = true;
            joke.likes++;
            joke.liking = false;
        }, 1000);
    };
    
    $scope.unlike = function(index) {
        var joke = $scope.jokes[index];
        joke.liking = true;
        $timeout(function() {
            joke.liked = false;
            joke.likes--;
            joke.liking = false;
        }, 1000);
    };


    $scope.loadComments = function(index) {
        var joke = $scope.jokes[index];
        joke.loadingComments = true;
        $http.get(postsPath+'/comments/'+joke.id).success(function(comments) {
            joke.loadingComments = false;
            joke.comments = comments;
            joke.commentsLength = comments.length;
        }).error(function(r){
            joke.loadingComments = false;
        });
        joke.showComments = true;
    };
    $scope.hideComments = function(index) {
        var joke = $scope.jokes[index];
        joke.showComments = false;
    };
    
    $scope.comment = function(index) {
        var joke = $scope.jokes[index];
        if (angular.isUndefined(joke.myComment))
            return;
        joke.oncomment = true;
        $http.post(postsPath+'/comment/'+joke.id,JSON.stringify(joke.myComment)).success(function(resp) {
            if(resp.code === 403) {
                services.notify("Login to continue", $rootScope);
                $rootScope.route('login');
                return;
            }
            
            joke.oncomment = false;
            joke.comments = resp.comments;
            joke.commentsLength = resp.comments.length;
            joke.myComment = '';
            joke.showComments = true;
            joke.makeComment = !joke.makeComment;
        }).error(function(r){
            joke.oncomment = false;
        });
    };


    $scope.reports = {};
    $scope.report = function() {
        console.log($scope.reports);
        $scope.selectedJoke.reporting = true;
        $timeout(function() {
            $scope.selectedJoke.reported = true;
            $scope.selectedJoke.reporting = false;
            $scope.reports = {};
            services.closeDialog('reportModal');
            services.notify("post reported successfully",$rootScope);
            
        }, 1000);
    };



    ///////INIT FUNCTIONS///////////
    loadPosts();
///////////////////////////
    function loadPosts() {
        $rootScope.loading = true;

        $http.get(postsPath+'/load/joke?start=0&size=10').success(function(resp) {
            $rootScope.loading = false;//hide loading..
            $scope.jokes = resp;//display jokes

        }).error(function(r){
            $rootScope.loading = false;
        });
    }

    $scope.openReport = function(index) {

        $scope.selectedJoke = $scope.jokes[index];
        $scope.reportAlerts = [];
//            $scope.selectedJoke
        services.openDialog('reportModal');
    };



});