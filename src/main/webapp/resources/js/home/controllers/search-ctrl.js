/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('searchCtrl', function ($scope, $http, $rootScope, searchPath, $stateParams) {

    console.log($stateParams);
    $scope.keyword = $stateParams.q;
    //FOR JOKES
    $scope.jokesize = 5;
    $scope.jokepage = 1;
    $scope.searchJokes = function () {
        $scope.jokesearching = true;
        $http.post(searchPath + '/results/posts/JOKE?q=' + $stateParams.q + '&size=' + $scope.jokesize + '&start=' + ($scope.jokepage - 1) * $scope.jokesize).success(function (resp) {
            $scope.jokesearching = false;
            $scope.jokecount = resp.count;
            $scope.jokes = resp.posts;
        });
    };
    //FOR Proverbs
    $scope.provsize = 5;
    $scope.provpage = 1;
    $scope.searchProverbs = function () {
        $scope.provsearching = true;
        $http.post(searchPath + '/results/posts/PROVERB?q=' + $stateParams.q + '&size=' + $scope.provsize + '&start=' + ($scope.provpage - 1) * $scope.provsize).success(function (resp) {
            $scope.provsearching = false;
            $scope.provcount = resp.count;
            $scope.proverbs = resp.posts;
        });
    };
    //FOR Quotes
    $scope.quotesize = 5;
    $scope.quotepage = 1;
    $scope.searchQuotes = function () {
        $scope.qsearching = true;
        $http.post(searchPath + '/results/posts/QUOTE?q=' + $stateParams.q + '&size=' + $scope.quotesize + '&start=' + ($scope.quotepage - 1) * $scope.quotesize).success(function (resp) {
            $scope.qsearching = false;
            $scope.quotecount = resp.count;
            $scope.quotes = resp.posts;
        });
    };


    $scope.userssize = 5;
    $scope.userspage = 1;
    $scope.searchUsers = function () {
        $http.post(searchPath + '/results/users?q=' + $stateParams.q + '&size=' + $scope.userssize + '&start=' + ($scope.userspage - 1) * $scope.userssize).success(function (resp) {
            $scope.searching = false;
            $scope.userscount = resp.userscount;
            $scope.persons = resp.users;
        });
    };

    $scope.searchJokes();
    $scope.searchUsers();
    $scope.searchProverbs();
    $scope.searchQuotes();
//    $scope.jokeChanged=function(){
//        
//    };
//    $scope.usersChanged=function(){
//        
//    };
});

