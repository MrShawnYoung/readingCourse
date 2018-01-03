package mybatis;

import jdbc.User;
import mybatis.dao.UserMapper;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;

public class TestMapper {
	static SqlSessionFactory sqlSessionFactory = null;
	static {
		sqlSessionFactory = MybatisUtil.getSqlSessionFactory();
	}

	// @Test
	public void testAdd() {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			User user = new User("tom", new Integer(5));
			userMapper.insertUser(user);
			sqlSession.commit();// 这里一定要提交，不然数据进不去数据库中
		} finally {
			sqlSession.close();
		}
	}

	// @Test
	public void getUser() {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			User user = userMapper.getUser(1);
			System.out.println("name：" + user.getName() + "|age："
					+ user.getAge());
		} finally {
			sqlSession.close();
		}
	}
}