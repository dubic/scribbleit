/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('quotesCtrl', function ($scope, $http, services, $rootScope, $timeout, spinner, postsPath, $stateParams) {
    $scope.$on('newPostBroadcast', function (e, j) {
//        console.log(j);
        $scope.quotes.unshift(j);
    });

    $scope.spinner = spinner;
    $scope.quotes = [];

    $scope.like = function (index) {
        var quote = $scope.quotes[index];
        quote.liking = true;
        $timeout(function () {
            quote.liked = true;
            quote.likes++;
            quote.liking = false;
        }, 1000);
    };

    $scope.unlike = function (index) {
        var quote = $scope.quotes[index];
        quote.liking = true;
        $timeout(function () {
            quote.liked = false;
            quote.likes--;
            quote.liking = false;
        }, 1000);
    };


    $scope.loadComments = function (index) {
        var quote = $scope.quotes[index];
        quote.showComments = true;
    };
    $scope.hideComments = function (index) {
        var quote = $scope.quotes[index];
        quote.showComments = false;
    };
    $scope.comment = function (index) {
        var quote = $scope.quotes[index];
        if (angular.isUndefined(quote.myComment))
            return;
        quote.oncomment = true;
        $timeout(function () {
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
    $scope.report = function () {
        $scope.selectedPost.reporting = true;
        $scope.reports.postId = $scope.selectedPost.id;
        $http.post(postsPath + '/report', $scope.reports).success(function (resp) {
            $scope.selectedPost.reporting = false;
            if (resp.code === 0) {
                $scope.reports = {};
                $scope.repActive = false;
                services.closeDialog('reportModal');
                services.notify("post reported successfully");
            } else if (resp.code === 501) {
                for (var i = 0; i < resp.msgs.length; i++) {
                    $scope.reportAlerts.push({class: 'alert-danger', msg: resp.msgs[i]});
                }
            } else {
                $scope.reportAlerts.push({class: 'alert-danger', msg: 'Unexpected server error'});
                services.notify("Unexpected server error");
            }
        }).error(function (data, status) {
            $scope.selectedPost.reporting = false;
            $scope.reportAlerts.push({class: 'alert-danger', msg: 'Unexpected server error'});
            services.notify("Unexpected server error occurred");
        });
    };


    ///////INIT FUNCTIONS///////////
    if (!angular.isUndefined($stateParams.id) && $stateParams.id !== '' && $stateParams.id !== null) {
        loadById($stateParams.id);
    } else {
        loadPosts();
    }
///////////////////////////
    function loadById(id) {
        $rootScope.loading = true;
        $http.get(postsPath + '/load/quote/' + id).success(function (resp) {
            $rootScope.loading = false;//hide loading..
            $scope.quotes = resp;//display jokes

        }).error(function (data, status) {
            $rootScope.loading = false;
        });
    }

    function loadPosts() {
        $rootScope.loading = true;
        $rootScope.loading = true;
        $http.get(postsPath + '/load/quote?start=0&size=10').success(function (resp) {
            $rootScope.loading = false;//hide loading..
            $scope.quotes = resp;//display jokes

        }).error(function (r) {
            $rootScope.loading = false;
        });
    }

    $scope.openReport = function (post) {
        $scope.reports = {};
        $scope.selectedPost = post;
        $scope.reportAlerts = [];
        services.openDialog('reportModal');
    };



});