package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Employee;
import util.JDBConnection;
public class EmployeeDAO implements DAO<Employee> {

	public void create(Employee entry) throws SQLException {
		Connection conn = JDBConnection.getConnection();
		String sql = "insert into employees values(emp_id_generator.next_val, ?, ?, ?, ?, ?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, entry.getUserName());
		ps.setString(2, entry.getPassword());
		ps.setString(3, entry.getFirstName());
		ps.setString(4, entry.getLastName());
		ps.setInt(5, (entry.isManager()?1:0));
		ps.executeUpdate();
		ps.close();
	}

	public Employee get(int id) throws SQLException {
		Connection conn = JDBConnection.getConnection();
		String sql = "select * from employees where emp_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, id);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			Employee e = new Employee(result.getInt("emp_id"), result.getString("user_name"), null,
					result.getString("first_name"), result.getString("last_name"), result.getBoolean("rank"));
			return e;
		}
		return null;
	}

	public void update(Employee newEntry) throws SQLException {
		Connection conn = JDBConnection.getConnection();
		String sql = "update employees set user_name = ?," +
		"first_name = ?, last_name = ?, rank = ?" +
		(newEntry.getPassword() == null?"":", password = ?")+
		" where emp_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, newEntry.getUserName());
		ps.setString(2, newEntry.getFirstName());
		ps.setString(3, newEntry.getLastName());
		ps.setInt(4, (newEntry.isManager()?1:0));
		if(newEntry.getPassword() != null) {
			ps.setString(5, newEntry.getPassword());
			ps.setInt(6, newEntry.getId());
		} else {
			ps.setInt(5, newEntry.getId());
		}
		ps.executeUpdate();
	}

	public void delete(int id) throws SQLException {
		Connection conn = JDBConnection.getConnection();
		String sql = "delete from employees where emp_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, id);
		ps.executeUpdate();
	}
	public Employee validate(String name, String password) throws SQLException {
		Connection conn = JDBConnection.getConnection();
		String sql = "select * from employees where user_name = ? and password = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, name);
		ps.setString(2, password);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			Employee e = new Employee(result.getInt("emp_id"), result.getString("user_name"), null,
					result.getString("first_name"), result.getString("last_name"), result.getBoolean("rank"));
			return e;
		}
		return null;
	}
	public ArrayList<Employee> getAllEmployees() throws SQLException {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		Connection conn = JDBConnection.getConnection();
		String sql = "select * from employees";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet result = ps.executeQuery();
		while(result.next()) {
			Employee e = new Employee(result.getInt("emp_id"), result.getString("user_name"), null,
					result.getString("first_name"), result.getString("last_name"), result.getBoolean("rank"));
			emp.add( e);
		}
		return null;
	}
}
