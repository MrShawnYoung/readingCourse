package springmvc;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class UserController extends AbstractController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<User> userList = new ArrayList<User>();
		User userA = new User();
		User userB = new User();
		userA.setUsername("张三");
		userA.setAge(27);
		userB.setUsername("李四");
		userB.setAge(37);
		userList.add(userA);
		userList.add(userB);
		return new ModelAndView("userlist", "users", userList);
	}
}