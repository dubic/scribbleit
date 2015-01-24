/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var app = angular.module('AppHome', ['ui.router', 'ngAnimate', 'remoteValidation', 'InputMatch', 'ui.bootstrap', 'controllers', 'Scribbles']);
app.constant("usersPath", "/selfcare/users/");
app.constant("spinner", "/scribbleit/resources/images/spinner.gif");
app.constant("imagePath", "/scribbleit/posts/img/");

var ctrls = angular.module('controllers', []);
//app.controller('headerCtrl',headerCtrl);

app.config(function($stateProvider, $urlRouterProvider) {
    $stateProvider.
            state('login', {
                url: '/login',
                templateUrl: 'login-view',
                controller: 'loginCtrl',
                data:{displayName:''}
            }).
            state('signup', {
                url: '/signup',
                templateUrl: 'signup-view',
                controller: 'signupCtrl',
                data:{displayName:''}
            }).
            state('forgot-password', {
                url: '/forgot-password',
                templateUrl: 'forgot-password-view',
                controller: 'fpasswordCtrl',
                data:{displayName:''}
            }).
            state('home', {
                url: '/home',
                templateUrl: '/scribbleit/home',
                controller: 'mainCtrl',
                data:{displayName:'home'}
            }).
            state('home.jokes', {
                url: '/jokes',
                templateUrl: '/scribbleit/posts/jokes/load',
                controller: 'jokesCtrl',
                data:{displayName:'Jokes'}
            }).            
            state('profile', {
                url: '/profile/{user}',
                templateUrl: '/scribbleit/profile/home',
                controller: 'profileCtrl',
                data:{displayName:'profile'}
            }).
            state('profile.activity', {
//                url: '/profile/activity',
                templateUrl: '/scribbleit/profile/activity',
                controller: 'activityCtrl',
                data:{displayName:'profile activity'}
            }).
            state('profile.account', {
//                url: '/profile/activity',
                templateUrl: '/scribbleit/profile/account',
                controller: 'accountCtrl',
                data:{displayName:'profile account'}
            }).
            state('profile.pword', {
//                url: '/profile/activity',
                templateUrl: '/scribbleit/profile/pword',
                controller: 'pwordCtrl',
                data:{displayName:'change password'}
            });

//    $urlRouterProvider.when('/profile','/home/jokes');
    $urlRouterProvider.otherwise('/home/jokes');

//      $locationProvider.html5Mode(true);
});

app.run(function($rootScope, $state, $window) {
    

    $rootScope.transition = function(state) {
        $state.transitionTo(state);
    };
    $rootScope.navigate = function(state,params) {
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
        $rootScope.loadingMsg = 'Loading '+to.data.displayName+'...';
        console.log('name - ' + to.name);
    });
    $rootScope.$on('$stateChangeSuccess', function(e, to) {
        $rootScope.loading = false;
    });
});

console.log("angular configured");

//  $rootScope.$on("$routeChangeSuccess", function(currentRoute, previousRoute){
//    //Change page title, based on Route information
//    console.log("$route.current.title");
////    $rootScope.title = $route.current.title;
//  });
