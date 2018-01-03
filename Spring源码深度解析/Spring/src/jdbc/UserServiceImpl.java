package jdbc;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class UserServiceImpl implements UserService {
	private JdbcTemplate jdbcTemplate;

	// 设置数据源
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void save(User user) {
		jdbcTemplate
				.update("insert into t_user(id,name,age,sex)values(SEQ_USER_ID.nextval,?,?,?)",
						new Object[] { user.getName(), user.getAge(),
								user.getSex() }, new int[] {
								java.sql.Types.VARCHAR, java.sql.Types.INTEGER,
								java.sql.Types.VARCHAR });
		// 事务测试，加上这句代码则数据不会保存到数据库中
		throw new RuntimeException("aa");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsers() {
		List<User> list = jdbcTemplate.query("select * from t_user",
				new UserRowMapper());
		return list;
	}
}