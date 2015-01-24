/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ctrls.controller('headerCtrl', ['$scope', '$http','$timeout', function($scope, $http,$timeout) {
        $scope.fullname = 'dubic uzuegbu';
        $scope.pop = false;
        $scope.testDialog = function() {
            $scope.dopen = true;
        };

        $scope.search = function() {
            $scope.searching = true;
            $timeout(function(){
                $scope.searching = false;
                $scope.searched = $scope.groups;
            },1000);
        };

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
    }]);