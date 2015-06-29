/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('headerCtrl', function ($scope, $http, $timeout, $rootScope, searchPath, $q,facebookService,gapiService) {
    $scope.fullname = 'dubic uzuegbu';
    $scope.pop = false;
    $scope.insearch = false;


    $scope.testDialog = function () {
        $scope.insearch = false;
    };

    $scope.logout = function () {
        $http.get('logout').success(function () {
            $rootScope.getCurrentUser();
            gapiService.logout();
            $timeout(function(){
                facebookService.logout.then(function(response){
                   console.log('User is now logged out of facebook'); 
                   console.log(response); 
                });
            },10000);
        });
    };

    $scope.search = function () {
        if (angular.isUndefined($scope.Search.keyword) || $scope.Search.keyword === '')
            return;
        $scope.searching = true;
        $('.pop').slideDown();
        $timeout(function () {

            var calls = [];
            calls.push($scope.searchUsers());
            calls.push($scope.searchJokes());
            calls.push($scope.searchProverbs());
            calls.push($scope.searchQuotes());
            $q.all($scope.searchCalls).then(function () {
                $scope.searching = false;
            });
        }, 500);

    };

    $scope.searchCalls = [];

    $scope.$on('profile.pix.changed', function (evt, pic) {
//        $rootScope.userDetails.picture = pic;
        $rootScope.userDetails.picture = pic;
    });

//        $scope.$watch(function( ) {
//            return $scope.insearch;
//        }, function(value) {
//            console.log('select values = ' + value);
//
//        });

    $scope.searchUsers = function () {
        var r = $http.post(searchPath + '/results/users?q=' + $scope.Search.keyword + '&size=5');
        $scope.searchCalls.push(r);
        r.success(function (resp) {
            $scope.empty('Persons');
            $scope.Results.push({name: 'Persons', count: resp.userscount, lists: resp.users});
        });
        return r;
    };
    
    $scope.searchJokes = function () {
        var r = $http.post(searchPath + '/results/posts/JOKE?q=' + $scope.Search.keyword + '&size=5');
        $scope.searchCalls.push(r);
        r.success(function (resp) {
            $scope.empty('Jokes');
            $scope.Results.push({name: 'Jokes', count: resp.count, lists: resp.posts});
        });
        return r;
    };
    $scope.searchProverbs = function () {
        var r = $http.post(searchPath + '/results/posts/PROVERB?q=' + $scope.Search.keyword + '&size=5');
        $scope.searchCalls.push(r);
        r.success(function (resp) {
            $scope.empty('Proverbs');
            $scope.Results.push({name: 'Proverbs', count: resp.count, lists: resp.posts});
        });
        return r;
    };
    $scope.searchQuotes = function () {
        var r = $http.post(searchPath + '/results/posts/QUOTE?q=' + $scope.Search.keyword + '&size=5');
        $scope.searchCalls.push(r);
        r.success(function (resp) {
            $scope.empty('Quotes');
            $scope.Results.push({name: 'Quotes', count: resp.count, lists: resp.posts});
        });
        return r;
    };
    $scope.Results = [
//        {
//            name: 'Jokes',
//            count: 28,
//            lists: [{id: 1, text: 'welcome to nigeria 99'}, {id: 2, text: 'the end of all things is at hand ghghguhiui vnghgyyyg bftydrersrs jhggyttyfyg bhghgygyufz tvuyuyuyh jjiuiui'}]
//        },
//        {
//            name: 'Proverbs',
//            count: 7,
//            lists: [{id: 1, text: 'Oh bother'}, {id: 2, text: 'why do yiu disturb me'}]
//        },
//        {
//            name: 'Persons',
//            count: 12,
//            lists: [{id: 'perry', text: 'perry'}, {id: 'dubic', text: 'dubic'}, {id: 'jerry', text: 'jerry'}]
//        }
    ];

    $scope.empty = function (name) {
        for (var i = 0; i < $scope.Results.length; i++)
            for (var key in $scope.Results[i]) {
                if (key === 'name')
                    if ($scope.Results[i][key] === name)
                        $scope.Results.splice(i, 1);
            }
    };
    
    $scope.runSearch = function () {
        $(".pop").slideUp();
        $rootScope.route('search', {q: $scope.Search.keyword});
    };
});