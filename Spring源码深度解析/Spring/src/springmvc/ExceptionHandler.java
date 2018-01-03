package springmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@Component
public class ExceptionHandler implements HandlerExceptionResolver {
	private static final Log logs = LogFactory.getLog(ExceptionHandler.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception exception) {
		request.setAttribute("exception", exception.toString());
		request.setAttribute("exceptionStack", exception);
		logs.error(exception.toString(), exception);
		return new ModelAndView("error/exception");
	}
}