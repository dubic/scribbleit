/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('lgmodalCtrl', function ($scope, $http, $rootScope, services, regPath, facebookService, gapiService, $window) {
    //TEST
    /////
    $scope.$on('login.modal.shown', function () {
        $scope.loginAlerts = [];
        $scope.user = {};
    });

    $scope.forgotPassword = function () {
        services.closeModal('loginModal');
        $rootScope.route('forgot-password');
    };
    $scope.toSignup = function () {
        services.closeModal('loginModal');
        $rootScope.route('signup');
    };

    $scope.fblogin = function () {
        services.showMsg('Facebook Authentication...');
        facebookService.checkLoginState().then(function (response) {
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
                    $scope.soc.pix = response.data.url;
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
                services.closeModal('loginModal');
                services.openModal('linkAcctModal');
            }else if (resp.code === 190) {
                services.notify('Facebook session expired. Refresh page and try again');
            }else {
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
            }else {
                services.notify('Unexpected error occurred!');
            }
        }).error(function (data, status) {
            services.hideMsg();
            services.notify('Error occurred.Refresh and try again');
        });
    };

    $scope.gglogin = function () {
        services.showMsg('Google Authentication...');
//        if (gapiService.checkLoginState()) {
//            console.log('already logged in to google');
//            gapiService.myDetails()
//                    .then(function (response) {
//                        console.log(response);
//                        $scope.sendGGtoken(response.getAuthResponse().access_token);
//                    });
//        } else {
            
            gapiService.login().then(function (response) {
                services.hideMsg();
                $scope.sendGGtoken(response.getAuthResponse().access_token);
            });
//        }
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
                $scope.loginAlerts = services.buildAlerts([{code: e, msg: 'email or password wrong'}]);
            else if (e === 501)
                $rootScope.navigate('inactive', {email: $scope.user.email});
        });
    };

    $scope.loginSuccess = function () {
        $rootScope.getCurrentUser();
        services.closeModal('loginModal');
        services.notify('Login sucessful');
    };

});