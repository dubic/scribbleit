/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('quotesCtrl', function ($scope, $http, services, $rootScope, $timeout, spinner, postsPath, $stateParams,$window) {
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
    
    $scope.share = function (i,quote) {
        var u = '/p/q/';
        var shareurls=[
            'http://www.facebook.com/share.php?'+$.param({u:$rootScope.endpoint+u+quote.id,t:quote.post}),
            'https://twitter.com/intent/tweet?'+$.param({url:$rootScope.endpoint+u+quote.id,text:quote.post}),
            'https://mail.google.com/mail/?'+$.param({view:'cm',fs:1,su:'share a quote',body:quote.post+'['+$rootScope.endpoint+u+quote.id+']'}),
            'https://plus.google.com/share?'+$.param({url:$rootScope.endpoint+u+quote.id})
        ];
        $window.open(shareurls[i], "MsgWindow", "width=700, height=600");
    };


    $scope.loadComments = function (quote) {
        quote.loadingComments = true;
        $http.get(postsPath + '/comments/' + quote.id).success(function (comments) {
            quote.loadingComments = false;
            quote.comments = comments;
            quote.commentsLength = comments.length;
        }).error(function (r) {
            quote.loadingComments = false;
        });
        quote.showComments = true;
    };
    $scope.hideComments = function (quote) {
        quote.showComments = false;
    };
    $scope.comment = function (index) {
        var quote = $scope.quotes[index];
        if (angular.isUndefined(quote.myComment))
            return;
        quote.oncomment = true;
        $http.post(postsPath + '/comment/' + quote.id, JSON.stringify(quote.myComment)).success(function (resp) {
            if (resp.code === 403) {
                services.notify("Login to continue", $rootScope);
                $rootScope.route('login');
                return;
            }

            quote.oncomment = false;
            quote.comments = resp.comments;
            quote.commentsLength = resp.comments.length;
            quote.myComment = '';
            quote.showComments = true;
            quote.makeComment = !quote.makeComment;
        }).error(function (data, status) {
            quote.oncomment = false;
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
    $scope.loadById = function (id) {
        services.showMsg('Loading...');
        $http.get(postsPath + '/load/quote/' + id).success(function (resp) {
            services.hideMsg();//hide loading..
            $scope.quotes = resp;//display jokes

        }).error(function (data, status) {
            services.hideMsg();
        });
    };

    $scope.query = {
        page: 1,
        size: 5,
        id: 0
    };
    $scope.loadPosts = function () {
        services.showMsg('Loading Quotes...');
        var p = {
            start: ($scope.query.page - 1) * $scope.query.size, size: $scope.query.size, id: $scope.query.id
        };
        $http.post(postsPath + '/load/quote?start=0&size=10', p).success(function (resp) {
            services.hideMsg();//hide loading..
            $scope.quotes = resp.posts;//display jokes

        }).error(function (r) {
            services.hideMsg();
        });
    };

    $scope.openReport = function (post) {
        $scope.reports = {};
        $scope.selectedPost = post;
        $scope.reportAlerts = [];
        services.openDialog('reportModal');
    };


    ///////INIT FUNCTIONS///////////
    console.log($stateParams);
    if (!angular.isUndefined($stateParams.id) && $stateParams.id !== '' && $stateParams.id !== null) {
        $scope.loadById($stateParams.id);
    } else {
        $scope.loadPosts();
    }
});