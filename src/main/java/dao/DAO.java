package dao;

import java.sql.SQLException;

public interface DAO<T> {

	public void create(T entry) throws SQLException;
	public T get(int id) throws SQLException;
	public void update(T newEntry) throws SQLException;
	public void delete(int id) throws SQLException;
	
}
