/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

ctrls.controller('provCtrl', function($scope, $http, services, $rootScope, $timeout, spinner,postsPath) {
    $scope.$on('newPostBroadcast', function(e, j) {
//        console.log(j);
        $scope.proverbs.unshift(j);
    });
    
    $scope.spinner = spinner;
    $scope.proverbs = [];

    $scope.like = function(index) {
        var proverb = $scope.proverbs[index];
        proverb.liking = true;
        $timeout(function() {
            proverb.liked = true;
            proverb.likes++;
            proverb.liking = false;
        }, 1000);
    };
    
    $scope.unlike = function(index) {
        var proverb = $scope.jokes[index];
        proverb.liking = true;
        $timeout(function() {
            proverb.liked = false;
            proverb.likes--;
            proverb.liking = false;
        }, 1000);
    };

    $scope.loadComments = function(index) {
        var proverb = $scope.proverbs[index];
        proverb.showComments = true;
    };
    $scope.hideComments = function(index) {
        var proverb = $scope.proverbs[index];
        proverb.showComments = false;
    };
    $scope.comment = function(index) {
        var proverb = $scope.proverbs[index];
        if (angular.isUndefined(proverb.myComment))
            return;
        proverb.oncomment = true;
        $timeout(function() {
            proverb.oncomment = false;
            var c = {
                text: proverb.myComment,
                poster: 'jack bauer',
                duration: '3 jan 2015',
                imageURL: '/scribbleit/posts/img/male.jpg'
            };
            proverb.comments.push(c);
            proverb.commentsLength++;
            proverb.myComment = '';
            proverb.showComments = true;
            proverb.makeComment = !proverb.makeComment;
        }, 1000);
    };


    $scope.reports = {};
    $scope.report = function() {
        console.log($scope.reports);
        $scope.selectedProverb.reporting = true;
        $timeout(function() {
            $scope.selectedProverb.reported = true;
            $scope.selectedProverb.reporting = false;
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
            $http.get(postsPath+'/load/proverb?start=0&size=10').success(function(resp) {
            $rootScope.loading = false;//hide loading..
            $scope.proverbs = resp;//display jokes

        }).error(function(r){
            $rootScope.loading = false;
        });
    }

    $scope.openReport = function(index) {

        $scope.selectedProverb = $scope.proverbs[index];
        $scope.reportAlerts = [];
//            $scope.selectedJoke
        services.openDialog('reportModal');
    };



});
