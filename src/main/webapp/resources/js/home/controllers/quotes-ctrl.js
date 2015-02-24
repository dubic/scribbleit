/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('quotesCtrl', function($scope, $http, services, $rootScope, $timeout, spinner,postsPath) {
    $scope.$on('newPostBroadcast', function(e, j) {
//        console.log(j);
        $scope.quotes.unshift(j);
    });

    $scope.spinner = spinner;
    $scope.quotes = [];

    $scope.like = function(index) {
        var quote = $scope.quotes[index];
        quote.liking = true;
        $timeout(function() {
            quote.liked = true;
            quote.likes++;
            quote.liking = false;
        }, 1000);
    };

    $scope.unlike = function(index) {
        var quote = $scope.quotes[index];
        quote.liking = true;
        $timeout(function() {
            quote.liked = false;
            quote.likes--;
            quote.liking = false;
        }, 1000);
    };


    $scope.loadComments = function(index) {
        var quote = $scope.quotes[index];
        quote.showComments = true;
    };
    $scope.hideComments = function(index) {
        var quote = $scope.quotes[index];
        quote.showComments = false;
    };
    $scope.comment = function(index) {
        var quote = $scope.quotes[index];
        if (angular.isUndefined(quote.myComment))
            return;
        quote.oncomment = true;
        $timeout(function() {
            quote.oncomment = false;
            var c = {
                text: quote.myComment,
                poster: 'jack bauer',
                duration: '3 jan 2015',
                imageURL: '/scribbleit/posts/img/male.jpg'
            };
            quote.comments.push(c);
            quote.commentsLength++;
            quote.myComment = '';
            quote.showComments = true;
            quote.makeComment = !quote.makeComment;
        }, 1000);
    };


    $scope.reports = {};
    $scope.report = function() {
        console.log($scope.reports);
        $scope.selectedQuote.reporting = true;
        $timeout(function() {
            $scope.selectedQuote.reported = true;
            $scope.selectedQuote.reporting = false;
            $scope.reports = {};
            services.closeDialog('reportModal');
            services.notify("post reported successfully", $rootScope);

        }, 1000);
    };



    ///////INIT FUNCTIONS///////////
    loadPosts();
///////////////////////////
    function loadPosts() {
        $rootScope.loading = true;
        $rootScope.loading = true;
        $http.get(postsPath + '/load/quote?start=0&size=10').success(function(resp) {
            $rootScope.loading = false;//hide loading..
            $scope.quotes = resp;//display jokes

        }).error(function(r) {
            $rootScope.loading = false;
        });
    }

    $scope.openReport = function(index) {

        $scope.selectedQuote = $scope.quotes[index];
        $scope.reportAlerts = [];
//            $scope.selectedJoke
        services.openDialog('reportModal');
    };



});