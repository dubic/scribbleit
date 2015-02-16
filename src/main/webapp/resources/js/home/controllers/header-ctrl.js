/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('headerCtrl', function($scope, $http, $timeout,$rootScope) {
        $scope.fullname = 'dubic uzuegbu';
        $scope.pop = false;
        $scope.insearch = false;
        

        $scope.testDialog = function() {
            $scope.insearch = false;
        };
        
        $scope.logout = function() {
            $http.get('logout').success(function(){
                $rootScope.getCurrentUser();
            });
        };

        $scope.search = function() {
            if(angular.isUndefined($scope.Search.keyword)) return;
            $scope.searching = true;
            $timeout(function() {
//                $scope.insearch = true;
                $('.pop').slideDown();
                $scope.searching = false;
                $scope.searched = $scope.groups;
            }, 1000);
        };

//        $scope.$watch(function( ) {
//            return $scope.insearch;
//        }, function(value) {
//            console.log('select values = ' + value);
//
//        });

        $scope.groups = [
            {
                name: 'Jokes',
                count: 28,
                lists: ['welcome to nigeria 99', 'the end of all things is at hand']
            },
            {
                name: 'Proverbs',
                count: 7,
                lists: ['Oh bother', 'why do yiu disturb me']
            }
        ];
    });