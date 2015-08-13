/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('lgmodalCtrl', function ($scope, $http, $rootScope, services, regPath, facebookService, gapiService, $window) {
    //TEST
    /////
    alert('seen');
    $scope.fblogin = function () {
        alert('seen');
        return;
        services.showMsg('Facebook Authentication...');
        facebookService.checkLoginState().then(function (response) {
            console.log('fb checkLoginState response...');
            console.log(response);
            if (response.status === 'connected') {
                // Logged into your app and Facebook.
                $rootScope.accessToken = response.authResponse.accessToken;
                $scope.sendFBtoken(response.authResponse.accessToken);
            } else if (response.status === 'not_authorized') {
                services.hideMsg();
                // The person is logged into Facebook, but not your app.
                facebookService.login().then(function (response) {
                    $rootScope.accessToken = response.authResponse.accessToken;
                    $scope.sendFBtoken(response.authResponse.accessToken);
                });
            } else {
                services.hideMsg();
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
        services.showMsg('Loading Facebook account details...');
        $scope.soc = {};
        $scope.sl = {};
        $http.get(regPath + '/facebook/token?t=' + $window.encodeURIComponent(t)).success(function (resp) {
            services.hideMsg();
            if (resp.code === 200) {
                facebookService.picture().then(function (response) {
//                    console.log(response);
                    $scope.soc.pix = response.data.url;
//                        console.log('picture resp...'+$scope.soc.pix);
                });
                $scope.soc.username = resp.name;
                $scope.soc = resp;

                $scope.socAlerts = [];
                services.closeModal('loginModal');
                services.openModal('socAcctModal');
            } else if (resp.code === 201) {
                $scope.loginSuccess();
            }
            else if (resp.code === 202) {
                $scope.sl = resp;
                console.log('before closed');
                services.closeModal('loginModal');
                alert('closed');
                services.openModal('linkAcctModal');
                alert('opened');
            } else {
                services.notify('Unexpected error occurred!');
            }
        }).error(function (data, status) {
            services.hideMsg();
            services.notify('Error occurred.Refresh and try again');
        });
    };

    $scope.sendGGtoken = function (t) {
        services.showMsg('Loading Google account details...');
        $scope.soc = {};
        $scope.sl = {};
        console.log(regPath + '/google/token?t=' + escape(t));
        $http.get(regPath + '/google/token?t=' + escape(t)).success(function (resp) {
            services.hideMsg();
            if (resp.code === 200) {

                $scope.soc.username = resp.name;
                $scope.soc = resp;
                $scope.socAlerts = [];
                services.closeModal('loginModal');
                services.openModal('socAcctModal');
            } else if (resp.code === 201) {
                $scope.loginSuccess();
            }
            else if (resp.code === 202) {
                $scope.sl = resp;
                services.closeModal('loginModal');
                services.openModal('linkAcctModal');
            } else {
                services.notify('Unexpected error occurred!');
            }
        }).error(function (data, status) {
            services.hideMsg();
            services.notify('Error occurred.Refresh and try again');
        });
    };

    $scope.gglogin = function () {
        services.showMsg('Google Authentication...');
        if (gapiService.checkLoginState()) {
            console.log('already logged in to google');
            gapiService.myDetails()
                    .then(function (response) {
                        $scope.sendGGtoken(response.currentUser.B.B.access_token);
                    });
        } else {
            services.hideMsg();
            gapiService.login().then(function (response) {
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
        services.showMsg('Creating account...');
        $http.post(regPath + '/social/create?username=' + $scope.soc.username + '&type=' + $scope.soc.account_type).success(function (resp) {
            services.hideMsg();
            if (resp.code === 200) {
                services.closeModal('socAcctModal');
                $scope.loginSuccess();
            } else {
                $scope.socAlerts = [{class: 'alert-danger', msg: resp.msg}];
            }
        }).error(function (data, status) {
            services.hideMsg();
            services.notify('Error occurred.Refresh and try again');
        });
    };

    $scope.linkAccount = function () {
        services.showMsg('Linking account...');
        $http.post(regPath + '/social/link?password=' + $scope.sl.password + '&type=' + $scope.sl.account_type).success(function (resp) {
            services.hideMsg();
            if (resp.code === 200) {
                services.closeModal('linkAcctModal');
                $scope.loginSuccess();
            } else {
                $scope.slAlerts = [{class: 'alert-danger', msg: resp.msg}];
            }
        }).error(function (data, status) {
            services.hideMsg();
            services.notify('Error occurred.Refresh and try again');
        });
    };


    $scope.login = function () {
        services.showMsg('Signing in...');
        $http.post('login?' + $.param($scope.user)).success(function (resp) {
            services.hideMsg();
            $scope.loginSuccess();
        }).error(function (resp, e) {
            services.hideMsg();
            console.log(e);
            services.isAuthenticated = false;
            if (e === 404)
                $scope.alerts = services.buildAlerts([{code: e, msg: 'email or password wrong'}]);
            else if (e === 501)
                $rootScope.navigate('inactive', {email: $scope.user.email});
        });
    };

    $scope.loginSuccess = function () {
        services.closeModal('loginModal');
        $rootScope.getCurrentUser();
    };
})

        .controller('signupCtrl', function ($scope, $http, regPath, services, $rootScope) {
            $scope.signup = function () {
//                $rootScope.navigate('signup-complete');
//            console.log($scope.user);
                $scope.loading = true;
                $http.post(regPath + '/signup', $scope.user).success(function (resp) {
                    $scope.alerts = services.buildAlerts([{code: resp.code, msg: resp.msg}]);
                    $scope.loading = false;
                    if (resp.code === 0) {
                        $rootScope.navigate('signup-complete');
                    }
                });
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
        })
        .controller('actvCtrl', function ($scope, $http, services, regPath, $stateParams) {
            services.showMsg('Please Wait...');
//            console.log($stateParams);
            $http.get(regPath + '/activate?t=' + encodeURIComponent($stateParams.t)).success(function (resp) {
                $scope.msg = resp.msg;
                services.hideMsg();
            });
        })
        .controller('inactvCtrl', function ($scope, $http, services, regPath, $stateParams, $timeout, $rootScope) {
            $scope.email = $stateParams.email;

            $scope.resend = function () {
                $scope.resending = true;
//                $timeout(function(){
//                    $scope.resending = false;
//                },3000);
                $http.get(regPath + '/resend?email=' + $scope.email).success(function (resp) {
                    $scope.resending = false;
                    services.notify(resp.msg, 3000);
                    if (resp.code !== 500)
                        $rootScope.navigate('login');
                });
            };

        });