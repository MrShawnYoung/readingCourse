/*定义模块actionApp，并依赖于路由模块ngRoute*/
var actionApp = angular.module('actionApp', [ 'ngRoute' ]);
/*配置路由，并注入$routeProvider来配置*/
actionApp.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/oper', { //路由名称
		controller : 'View1Controller', //控制器名称
		templateUrl : 'views/view1.html', //视图地址
	}).when('/directive', {
		controller : 'View2Controller',
		templateUrl : 'views/view2.html',
	});
} ]);