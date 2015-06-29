/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('loginCtrl', function ($scope, $http, $rootScope, services, regPath, facebookService, gapiService, $timeout) {
    //TEST

//    $timeout(function () {
//        services.openModal('socAcctModal');   
//        $timeout(function () {
//            services.closeModal('socAcctModal');
//            $scope.loginSuccess();
//        }, 3000);
//    }, 5000);

    /////
    $scope.fblogin = function () {
        facebookService.checkLoginState().then(function (response) {
            console.log('fb checkLoginState response...');
            console.log(response);
            if (response.status === 'connected') {
                // Logged into your app and Facebook.
                $rootScope.accessToken = response.authResponse.accessToken;
                $scope.sendFBtoken(response.authResponse.accessToken);
            } else if (response.status === 'not_authorized') {
                // The person is logged into Facebook, but not your app.
                facebookService.login().then(function (response) {
                    $rootScope.accessToken = response.authResponse.accessToken;
                    $scope.sendFBtoken(response.authResponse.accessToken);
                });
            } else {
                // The person is not logged into Facebook, so we're not sure if
                facebookService.login().then(function (response) {
                    if (response.status === 'connected') {
                        $rootScope.accessToken = response.authResponse.accessToken;
                        $scope.sendFBtoken(response.authResponse.accessToken);
                    }
                });

            }
        });
    };

    $scope.sendFBtoken = function (t) {
        $scope.soc = {};
        $scope.sl = {};
        $http.get(regPath + '/facebook/token?t=' + t).success(function (resp) {
            $scope.loading = false;
            if (resp.code === 200) {
                facebookService.picture().then(function (response) {
//                    console.log(response);
                    $scope.soc.pix = response.data.url;
//                        console.log('picture resp...'+$scope.soc.pix);
                });
                $scope.soc.username = resp.name;
                $scope.soc = resp;

                $scope.socAlerts = [];
                services.openModal('socAcctModal');
            } else if (resp.code === 201) {
                $scope.loginSuccess();
            }
            else if (resp.code === 202) {
                $scope.sl = resp;
                $scope.loginSuccess();
            }
        });
    };

    $scope.sendGGtoken = function (t) {
        $scope.soc = {};
        $scope.sl = {};
        $http.get(regPath + '/google/token?t=' + t).success(function (resp) {
            $scope.loading = false;
            if (resp.code === 200) {

                $scope.soc.username = resp.name;
                $scope.soc = resp;
                $scope.socAlerts = [];
                services.openModal('socAcctModal');
            } else if (resp.code === 201) {
                $scope.loginSuccess();
            }
            else if (resp.code === 202) {
                $scope.sl = resp;
                services.openModal('linkAcctModal');
//                $scope.loginSuccess();
            }
        });
    };

    $scope.gglogin = function () {
        if (gapiService.checkLoginState()) {
            console.log('already logged in to google');
            gapiService.myDetails()
                    .then(function (response) {
//                        console.log(response.currentUser);
//                        console.log(response.currentUser.B.B.id_token);
                        $scope.sendGGtoken(response.currentUser.B.B.access_token);
                    });
        } else {
            gapiService.login().then(function (response) {
                console.log(response.getBasicProfile());
                console.log(response.getAuthResponse());
                $scope.sendGGtoken(response.getAuthResponse().access_token);
            });
        }
    };

    $scope.gglogout = function () {
        gapiService.logout().then(function (response) {
            console.log('logged out from google');
        });
    };

    $scope.createSocialAccount = function () {
        $scope.creating = true;
        $http.post(regPath + '/social/create?username=' + $scope.soc.username + '&type=' + $scope.soc.account_type).success(function (resp) {
            $scope.creating = false;
            if (resp.code === 200) {
                services.closeModal('socAcctModal');
                $scope.loginSuccess();
            } else {
                $scope.socAlerts = [{class: 'alert-danger', msg: resp.msg}];
            }
        });
    };

    $scope.linkAccount = function () {
        $scope.linking = true;
        $http.post(regPath + '/social/link?password=' + $scope.sl.password + '&type=' + $scope.sl.account_type).success(function (resp) {
            $scope.linking = false;
            if (resp.code === 200) {
                services.closeModal('linkAcctModal');
                $scope.loginSuccess();
            } else {
                $scope.slAlerts = [{class: 'alert-danger', msg: resp.msg}];
            }
        });
    };


    $scope.login = function () {
        $scope.loading = true;
        $http.post('login?' + $.param($scope.user)).success(function (resp) {
            $scope.loading = false;
            $scope.loginSuccess();
        }).error(function (resp, e) {
            console.log(e);
            services.isAuthenticated = false;
            if (e === 404)
                $scope.alerts = services.buildAlerts([{code: e, msg: 'email or password wrong'}]);
            else if (e === 501)
                $scope.alerts = services.buildAlerts([{code: e, msg: 'your account has not been activated'}]);
        });
    };

    $scope.loginSuccess = function () {
//            $rootScope.isAuthenticated = true;
        if (angular.isUndefined($rootScope.previous) || $rootScope.previous === '') {
            $rootScope.route('home.jokes');
//                else $rootScope.route($rootScope.previous);
//                $rootScope.route('home.jokes');
        }
        else {
            console.log($rootScope.previous);
            if (['login', 'signup', 'signup-complete', 'forgot-password', 'home','profile'].indexOf($rootScope.previous) !== -1) {
                $rootScope.route('home.jokes');
            }
            else
                $rootScope.route($rootScope.previous);
        }
        $rootScope.getCurrentUser();
    };
})

        .controller('signupCtrl', function ($scope, $http, regPath, services, $rootScope) {
            $scope.signup = function () {
                $rootScope.navigate('signup-complete');
//            console.log($scope.user);
//                $scope.loading = true;
//                $http.post(regPath + '/signup', $scope.user).success(function(resp) {
//                    $scope.alerts = services.buildAlerts([{code: resp.code, msg: resp.msg}]);
//                    $scope.loading = false;
//                    if (resp.code === 0) {
//                        $rootScope.navigate('signup-complete');
//                    }
//                });
            };
        })

        .controller('fpasswordCtrl', function ($scope, $http, usersPath, services, regPath) {
            $scope.isCollapsed = true;
            $scope.loading = false;

            $scope.forgetToken = function () {
                $scope.loading = true;
                $http.get(regPath + '/password/forgot?email=' + $scope.User.email).success(function (resp) {
                    $scope.alerts = services.buildAlerts([{code: resp.code, msg: resp.msg}]);
                    $scope.loading = false;

                    if (resp.code === 0) {
                        $scope.isCollapsed = false;
                    }
                });
            };

            $scope.resetPassword = function () {
                if (confirm('reset password?') === false)
                    return;
                $scope.loading = true;
                $http.post(regPath + '/password/reset', $scope.User).success(function (resp) {
                    $scope.loading = false;
                    $scope.alerts = services.buildAlerts([{code: resp.code, msg: resp.msg}]);
                    if (resp.code === 0)
                        $scope.isCollapsed = true;
                });
            };
        });