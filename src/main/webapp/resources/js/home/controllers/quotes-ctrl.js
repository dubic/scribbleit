/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('quotesCtrl', function($scope, $http, services, $rootScope, $timeout, spinner) {
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
            services.notify("post reported successfully",$rootScope);
            
        }, 1000);
    };



    ///////INIT FUNCTIONS///////////
    loadPosts();
///////////////////////////
    function loadPosts() {
        $rootScope.loading = true;

//        $timeout(function() {

            var r = [{
                    id: 1,
                    likes: 6,
                    duration: '31 dec 2014',
                    post: 'it is one thing to take a method and try it,if it fails, then try another. But above all try something',
                    poster: 'dubine uzuegbu',
                    source: 'Nyquist',
                    imageURL: '/scribbleit/posts/img/male.jpg',
                    commentsLength: 5,
                    comments: [],
                    tags: ['customs', 'akpos', 'politics']
                }, {
                    id: 2,
                    likes: 3,
                    duration: '30 dec 2014',
                    post: 'to be trusted is a better compliment than to be loved',
                    poster: 'remy martin',
                    source: 'van ludwig',
                    imageURL: '/scribbleit/posts/img/male.jpg',
                    commentsLength: 0,
                    comments: [],
                    tags: []
                }];
            $rootScope.loading = false;//hide loading..
            $scope.quotes = r;//display jokes

//        }, 1000);
    }

    $scope.openReport = function(index) {

        $scope.selectedQuote = $scope.quotes[index];
        $scope.reportAlerts = [];
//            $scope.selectedJoke
        services.openDialog('reportModal');
    };



});