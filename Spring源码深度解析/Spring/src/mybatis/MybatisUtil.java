package mybatis;

import java.io.Reader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MybatisUtil {
	private final static SqlSessionFactory sqlSessionFactory;
	static {
		String resource = "mybatis.xml";
		Reader reader = null;
		try {
			reader = Resources.getResourceAsReader(resource);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
}