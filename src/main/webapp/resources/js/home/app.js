/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var app = angular.module('AppHome', ['ui.router', 'ngAnimate', 'remoteValidation', 'InputMatch', 'ui.bootstrap', 'controllers', 'Scribbles', 'angularFileUpload', 'scFilters', 'facebook', 'google']);
app.constant("usersPath", "users");
app.constant("regPath", "registration");
app.constant("spinner", "resources/images/spinner.gif");
app.constant("imagePath", "posts/img");
app.constant("postsPath", "posts");
app.constant("profPath", "profile");
app.constant("searchPath", "search");

var ctrls = angular.module('controllers', []);
//app.controller('headerCtrl',headerCtrl);

app.config(function ($stateProvider, $urlRouterProvider) {
    $stateProvider.
            state('login', {
                url: '/login',
                templateUrl: 'static/reg/login.html',
                controller: 'loginCtrl',
                data: {displayName: ''}
            }).
            state('signup', {
                url: '/signup',
                templateUrl: 'static/reg/signup.html',
                controller: 'loginCtrl',
                data: {displayName: ''}
            }).
            state('signup-complete', {
                templateUrl: 'static/reg/reg-complete.html',
                controller: 'signupCtrl',
                data: {displayName: ''}
            }).
            state('forgot-password', {
                url: '/forgot-password',
                templateUrl: 'static/reg/forgot-password.html',
                controller: 'fpasswordCtrl',
                data: {displayName: ''}
            }).
            state('home', {
                url: '/home',
                templateUrl: 'static/home.html',
                controller: 'mainCtrl',
                data: {displayName: 'home'}
            }).
            state('home.jokes', {
                url: '/jokes?id&access_token&code',
                templateUrl: 'static/posts/jokes.html',
                controller: 'jokesCtrl',
                data: {displayName: 'Jokes'}
            }).
            state('home.proverbs', {
                url: '/proverbs?id',
                templateUrl: 'static/posts/proverbs.html',
                controller: 'provCtrl',
                data: {displayName: 'Proverbs'}
            }).
            state('home.quotes', {
                url: '/quotes?id',
                templateUrl: 'static/posts/quotes.html',
                controller: 'quotesCtrl',
                data: {displayName: 'Quotes'}
            }).
            state('profile', {
                url: '/profile/{user}',
                templateUrl: 'static/profile/profile.html',
                controller: 'profileCtrl',
                data: {displayName: 'profile'}
            }).
            state('profile.activity', {
//                url: '/profile/activity',
                templateUrl: 'static/profile/activity.html',
                controller: 'activityCtrl',
                data: {displayName: 'profile activity'}
            }).
            state('profile.account', {
                templateUrl: 'static/profile/account.html',
                controller: 'accountCtrl',
                data: {displayName: 'profile account'}
            }).
            state('profile.pword', {
                templateUrl: 'static/profile/password.html',
                controller: 'pwordCtrl',
                data: {displayName: 'change password'}
            }).
            state('profile.email', {
                templateUrl: 'static/profile/email.html',
                controller: 'emailCtrl',
                data: {displayName: 'change email'}
            }).
            state('search', {
                url: '/search?q',
                templateUrl: 'static/search.html',
                controller: 'searchCtrl',
                data: {displayName: ''}
            }).
            state('activate', {
                url: '/activate?t',
                templateUrl: 'static/reg/activation.html',
                controller: 'actvCtrl',
                data: {displayName: ''}
            }).
            state('inactive', {
                url: '/inactive?email',
                templateUrl: 'static/reg/inactive.html',
                controller: 'inactvCtrl',
                data: {displayName: ''}
            });

//    $urlRouterProvider.when('/profile','/home/jokes');
    $urlRouterProvider.otherwise('/home/jokes');

//      $locationProvider.html5Mode(true);
});

app.run(function ($rootScope, $state, $window, imagePath, $http, usersPath, spinner, services,postsPath) {
//$rootScope.testvar = 300;
    $rootScope.endpoint = 'http://www.xcribbles.com';
    $rootScope.usersPath = usersPath;
    $rootScope.spinner = spinner;
    $rootScope.imagePath = imagePath;

    $rootScope.navigate = function (state, params) {
        $state.go(state, params, {location: false});
    };
    $rootScope.route = function (state, params) {
        $state.go(state, params);
    };
    $rootScope.back = function () {
        $window.history.back();
    };

    $rootScope.alerts = [];
    $rootScope.$on('$stateChangeStart', function (e, to) {
        $rootScope.loadingMsg = 'Loading ' + to.data.displayName + '...';
        $rootScope.previous = $state.current.name;
//        console.log();

    });
    $rootScope.$on('$stateChangeSuccess', function (e, to) {
        $rootScope.loadingMsg = undefined;
        $rootScope.activePage = to.name;
    });

    $rootScope.getCurrentUser = function () {
        $http.get(usersPath + '/current').success(function (resp) {
            if (resp.code === 0) {
                $rootScope.userDetails = resp;
                $rootScope.isAuthenticated = true;
            } else {
                $rootScope.isAuthenticated = false;
            }
        });
    };

    $rootScope.getCurrentUser();

    $rootScope.profilePixChanged = function (pic) {
        $rootScope.$broadcast('profile.pix.changed', pic);
    };
    $rootScope.sessionTimeout = function () {
        $rootScope.isAuthenticated = false;
        $rootScope.$broadcast('session.timeout');
        services.notify("Login to continue");
//        $rootScope.route('login');
    };
    $rootScope.loggedOut = function () {
        $rootScope.isAuthenticated = false;
        $rootScope.$broadcast('logged.out');
    };
    
    $http.get(postsPath + '/tags').success(function (resp) {
        $rootScope.tagcloud = resp;
    });
});

console.log("angular configured");

//  $rootScope.$on("$routeChangeSuccess", function(currentRoute, previousRoute){
//    //Change page title, based on Route information
//    console.log("$route.current.title");
////    $rootScope.title = $route.current.title;
//  });
