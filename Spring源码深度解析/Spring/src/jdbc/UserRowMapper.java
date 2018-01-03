package jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class UserRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet set, int index) throws SQLException {
		User person = new User(set.getInt("id"), set.getString("name"),
				set.getInt("age"), set.getString("sex"));
		return person;
	}
}