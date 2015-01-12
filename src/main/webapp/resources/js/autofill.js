/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var app = angular.module('AutofillSubmit', []);
app.directive('autofillSubmit', function () {
  'use strict';

  return {
    restrict: 'A',
    priority: -10,
    link: function (scope, element) {

      element.on('submit', function () {
        element.find('input, textarea, select').trigger('input').trigger('change').trigger('keydown');
      });

    }
  };
});
