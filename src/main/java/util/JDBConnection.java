package util;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class JDBConnection {

	public JDBConnection() {
		if(conn == null) {
			try {
				Class.forName("org.postgresql.Driver");
				String fName = JDBConnection.class.getClassLoader().getResource("connections.properties").getFile();
				System.out.println(fName);
//				System.out.println(f.exists());
//				System.out.println(f.getAbsolutePath());
				Properties props = new Properties();
				FileInputStream in = new FileInputStream(fName);
				props.load(in);
				String url = props.getProperty("url");
				String username = props.getProperty("username");
				String password = props.getProperty("password");
				conn = DriverManager.getConnection(url, username, password);
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
	private static Connection conn;
	/**
	 * Returns a Singleton database connection
	 * @return
	 */
	public Connection getConnection() {
		return conn;
	}
}
