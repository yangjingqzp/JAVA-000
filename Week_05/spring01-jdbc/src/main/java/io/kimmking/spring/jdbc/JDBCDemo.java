package io.kimmking.spring.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.kimmking.spring.jdbc.entity.Student;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;

/**
 * @author yangjing
 * @version v1.0
 * @description
 * @date 2020/11/18 10:30 PM
 */
public class JDBCDemo {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/test";

	static final String USER = "root";
	static final String PASS = "root";

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		Class.forName(JDBC_DRIVER);

		// 原生方式获取 connection
		//Connection connection = DriverManager.getConnection(DB_URL,USER,PASS);

		// 数据库连接池方式获取 connection
		Connection connection = getConnectionFormHikari();

		Statement statement = connection.createStatement();

		// 测试原生 SQL
		//testForRawSql(connection, statement);

		// 测试 Preparestatment 方式
		testForPrepareStatement(connection, statement);

		statement.close();
		connection.close();
	}

	private static Connection getConnectionFormHikari() throws SQLException {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(DB_URL);
		config.setUsername(USER);
		config.setPassword(PASS);
		// 连接超时 1s
		config.addDataSourceProperty("connectionTimeout", 1000);
		// 空闲超时 60s
		config.addDataSourceProperty("idleTimeout", 60000);
		// 最大连接数 10
		config.addDataSourceProperty("maximumPoolSize", 10);
		DataSource dataSource = new HikariDataSource(config);

		return dataSource.getConnection();
	}

	private static void testForPrepareStatement(Connection connection, Statement statement)
		throws SQLException {
		// 插入
		PreparedStatement preparedStatement = connection.prepareStatement("INSERT into student(`name`, `age`) values (?, ?)", Statement.RETURN_GENERATED_KEYS);
		preparedStatement.setObject(1, "PreparedName");
		preparedStatement.setObject(2, 18);
		int n = preparedStatement.executeUpdate();
		System.out.println("PrepareStatement 插入数据返回："+n);

		ResultSet resultSet = preparedStatement.getGeneratedKeys();
		int id = 0;
		if (resultSet.next()) {
			id = resultSet.getInt(1);
			System.out.println("PrepareStatement 插入数据，id"+id);
		}

		PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT `id`,`name`,`age` FROM student where name=?");
		preparedStatement1.setObject(1, "PreparedName");
		ResultSet selectRes = preparedStatement1.executeQuery();
		while (selectRes.next()) {
			System.out.println(
				"id => "+ selectRes.getInt("id") + "; " +
				"name => " + selectRes.getString("name") +"; " +
				"age => "+ selectRes.getInt("age")
			);
		}

		PreparedStatement ps = connection.prepareStatement("INSERT into student(`name`, `age`) values (?, ?)", Statement.RETURN_GENERATED_KEYS);

		ps.setObject(1, "name1");
		ps.setObject(2, 26);
		ps.addBatch();

		ps.setObject(1, "name2");
		ps.setObject(2, 27);
		ps.addBatch();

		int[] batchRes = ps.executeBatch();
		ps.clearBatch();
		System.out.println(Arrays.toString(batchRes));
	}

	private static void testForRawSql(Connection connection, Statement statement)
		throws SQLException {

		testForInsert(connection, statement);

		List<Student> studentList = testForSelectAll(connection, statement);
		System.out.println(studentList);

		testForUpdate(connection, statement, studentList.get(studentList.size()-1).getId());

		testForDelete(connection, statement, studentList.get(0).getId());
	}

	private static void testForUpdate(Connection connection, Statement statement, int id)
		throws SQLException {
		connection.setAutoCommit(false);

		String sql = "UPDATE student set name='updatedName', age=28 where id="+id;
		statement.execute(sql);
		int updatedCount = statement.getUpdateCount();
		System.out.println("更新数量:"+updatedCount);

		connection.commit();

	}

	private static void testForDelete(Connection connection, Statement statement, int id)
		throws SQLException {
		connection.setAutoCommit(true);

		String sql = "DELETE FROM student WHERE id="+id;
		boolean res = statement.execute(sql);

		System.out.println("删除数据："+res);
	}

	private static List<Student> testForSelectAll(Connection connection, Statement statement)
		throws SQLException {
		String sql = "SELECT id, name, age FROM student";
		ResultSet rs = statement.executeQuery(sql);
		List<Student> list = new ArrayList<>();
		while (rs.next()) {
			Student student = new Student();
			student.setId(rs.getInt("id"));
			student.setAge(rs.getInt("age"));
			student.setName(rs.getString("name"));
			list.add(student);
		}
		return list;
	}

	private static void testForInsert(Connection connection, Statement statement)
		throws SQLException {
		connection.setAutoCommit(false);
		System.out.println("事务隔离级别："+connection.getTransactionIsolation());

		Boolean res = statement.execute("insert student(`name`, `age`) values ('geektime', 3), ('chengcheng', 18)");
		System.out.println("插入数据："+res);

		connection.commit();
	}
}
