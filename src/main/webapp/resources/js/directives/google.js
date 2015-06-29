/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


'use strict';

/*
 * angular-google-plus-directive v0.0.1
 * ♡ CopyHeart 2013 by Jerad Bitner http://jeradbitner.com
 * Copying is an act of love. Please copy.
 */

angular.module('google', [])
        .factory('gapiService', function ($q, $window) {
            return {
                myDetails: function () {
                    var deferred = $q.defer();
                    $window.auth2.then(function (response) {
                        if (!response || response.error) {
                            deferred.reject('Error occured');
                        } else {
                            deferred.resolve(response);
                        }
                    }, function () {
                        console.log('error occurred loading google user');
                    });
                    
                    return deferred.promise;
                },
                login: function () {
                    var deferred = $q.defer();
                    $window.auth2.signIn().then(function (response) {
                        console.log(response.getBasicProfile().getName());
                        console.log(response.getBasicProfile().getEmail());
                        if (!response || response.error) {
                            deferred.reject('Error occured');
                        } else {
                            deferred.resolve(response);
                        }
                    });
                    return deferred.promise;
                },
                checkLoginState: function () {
                    return $window.auth2.isSignedIn.get();
                },
                logout: function () {
//                    var deferred = $q.defer();
//                    $window.auth2.signOut.then(function (response) {
//                        if (!response || response.error) {
//                            deferred.reject('Error occured');
//                        } else {
//                            deferred.resolve(response);
//                        }
//                    }, function () {
//                        console.log('error occurred logging out google user');
//                    });
//                    
                    return $window.auth2.signOut();
                }
            };
        }).
        run(['$window', '$rootScope', function ($window, $rootScope) {
                var po = document.createElement('script');
                po.type = 'text/javascript';
                po.async = true;
//                po.src = 'https://apis.google.com/js/client:plusone.js';
                po.src = 'https://apis.google.com/js/platform.js?onload=ggloaded';
                var s = document.getElementsByTagName('script')[0];
                s.parentNode.insertBefore(po, s);


                $window.ggloaded = function () {
//                    var auth2;
//                    console.log('loaded platform.js script');
                    gapi.load('auth2', function () {
                        $window.auth2 = gapi.auth2.init({
                            client_id: '271887426001-bj08flb6se3s08hg10a9cpgmu6l8pgvc.apps.googleusercontent.com',
                            scope: 'profile email'
                        });
                    });
                };
            }]);