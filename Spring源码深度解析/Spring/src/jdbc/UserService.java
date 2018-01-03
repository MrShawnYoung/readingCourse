package jdbc;

import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
public interface UserService {
	public void save(User user) throws Exception;

	public List<User> getUsers();
}