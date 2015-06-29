/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('jokesCtrl', function ($scope, $http, services, $rootScope, $timeout, spinner, postsPath, $stateParams) {
    $scope.$on('newPostBroadcast', function (e, j) {
        console.log(j);
        $scope.jokes.unshift(j);
    });
    console.log($stateParams);
    $scope.spinner = spinner;
    $scope.jokes = [];
    $scope.like = function (joke) {
        joke.liking = true;
        $timeout(function () {
            joke.liked = true;
            joke.likes++;
            joke.liking = false;
        }, 1000);
    };
    $scope.unlike = function (joke) {
        joke.liking = true;
        $timeout(function () {
            joke.liked = false;
            joke.likes--;
            joke.liking = false;
        }, 1000);
    };
    $scope.loadComments = function (index) {
        var joke = $scope.jokes[index];
        joke.loadingComments = true;
        $http.get(postsPath + '/comments/' + joke.id).success(function (comments) {
            joke.loadingComments = false;
            joke.comments = comments;
            joke.commentsLength = comments.length;
        }).error(function (r) {
            joke.loadingComments = false;
        });
        joke.showComments = true;
    };
    $scope.hideComments = function (index) {
        var joke = $scope.jokes[index];
        joke.showComments = false;
    };
    $scope.comment = function (index) {
        var joke = $scope.jokes[index];
        if (angular.isUndefined(joke.myComment))
            return;
        joke.oncomment = true;
        $http.post(postsPath + '/comment/' + joke.id, JSON.stringify(joke.myComment)).success(function (resp) {
            if (resp.code === 403) {
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
        }).error(function (r) {
            joke.oncomment = false;
        });
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

///////////////////////////
    function loadById(id) {
        $rootScope.loading = true;
        $http.get(postsPath + '/load/joke/' + id).success(function (resp) {
            $rootScope.loading = false; //hide loading..
            $scope.jokes = resp; //display jokes
        }).error(function (data, status) {
            $rootScope.loading = false;
        });
    }

//$scope.lastDate = ;

    $scope.query = {
        page: 1,
        size: 5,
        id: 0
    };
    $scope.loadPosts = function () {
        $rootScope.loading = true;

        var p = {
            start: ($scope.query.page - 1) * $scope.query.size, size: $scope.query.size, id: $scope.query.id
        };
        $http.get(postsPath + '/load/joke?' + $.param(p)).success(function (resp) {
            $rootScope.loading = false; //hide loading..
            $scope.jokes = resp.posts; //display jokes
            $scope.totalPosts = resp.total;
            if($scope.query.id === 0){
                $scope.query.id = resp.posts[0].id;
            }
//            if((p.start*p.size) >= resp.total){
//                $scope.moreJokes = false;
//            }
            console.log(p);
        }).error(function (data, status) {
            $rootScope.loading = false;
        });
    };

    $scope.openReport = function (joke) {
        $scope.reports = {};
        $scope.selectedPost = joke;
        $scope.reportAlerts = [];
        services.openDialog('reportModal');
    };
    $scope.openShare = function (post) {
        $scope.sharetitle = angular.isUndefined(post.title) ? post.post : post.title;
        $scope.selectedPost = post;
        $scope.share = {};
        services.openDialog('shareModal');
    };
    
    $scope.shareByEmail = function () {
        $http.post(postsPath + '/share/email/' + $scope.selectedPost.id, $scope.share).success(function () {

        }).error(function (data, status) {

        });
    };
    
    $scope.nextPage = function(){
        $scope.query.page++;
        $scope.loadPosts();
    };

    ///////INIT FUNCTIONS///////////
//    console.log(angular.isNumber($stateParams.id));
    if (!angular.isUndefined($stateParams.id) && $stateParams.id !== '' && $stateParams.id !== null) {
        loadById($stateParams.id);
    } else {
        $scope.loadPosts();
    }
});