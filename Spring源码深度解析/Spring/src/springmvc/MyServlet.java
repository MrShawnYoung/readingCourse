package springmvc;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyServlet extends HttpServlet {

	@Override
	public void init() {
		System.out.println("this is init method");
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		handleLogic(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		handleLogic(request, response);
	}

	private void handleLogic(HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println("handle myLogic");
		ServletContext sc = getServletContext();
		RequestDispatcher rd = null;
		rd = sc.getRequestDispatcher("/index.jsp");// 定向的页面
		try {
			rd.forward(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}
}