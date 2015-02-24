/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var app = angular.module('AppHome', ['ui.router', 'ngAnimate', 'remoteValidation', 'InputMatch', 'ui.bootstrap', 'controllers', 'Scribbles']);
app.constant("usersPath", "/scribbleit/users");
app.constant("regPath", "/scribbleit/registration");
app.constant("spinner", "/scribbleit/resources/images/spinner.gif");
app.constant("imagePath", "/scribbleit/posts/img");
app.constant("postsPath", "/scribbleit/posts");

var ctrls = angular.module('controllers', []);
//app.controller('headerCtrl',headerCtrl);

app.config(function($stateProvider, $urlRouterProvider) {
    $stateProvider.
            state('login', {
                url: '/login',
                templateUrl: '/scribbleit/registration/load?p=login',
                controller: 'loginCtrl',
//                resolve: {
//                    permission: function() {
//                        console.log('resloving...');
//                         return authorizationService.permissionCheck([roles.superUser]);
//                     }
//                },
                data: {displayName: ''}
            }).
            state('signup', {
                url: '/signup',
                templateUrl: '/scribbleit/registration/load?p=signup',
                controller: 'signupCtrl',
                data: {displayName: ''}
            }).
            state('signup-complete', {
                template: '<div class="login" style="min-width: 40%;margin-top: 42px;">'
                        + '<aside class="content">'
                        + '<div class="well well-sm">your account has been created. An email will be sent to you shortly to activate your account</div>'
                        + '</aside>'
                        + '</div>',
                controller: 'signupCtrl',
                data: {displayName: ''}
            }).
            state('forgot-password', {
                url: '/forgot-password',
                templateUrl: '/scribbleit/registration/load?p=forgot-password',
                controller: 'fpasswordCtrl',
                data: {displayName: ''}
            }).
            state('home', {
                url: '/home',
                templateUrl: '/scribbleit/home',
                controller: 'mainCtrl',
                data: {displayName: 'home'}
            }).
            state('home.jokes', {
                url: '/jokes',
                templateUrl: '/scribbleit/posts/view?page=jokes',
                controller: 'jokesCtrl',
                data: {displayName: 'Jokes'}
            }).
            state('home.proverbs', {
                url: '/proverbs',
                templateUrl: '/scribbleit/posts/view?page=proverbs',
                controller: 'provCtrl',
                data: {displayName: 'Proverbs'}
            }).
            state('home.quotes', {
                url: '/quotes',
                templateUrl: '/scribbleit/posts/view?page=quotes',
                controller: 'quotesCtrl',
                data: {displayName: 'Quotes'}
            }).
            state('profile', {
                url: '/profile/{user}',
                templateUrl: '/scribbleit/profile/home',
                controller: 'profileCtrl',
                data: {displayName: 'profile'}
            }).
            state('profile.activity', {
//                url: '/profile/activity',
                templateUrl: '/scribbleit/profile/activity',
                controller: 'activityCtrl',
                data: {displayName: 'profile activity'}
            }).
            state('profile.account', {
                templateUrl: '/scribbleit/profile/account',
                controller: 'accountCtrl',
                data: {displayName: 'profile account'}
            }).
            state('profile.pword', {
                templateUrl: '/scribbleit/profile/pword',
                controller: 'pwordCtrl',
                data: {displayName: 'change password'}
            });

//    $urlRouterProvider.when('/profile','/home/jokes');
    $urlRouterProvider.otherwise('/home/jokes');

//      $locationProvider.html5Mode(true);
});

app.run(function($rootScope, $state, $window, imagePath, $http, usersPath) {

    $rootScope.imagePath = imagePath;

    $rootScope.navigate = function(state, params) {
        $state.go(state, params, {location: false});
    };
    $rootScope.route = function(state) {
        $state.go(state);
    };
    $rootScope.back = function() {
        $window.history.back();
    };

    $rootScope.alerts = [];
    $rootScope.$on('$stateChangeStart', function(e, to) {
        $rootScope.loading = true;
        $rootScope.loadingMsg = 'Loading ' + to.data.displayName + '...';
        $rootScope.previous = $state.current.name;
//        console.log();

    });
    $rootScope.$on('$stateChangeSuccess', function(e, to) {
        $rootScope.loading = false;
        $rootScope.activePage = to.name;
    });

    $rootScope.getCurrentUser = function() {
        $http.get(usersPath + '/current').success(function(resp) {
            if (resp.code === 0) {
                $rootScope.userDetails = resp;
                $rootScope.isAuthenticated = true;
            }else{
                $rootScope.isAuthenticated = false;
            }
        });
    };
    
    $rootScope.getCurrentUser();
});

console.log("angular configured");

//  $rootScope.$on("$routeChangeSuccess", function(currentRoute, previousRoute){
//    //Change page title, based on Route information
//    console.log("$route.current.title");
////    $rootScope.title = $route.current.title;
//  });
