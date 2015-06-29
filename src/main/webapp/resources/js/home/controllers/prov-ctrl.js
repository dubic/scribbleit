/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

ctrls.controller('provCtrl', function ($scope, $http, services, $rootScope, $timeout, spinner, postsPath, $stateParams) {
    $scope.$on('newPostBroadcast', function (e, j) {
//        console.log(j);
        $scope.proverbs.unshift(j);
    });

    $scope.spinner = spinner;
    $scope.proverbs = [];

    $scope.like = function (proverb) {
        proverb.liking = true;
        $timeout(function () {
            proverb.liked = true;
            proverb.likes++;
            proverb.liking = false;
        }, 1000);
    };

    $scope.unlike = function (proverb) {
        proverb.liking = true;
        $timeout(function () {
            proverb.liked = false;
            proverb.likes--;
            proverb.liking = false;
        }, 1000);
    };

    $scope.loadComments = function (proverb) {
        proverb.loadingComments = true;
        $http.get(postsPath + '/comments/' + proverb.id).success(function (comments) {
            proverb.loadingComments = false;
            proverb.comments = comments;
            proverb.commentsLength = comments.length;
        }).error(function (r) {
            proverb.loadingComments = false;
        });
        proverb.showComments = true;
    };
    $scope.hideComments = function (proverb) {
        proverb.showComments = false;
    };
    $scope.comment = function (index) {
        var proverb = $scope.proverbs[index];
        if (angular.isUndefined(proverb.myComment))
            return;
        proverb.oncomment = true;
        $http.post(postsPath + '/comment/' + proverb.id, JSON.stringify(proverb.myComment)).success(function (resp) {
            if (resp.code === 403) {
                services.notify("Login to continue", $rootScope);
                $rootScope.route('login');
                return;
            }

            proverb.oncomment = false;
            proverb.comments = resp.comments;
            proverb.commentsLength = resp.comments.length;
            proverb.myComment = '';
            proverb.showComments = true;
            proverb.makeComment = !proverb.makeComment;
        }).error(function (r) {
            proverb.oncomment = false;
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



    ///////INIT FUNCTIONS///////////
    if (!angular.isUndefined($stateParams.id) && $stateParams.id !== '' && $stateParams.id !== null) {
        loadById($stateParams.id);
    } else {
        loadPosts();
    }
///////////////////////////
    function loadById(id) {
        $rootScope.loading = true;
        $http.get(postsPath + '/load/proverb/' + id).success(function (resp) {
            $rootScope.loading = false;//hide loading..
            $scope.proverbs = resp;//display jokes

        }).error(function (data, status) {
            $rootScope.loading = false;
        });
    }

    function loadPosts() {
        $rootScope.loading = true;
        $http.get(postsPath + '/load/proverb?start=0&size=10').success(function (resp) {
            $rootScope.loading = false;//hide loading..
            $scope.proverbs = resp;//display jokes

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
