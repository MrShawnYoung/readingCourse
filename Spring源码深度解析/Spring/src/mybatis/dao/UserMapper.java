package mybatis.dao;

import jdbc.User;

public interface UserMapper {
	public void insertUser(User user);

	public User getUser(Integer id);
}