/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('mainCtrl', function ($scope, $http, $rootScope, $timeout, services, postsPath, $upload) {
    $scope.fileReaderSupported = window.FileReader !== null && (window.FileAPI === null || FileAPI.html5 !== false);
//    console.log('file reader supported : ' + $scope.fileReaderSupported);

    $scope.dialog = function () {
//            alert('ok');
        BootstrapDialog.alert('I want banana!');
    };

    $scope.startNewPost = function () {
        if ($rootScope.isAuthenticated)
            $scope.openpost = !$scope.openpost;
        else
            $rootScope.route('login');
    };

    $scope.Post = {};
    $scope.newtag = false;
    $scope.tagcloud = ['akpos', 'politics', 'football'];


    $scope.newpost = function () {
        if (angular.isUndefined($scope.Post.msg))
            return;

        $scope.newposting = true;
        var p = {
            title: $scope.Post.title,
            post: $scope.Post.msg,
            source: $scope.Post.source,
            tags: $scope.Post.tags
        };
        var TYPE;
        if ($rootScope.activePage === 'home.jokes')
            TYPE = 'JOKE';
        else if ($rootScope.activePage === 'home.proverbs')
            TYPE = 'PROVERB';
        else if ($rootScope.activePage === 'home.quotes')
            TYPE = 'QUOTE';

//console.log($.param(p));
        $scope.newposting = true;
        $upload.upload({
            url: postsPath + '/new/' + TYPE,
            fields: p,
            file: $scope.file
        }).success(function (resp, status, headers, config) {
            $scope.newposting = false;
            if (resp.code === 0) {
                services.notify("post saved successfully");
                $rootScope.$broadcast('newPostBroadcast', resp.post);
                $scope.Post = {};
                $scope.openpost = !$scope.openpost;
            }
            else if (resp.code === 500)
                services.notify("Unexpected Error", $rootScope);
            else if (resp.code === 403) {
                services.notify("Login to continue", $rootScope);
                $rootScope.route('login');
            }
        }).error(function (data, status) {
            $scope.newposting = false;
            services.notify("Service unavailable");
        });


//        $http.post(postsPath + '/new/' + TYPE, $.param(p)).success(function (resp) {
//            $scope.newposting = false;
//            if (resp.code === 0) {
//                services.notify("post saved successfully");
//                $rootScope.$broadcast('newPostBroadcast', resp.post);
//                $scope.Post = {};
//                $scope.openpost = !$scope.openpost;
//            }
//            else if (resp.code === 500)
//                services.notify("Unexpected Error", $rootScope);
//            else if (resp.code === 403) {
//                services.notify("Login to continue", $rootScope);
//                $rootScope.route('login');
//            }
//        }).error(function (data, status) {
//            $scope.newposting = false;
//            services.notify("Service unavailable");
//        });
    };

    $scope.ustage = 0;
    $scope.generateThumb = function (file) {
        console.log(file);
        if (file !== null && angular.isDefined(file)) {
            if ($scope.fileReaderSupported && file.type.indexOf('image') > -1) {
                $scope.ustage = 1;
                $scope.file = file;
                $timeout(function () {
                    var fileReader = new FileReader();
                    fileReader.readAsDataURL(file);
                    fileReader.onload = function (e) {
                        $timeout(function () {
//                            file.dataUrl = e.target.result;
                            $scope.imgUrl = e.target.result;
                            $scope.ustage = 2;
                        });
                    };
                });
            } else {
                services.notify('file must be an image!');
            }
        }
    };

    $scope.removeImage = function () {
        $scope.file = undefined;
        $scope.imgUrl = undefined;
        $scope.ustage = 0;
    };
});