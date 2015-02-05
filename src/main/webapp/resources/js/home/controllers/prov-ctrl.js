/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

ctrls.controller('provCtrl', function($scope, $http, services, $rootScope, $timeout, spinner) {
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
//        it('should be true',function(){
//            expect(view).toEqual(0); 
//        });
//        $timeout(function() {

            var r = [{
                    id: 1,
                    likes: 6,
                    duration: '31 dec 2014',
                    post: 'A tree that does not know how to dance will be taught by the wind',
                    poster: 'dubine uzuegbu',
                    imageURL: '/scribbleit/posts/img/male.jpg',
                    commentsLength: 5,
                    comments: [],
                    tags: ['Africa', 'akpos', 'politics']
                }, {
                    id: 2,
                    title: 'Ay the comedian',
                    likes: 3,
                    duration: '30 dec 2014',
                    post: 'One does not cut off his nose to spite his face',
                    poster: 'remy martin',
                    imageURL: '/scribbleit/posts/img/male.jpg',
                    commentsLength: 0,
                    comments: [],
                    tags: []
                }];
            $rootScope.loading = false;//hide loading..
            $scope.proverbs = r;//display jokes

//        }, 1000);
    }

    $scope.openReport = function(index) {

        $scope.selectedProverb = $scope.proverbs[index];
        $scope.reportAlerts = [];
//            $scope.selectedJoke
        services.openDialog('reportModal');
    };



});
