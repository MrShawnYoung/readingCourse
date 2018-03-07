/*定义控制器，并注入$rootScope,$scope,$http*/
actionApp.controller('View1Controller', [ '$rootScope', '$scope', '$http', function($rootScope, $scope, $http) {
	/*监听$viewContentLoaded加载*/
	$scope.$on('$viewContentLoaded', function() {
		console.log('页面加载完成');
	});
	/*定义一个search方法，通过ng-click调用*/
	$scope.search = function() {
		/*获取页面定义的ng-model="personName"*/
		personName = $scope.personName;
		/*向服务器发送get请求*/
		$http.get('search', {
			/*增加请求参数*/
			params : {
				personName : personName
			}
		/*成功后的回调*/
		}).success(function(data) {
			/*返回数据赋值给模型person*/
			$scope.person = data;
		});
		;
	};
} ]);
actionApp.controller('View2Controller', [ '$rootScope', '$scope', function($rootScope, $scope) {
	$scope.$on('$viewContentLoaded', function() {
		console.log('页面加载完成');
	});
} ]);