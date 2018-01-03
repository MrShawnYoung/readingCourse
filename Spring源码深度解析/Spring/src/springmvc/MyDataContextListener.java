package springmvc;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyDataContextListener implements ServletContextListener {
	private ServletContext context = null;

	public MyDataContextListener() {

	}

	@Override
	// 该方法在ServletContext启动之后被调用，并准备好处理客户端请求
	public void contextInitialized(ServletContextEvent event) {
		this.context = event.getServletContext();
		context.setAttribute("myData", "this is myData");
		// 通过你可以实现自己的逻辑并将结果记录在属性中
	}

	@Override
	// 这个方法在ServletContext将要关闭的时候调用
	public void contextDestroyed(ServletContextEvent event) {
		this.context = null;
	}
}