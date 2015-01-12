<%-- 
    Document   : angular
    Created on : Sep 13, 2014, 8:22:29 PM
    Author     : dubem
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="/scribbleit/assets/plugins/jquery-1.10.2.min.js"></script>
        <script src="/scribbleit/assets/plugins/bootstrap/js/bootstrap.min.js"></script>
        <script src="/scribbleit/resources/js/angular.min.js"></script>
        <link href="/scribbleit/assets/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
        <title>Angular test</title>
    </head>
    <body ng-app="App">
        <style>
            .row{
                margin: 5px;
            }
            .form-control{
                width: 300px;
            }
            input.ng-invalid{
                border: 1px solid red;
            }
        </style>
        <h3>Form Test</h3>
        <form name="testForm" ng-controller="TestController">
            <div class="row">
                <div></div>
                <div><input class="form-control" placeholder="screen name" name="screenName" type="text" ng-model="person.name" required/></div>
            </div>
            <div class="row">
                <div></div>
                <div><input class="form-control" name="email" placeholder="email" type="email" ng-model="person.email" required/></div>
            </div>
            <div class="row">
                <div></div>
                <div><input class="form-control" name="password" placeholder="password" type="password" ng-model="person.password"/></div>
            </div>
            <div class="row">
                <div><input name="save" class="btn btn-info" type="submit" ng-click="register()"/></div>
            </div>
        </form>

        <div ng-controller="CheckAllCtrl">
            <table class="table table-hover table-striped">
                <thead>
                    <tr>
                        <th>Name</th><th>Email</th><th><input type="checkbox" ng-model="checkAll"/></th>
                    </tr>
                </thead>
                <tbody> 
                    <tr ng-repeat="person in persons"><td>{{person.name}}</td><td>{{person.email}}</td><td><input type="checkbox" ng-model="person.checked" ng-checked="checkAll"/></td></tr>
                </tbody>
            </table>
        </div>
    </body>
    <script>
        angular.module('App', [])
                .controller('TestController', function($scope) {
                    
                    $scope.register = function() {
                        console.log($scope.person);
                        console.log('jquery >> ' + $('input[name=password]').val());
                    };
                })
                .controller('CheckAllCtrl', function($scope) {
//                    $scope.checkAll = false;
                    $scope.persons = [
                        {name: "dubic", email: "udubic@gmail.com", checked: true},
                        {name: "dubine", email: "dubine@gmail.com", checked: false},
                        {name: "crown", email: "crown@gmail.com", checked: false}
                    ];
                });
    </script>
</html>
