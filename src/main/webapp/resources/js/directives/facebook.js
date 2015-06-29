/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
'use strict';

angular.module('facebook', [])
        .factory('facebookService', function ($q) {
            return {
                getMyLastName: function () {
                    var deferred = $q.defer();
                    FB.api('/me', {
                        fields: 'last_name'
                    }, function (response) {
                        if (!response || response.error) {
                            deferred.reject('Error occured');
                        } else {
                            deferred.resolve(response);
                        }
                    });
                    return deferred.promise;
                },
                login: function () {
                    var deferred = $q.defer();
                    FB.login(function (response) {
                        if (!response || response.error) {
                            deferred.reject('Error occured');
                        } else {
                            deferred.resolve(response);
                        }
                    }, {scope: 'public_profile,email,user_friends'});
                    return deferred.promise;
                },
                checkLoginState: function () {
                    var deferred = $q.defer();
                    FB.getLoginStatus(function (response) {
                        if (!response || response.error) {
                            deferred.reject('Error occured');
                        } else {
                            deferred.resolve(response);
                        }
                    });
                    return deferred.promise;
                },
                picture: function () {
                    var deferred = $q.defer();
                    FB.api(
                            "/me/picture?height=200&width=200",
                            function (response) {
                                if (response && !response.error) {
                                    deferred.resolve(response);
                                } else {
                                    deferred.reject('Error occured');
                                }
                            }
                    );
                    return deferred.promise;
                },
                logout: function () {
                    var deferred = $q.defer();
                    FB.logout(function (response) {
                        deferred.resolve(response);
                    });
                    return deferred.promise;
                }
            };
        }).
        run(['$window', '$rootScope', function ($window, $rootScope) {
                $window.fbAsyncInit = function () {
                    FB.init({
                        appId: '920675767993594',
                        cookie: true, // enable cookies to allow the server to access 
                        // the session
                        xfbml: true, // parse social plugins on this page
                        version: 'v2.2', // use version 2.2
                        channelUrl: 'static/channel.html'
                    });
                };
                (function (d, s, id) {
                    var js, fjs = d.getElementsByTagName(s)[0];
                    if (d.getElementById(id)) {
                        return;
                    }
                    js = d.createElement(s);
                    js.id = id;
//                    js.src = "//connect.facebook.net/en_US/sdk.js";
                    js.src = "http://connect.facebook.net/en_US/sdk.js";
                    fjs.parentNode.insertBefore(js, fjs);
                }(document, 'script', 'facebook-jssdk'));
            }]);
