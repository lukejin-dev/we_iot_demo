var app = angular.module('p1demoApp', ["ngRoute","highcharts-ng"]);

app.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/', {
    controller:overviewCtr,
    templateUrl:'templates/overview.html'
  }).
  when('/query', {
    controller:queryCtr,
    templateUrl:'templates/query.html'
  }).
  when('/visualization', {
    controller:visualizationCtr,
    templateUrl:'templates/visualization.html'
  }).
  when('/reference', {
    controller:referenceCtr,
    templateUrl:'templates/reference.html'
  }).
  
  otherwise({redirectTo: '/'});
}]);