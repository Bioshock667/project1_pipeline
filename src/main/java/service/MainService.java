package service;
import java.sql.SQLException;

import org.apache.logging.log4j.*;
import java.util.ArrayList;

import dao.EmployeeDAO;
import dao.RRDAO;
import model.*;
public class MainService {
	private static EmployeeDAO eDAO = new EmployeeDAO();
	private static RRDAO rrDAO = new RRDAO();

	final public static Logger logger;
	
	static {
		System.setProperty("log4j.configurationFile","src/main/resources/configuration.xml");
		logger = LogManager.getLogger(MainService.class);
		System.out.println("Configuration File Defined To Be :: "+System.getProperty("log4j.configurationFile"));
		System.setProperty("log4j2.debug", "true");
	}
	
	public static void createEmployee(Employee e) throws SQLException {
		eDAO.create(e);
	}
	public static void submitReq(int emp_id, double amount, String reason) throws SQLException {
		rrDAO.create(new ReimbRequest(0,emp_id, amount, Status.PENDING, null, null, null, null, reason, null));
	}
	public static Employee login(String username, String password) throws SQLException {
		logger.info("Validating user: " + username);
		return eDAO.validate(username, password);
	}
	public static ArrayList<Employee> getEmployees() throws SQLException {
		return eDAO.getAllEmployees();
	}
	public static Employee getEmployee(int id) throws SQLException {
		return eDAO.get(id);
	}
	public static ArrayList<ReimbRequest> getAllRRs(String... args) throws SQLException {
		logger.info("getting all RRs");
		return rrDAO.getRRs(null, args);
	}
	public static ArrayList<ReimbRequest> getRRsbyId(int id, String... args) throws SQLException {
		logger.info("getting Requests by id "+ id);
		return rrDAO.getRRs(id, args);
	}
	public static ReimbRequest getRR(int id) throws SQLException {
		return rrDAO.get(id);
	}
	public static void updateEmployee(Employee e) throws SQLException {
		eDAO.update(e);
	}

	public static void approveReq(int rr_id, int resolver_id) throws SQLException {
		rrDAO.approve(rr_id, resolver_id);
	}
	
	public static void denyReq(int rr_id, int resolver_id, String reason) throws SQLException {
		rrDAO.deny(rr_id, resolver_id, reason);
	}
	
	public static void deleteEmployee(int id) throws SQLException {
		
	}
	
	public static void deleteReq(int id) throws SQLException {
		
	}
}
