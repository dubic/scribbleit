/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('jokesCtrl', function ($scope, $http, services, $rootScope, $timeout, spinner, postsPath, $stateParams,$window) {
    $scope.$on('newPostBroadcast', function (e, j) {
        console.log(j);
        $scope.jokes.unshift(j);
    });
    console.log($stateParams);
    $scope.spinner = spinner;
    $scope.jokes = [];

    $scope.share = function (i,joke) {
        var u = '/p/j/';
        var shareurls=[
            'http://www.facebook.com/share.php?'+$.param({u:$rootScope.endpoint+u+joke.id,t:joke.post}),
            'https://twitter.com/intent/tweet?'+$.param({url:$rootScope.endpoint+u+joke.id,text:joke.post}),
            'https://mail.google.com/mail/?'+$.param({view:'cm',fs:1,su:'share a joke',body:joke.post+'['+$rootScope.endpoint+u+joke.id+']'}),
            'https://plus.google.com/share?'+$.param({url:$rootScope.endpoint+u+joke.id})
        ];
        $window.open(shareurls[i], "MsgWindow", "width=700, height=600");
    };

    $scope.loadComments = function (joke) {
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
    $scope.hideComments = function (joke) {
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
        }).error(function (data, status) {
            joke.oncomment = false;
            if (status === 407) {
                $rootScope.sessionTimeout();
                services.popLogin();
            } else
                services.notify("Service unavailable");
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
        services.showMsg('Loading joke...');
        $http.get(postsPath + '/load/joke/' + id).success(function (resp) {
            services.hideMsg(); //hide loading..
            $scope.jokes = resp; //display jokes
        }).error(function (data, status) {
            services.hideMsg();
        });
    }

//$scope.lastDate = ;

    $scope.query = {
        page: 1,
        size: 5,
        id: 0
    };
    $scope.loadPosts = function () {
        services.showMsg('Loading Jokes...');

        var p = {
            start: ($scope.query.page - 1) * $scope.query.size, size: $scope.query.size, id: $scope.query.id
        };
        $http.get(postsPath + '/load/joke?' + $.param(p)).success(function (resp) {
            services.hideMsg(); //hide loading..
            $scope.jokes = resp.posts; //display jokes
            $scope.totalPosts = resp.total;
            if ($scope.query.id === 0) {
                $scope.query.id = resp.posts[0].id;
            }
//            if((p.start*p.size) >= resp.total){
//                $scope.moreJokes = false;
//            }
        }).error(function (data, status) {
            services.hideMsg();
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

   
    $scope.forTags = function (t) {
        $scope.query = {
            page: 1,
            size: 5,
            id: 0
        };
        var p = {
            start: ($scope.query.page - 1) * $scope.query.size, size: $scope.query.size, id: $scope.query.id
        };
        services.showMsg('Loading jokes on ' + t);
        $http.get(postsPath + '/load/joke/tag/' + t + '?' + $.param(p)).success(function (resp) {
            services.hideMsg(); //hide loading..
            $scope.jokes = resp.posts; //display jokes
            $scope.totalPosts = resp.total;
            if ($scope.query.id === 0) {
                $scope.query.id = resp.posts[0].id;
            }
        });
    };

    ///////INIT FUNCTIONS///////////
//    console.log(angular.isNumber($stateParams.id));
    if (!angular.isUndefined($stateParams.id) && $stateParams.id !== '' && $stateParams.id !== null) {
        loadById($stateParams.id);
    } else {
        $scope.loadPosts();
    }
});