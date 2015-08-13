/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

angular.module('Scribbles', [])
        .directive('updateOnBlur', function () {
            'use strict';

            return {
                restrict: 'A',
                priority: -10,
//                require: 'ngModel',
                link: function (scope, element, attr) {
                    element.blur(function () {
                        $('input[type=password]').trigger('change');
                    });
//                        scope.$watch(function() {
//                            return ngModel.$viewValue;
//                        }, function(value) {
//                            
//                        });
                }
            };
        })
        .directive('popShow', function () {
            'use strict';
//attr : showDialog,sDialog[jquery dialog selector],onShow,onShown,onHide[callbacks]
            return {
                restrict: 'A',
                priority: -10,
                scope: {
                    popShowTrigger: '='
                },
                link: function (scope, element, attr) {
                    scope.$watch(function () {
                        return scope.popShowTrigger;
                    }, function (value) {
                        if (value === true) {
                            if (attr.popShowAnim === 'slide')
                                $('#' + attr.popShow).slideDown();
                            if (attr.popShowAnim === 'fade')
                                $('#' + attr.popShow).fadeIn();
                        } else {
                            if (attr.popShowAnim === 'slide')
                                $('#' + attr.popShow).slideUp();
                            if (attr.popShowAnim === 'fade')
                                $('#' + attr.popShow).fadeOut();
                        }
                    });

                    if (!attr.popShowTrigger) {
                        element.click(function () {
                            if (attr.popShowAnim === 'slide')
                                $('#' + attr.popShow).slideToggle();
                            if (attr.popShowAnim === 'fade')
                                $('#' + attr.popShow).fadeToggle();
                        });
                    }
                }
            };
        })
        .directive('jqUniform', function () {
            'use strict';
            return {
                restrict: 'A',
                link: function (scope, element, attr) {
                    element.uniform();
                }
            };
        })
        .directive('select2', function () {
            'use strict';
            return {
                restrict: 'A',
                priority: -10,
                require: 'ngModel',
                scope: {
                    select2: '='
                },
                link: function (scope, element, attr, ngModel) {
                    element.select2({
                        tags: true
                    });

                    if (attr.select2Sync) {
                        scope.$watch(function ( ) {
                            return ngModel.$viewValue;
                        }, function (value) {
//                            console.log('select values = ' + value);
                            element.select2("val", value);
                        });
                    }
                }
            };
        })
        .directive('tagCloud', function () {//converts an input field to a select2 tagcloud
            'use strict';
            return {
                restrict: 'A',
                priority: 0,
                require: 'ngModel',
                scope: {
                    tagCloud: '='
                },
                link: function (scope, element, attr, ngModel) {
                    element.select2({
                        tags: scope.tagCloud,
                        tokenSeparators: [",", " ","."]
                    });

                    scope.$watch(function ( ) {
                        return ngModel.$modelValue;
                    }, function (value) {
//                            console.log('select values = ' + value);
                        if (!angular.isUndefined(value)) {
                            element.select2("val", value.split(","));//value is returned as comma sep
                        }
                    });
                    scope.$watch(function ( ) {
                        return scope.tagCloud;
                    }, function (value) {
                        if (!angular.isUndefined(value)) {
                            element.select2({tags: scope.tagCloud,tokenSeparators: [",", " ","."]});
                        }
                    });
                }
            };
        })
        .directive('dClick', function () {
            'use strict';
            return {
                restrict: 'A',
                priority: -1,
                scope: {
                    dClick: '='
                },
                link: function (scope, element, attr, ngModel) {
                    element.click(function () {
                        element.parent().find('.' + scope.dClick).slideDown();
                    });
                }
            };
        }).directive('addthisToolbox', ['$timeout', function ($timeout) {
        return {
            restrict: 'A',
            transclude: true,
            replace: true,
            template: '<div ng-transclude></div>',
            link: function ($scope, element, attrs) {
                $timeout(function () {
                    addthis.init();
                    addthis.toolbox($(element).get(), {}, {
                        url: attrs.url,
                        title: "My Awesome Blog",
                        description: 'Checkout this awesome post on blog.me'
                    });
                });
            }
        };
    }]).directive('ajaxButton', function () {
    return {
        restrict: 'A',
        priority: 10,
        scope: {
            ajaxButton: '='
        },
        link: function (scope, element, attr, ngModel) {
            var txt = element.text();
            scope.$watch(function ( ) {
                return scope.ajaxButton;
            }, function (value) {
                if (value) {
                    element.text(attr.ajaxText);
                }else element.text(txt);
            });
        }
    };
});

