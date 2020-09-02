package dao;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DAO<T> {
	protected Connection conn;
	public DAO(Connection conn) {
		this.conn = conn;
	}
	abstract public void create(T entry) throws Exception;
	abstract public T get(int id) throws Exception;
	abstract public void update(T newEntry) throws Exception;
	abstract public void delete(int id) throws Exception;
	
}
