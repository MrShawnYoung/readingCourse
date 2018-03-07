/*定义一个指令'datePicker'*/
actionApp.directive('datePicker', function() {
	return {
		/*限制为属性指令和样式指令*/
		restrict : 'AC',
		/*使用link方法定义指令*/
		link : function(scope, elem, attrs) {
			/*初始化JQueryUI的datepicker*/
			elem.datepicker();
		}
	};
});