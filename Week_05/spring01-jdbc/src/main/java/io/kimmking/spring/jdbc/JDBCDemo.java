package io.kimmking.spring.jdbc;

import io.kimmking.spring.jdbc.entity.Student;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

		Connection connection = DriverManager.getConnection(DB_URL,USER,PASS);
		Statement statement = connection.createStatement();

		testForInsert(connection, statement);

		List<Student> studentList = testForSelectAll(connection, statement);
		System.out.println(studentList);

		testForDelete(connection, statement, studentList.get(0).getId());

		statement.close();
		connection.close();
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
