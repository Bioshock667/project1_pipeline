package dao;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.bind.DatatypeConverter;

import model.Employee;
import util.JDBConnection;
/*
 * create table employees(
	emp_id int primary key,
	user_name varchar(100),
	password text,
	first_name varchar(100),
	last_name varchar(100),
	rank boolean
)
 */
public class EmployeeDAO extends DAO<Employee> {
	public EmployeeDAO(Connection conn){super(conn);}
	public void create(Employee entry) throws SQLException {
		String sql = "insert into employees values(nextval('emp_id_generator'), ?, ?, ?, ?, ?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, entry.getUserName());
		ps.setString(2, entry.getPassword());
		ps.setString(3, entry.getFirstName());
		ps.setString(4, entry.getLastName());
		ps.setBoolean(4, entry.isManager());
		ps.executeUpdate();
		ps.close();
	}

	public Employee get(int id) throws SQLException {
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
	private String encode(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(input.getBytes());
		return DatatypeConverter.printHexBinary(digest);
	}
	public void update(Employee newEntry) throws SQLException, NoSuchAlgorithmException {
		String sql = "update employees set user_name = ?," +
		"first_name = ?, last_name = ?, rank = ?" +
		(newEntry.getPassword() == null?"":", password = ?")+
		" where emp_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, newEntry.getUserName());
		ps.setString(2, newEntry.getFirstName());
		ps.setString(3, newEntry.getLastName());
		ps.setBoolean(4, (newEntry.isManager()));
		if(newEntry.getPassword() != null) {
			ps.setString(5, encode(newEntry.getPassword()));
			ps.setInt(6, newEntry.getId());
		} else {
			ps.setInt(5, newEntry.getId());
		}
		ps.executeUpdate();
	}

	public void delete(int id) throws SQLException {
		String sql = "delete from employees where emp_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, id);
		ps.executeUpdate();
	}
	public Employee validate(String name, String password) throws SQLException, NoSuchAlgorithmException {
		String sql = "select * from employees where user_name = ? and password = ?";
		String pHash = encode(password);
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, name);
		ps.setString(2, pHash);
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
