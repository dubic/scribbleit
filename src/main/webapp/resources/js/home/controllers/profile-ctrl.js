/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('profileCtrl', function ($scope, $http, $rootScope, $timeout, services, $stateParams, profPath, $upload) {
    $scope.Profile = {};
    $scope.profileExists = true;


    $scope.loadProfile = function () {
        console.log($stateParams);
        if (angular.isUndefined($stateParams.user) || $stateParams.user === '') {
            $rootScope.route('home.jokes');
            return;
        }
        $rootScope.loadingProfile = true;

        $http.get(profPath + '/details/' + $stateParams.user).success(function (resp) {
            $scope.Profile = resp;
            $rootScope.loadingProfile = false;
            $scope.profileExists = resp.exists;
            if (!resp.session) {
                $rootScope.isAuthenticated = false;
                $rootScope.$broadcast('session.timeout');
            }
            $rootScope.navigate('profile.activity');
        }).error(function (data, status) {
            $rootScope.loadingProfile = false;
            services.notify('unexpected server error occurred');
        });


    };
    $scope.loadProfile();

    $scope.$on('logged.out', function () {
        $scope.loadProfile();
    });


    $scope.upload = function (files) {
        if (files && files.length) {
            for (var i = 0; i < files.length; i++) {
                var file = files[i];

                if (file.type.indexOf('image') === -1) {
                    services.notify('Upload must be an image');
                    continue;
                }
                $scope.uploading = true;
                $scope.progressPercentage = 0;
                $upload.upload({
                    url: $rootScope.usersPath + '/picture/upload',
                    fields: {username: '$scope.username'},
                    file: file
                }).progress(function (evt) {
                    $scope.progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
//                    console.log('progress: ' + $scope.progressPercentage + '% ' + evt.config.file.name);
                }).success(function (data, status, headers, config) {
                    $rootScope.profilePixChanged(data);
                    $scope.uploading = false;
//                    console.log('file ' + config.file.name + 'uploaded. Response: ' + data);
                }).error(function (data, status) {
                    $scope.uploading = false;
                    if (status === 407) {
                        $rootScope.sessionTimeout();
                        $rootScope.route('login');
                    } else
                        services.notify("Service unavailable");
                });
            }
        }
    };

    $scope.$on('profile.pix.changed', function (evt, pic) {
        $scope.Profile.picture = pic;
    });
});

ctrls.controller('activityCtrl', function ($scope, $http, $rootScope, profPath, services, $stateParams) {
//    $scope.Profile = {};
    $scope.Activity = {};


    $scope.load = function () {
        console.log($stateParams.user);
//        $scope.Profile.isMe = false;

        $rootScope.loadingActivity = false;
        $http.get(profPath + '/posts/' + $stateParams.user).success(function (resp) {
            $scope.Activity.posts = resp.posts;
            $rootScope.loadingActivity = false;
            if ($scope.session === false) {
                $rootScope.isAuthenticated = false;
                $rootScope.$broadcast('session.timeout');
            }
        }).error(function (data, status) {
            $rootScope.loadingActivity = false;
            services.notify('unexpected server error occurred');
        });
    };

    $scope.deletePost = function (post) {
        var title = post.title || post.id + '';
        if (!confirm('delete post : ' + title + '?'))
            return;
        
        $scope.deleting = true;
        $http.get(profPath + '/posts/delete/'+post.id).success(function (resp) {
            $scope.deleting = false;
            if (resp.code === 0) {
                $scope.load();
                services.notify('Post deleted successfully');
                $scope.loadProfile();
            } else {
                services.notify(resp.msg);
            }
        }).error(function (data, status) {
            $scope.deleting = false;
            if (status === 407) {
                $rootScope.sessionTimeout();
                services.popLogin();
            } else
                services.notify("Service unavailable");
        });
    };
    $scope.load();

});

ctrls.controller('accountCtrl', function ($scope, $http, $rootScope, $timeout, services, $stateParams, profPath) {

    $scope.Account = $scope.Profile;
//    $scope.loadAcct = function () {
////        console.log($stateParams);
//        if (angular.isUndefined($stateParams.user) || $stateParams.user === '') {
//            $rootScope.route('home.jokes');
//            return;
//        }
//        $rootScope.loadingProfile = true;
//
//        $http.get(profPath + '/details/' + $stateParams.user).success(function (resp) {
//            $scope.Account = resp;
//            $rootScope.loadingProfile = false;
//        }).error(function (data, status) {
//            $rootScope.loadingProfile = false;
//            services.notify('unexpected server error occurred');
//        });
//
//
//    };
//    $scope.loadAcct();

    $scope.saveAccount = function () {
        $scope.saving = true;
        $http.post(profPath + '/update-account', $scope.Account).success(function (resp) {
            $scope.saving = false;
            if (resp.code === 0) {
                $scope.accalerts = [{class: 'alert-success', msg: 'Account details updated!'}];
            } else {
                $scope.accalerts = [{class: 'alert-danger', msg: resp.msg}];
            }
        }).error(function (data, status) {
            $scope.saving = false;
            if (status === 407) {
                $rootScope.sessionTimeout();
                services.popLogin();
            } else
                services.notify("Service unavailable");
        });
    };

});

ctrls.controller('pwordCtrl', function ($scope, $http, $rootScope, $timeout, services, profPath) {
    $scope.Account = $scope.Profile;
    $scope.P = {};

    $scope.savePassword = function () {
        if (confirm('update password?') === false)
            return;
        $scope.saving = true;
        $http.post(profPath + '/change-password', $scope.P).success(function (resp) {
            $scope.saving = false;
            if (resp.code === 0) {
                $scope.pwordalerts = [{class: 'alert-success', msg: 'password changed successfully!'}];
            } else {
                $scope.pwordalerts = [{class: 'alert-danger', msg: resp.msg}];
            }
        }).error(function (data, status) {
            $scope.saving = false;
            if (status === 407) {
                $rootScope.sessionTimeout();
                $rootScope.route('login');
            } else
                services.notify("Service unavailable");
        });
    };

});

ctrls.controller('emailCtrl', function ($scope, $http, $rootScope, services, profPath) {
    $scope.saveEmail = true;

    if ($scope.Profile.hasPwd === false) {
        $scope.emailalerts = [{class: 'alert-info', msg: 'Your account does not have a password. Create a password in password settings to continue'}];
    }
    $scope.sendPasscode = function () {
        $scope.validating = true;
        $http.get(profPath + '/validate-email?p=' + $scope.newEmail).success(function (resp) {
            $scope.validating = false;
            if (resp.code === 0) {
                $scope.saveEmail = false;
            } else {
                $scope.emailalerts = [{class: 'alert-danger', msg: resp.msg}];
            }

        }).error(function (data, status) {
            $scope.validating = false;
            if (status === 407) {
                $rootScope.sessionTimeout();
                $rootScope.route('login');
            } else
                services.notify("Service unavailable");
        });
    };

    $scope.updateEmail = function () {
        $scope.saving = true;
        $http.post(profPath + '/change-email', {pcode: $scope.passcode, pword: $scope.password}).success(function (resp) {
            $scope.saving = false;
            if (resp.code === 0) {
                $scope.emailalerts = [{class: 'alert-success', msg: "Email updated successfully"}];
                $scope.saveEmail = true;
            } else {
                $scope.emailalerts = [{class: 'alert-danger', msg: resp.msg}];
            }

        }).error(function (data, status) {
            $scope.saving = false;
            if (status === 407) {
                $rootScope.sessionTimeout();
                $rootScope.route('login');
            } else
                services.notify("Service unavailable");
        });
    };

});