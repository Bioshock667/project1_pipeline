import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Test;
import junit.framework.TestCase;
import model.*;
import service.MainService;
public class ServiceTester{

	public ServiceTester() {
	}
	
	@Test
	public void getEmployee() {
		try {
			Employee emp = MainService.getEmployee(0);
			assertEquals("seth", emp.getUserName());
			assertEquals("Seth", emp.getFirstName());
			assertEquals("Lemanek", emp.getLastName());
			assertEquals(true , emp.isManager());
			assertNull(emp.getPassword());
		} catch (SQLException e) {
			fail("SQL Exception has been thrown at getEmployee(): " + e.getMessage());
		}
	}
	
	@Test
	public void updateEmployee() {
		try {
			Employee emp1 = MainService.getEmployee(1); //unchanged original account 
			Employee emp2 = MainService.getEmployee(1); //account to be changed
			emp2.setFirstName("Sheldon");
			emp2.setLastName("Plankton");
			MainService.updateEmployee(emp2);
			assertEquals("Sheldon", emp2.getFirstName());
			assertEquals("Plankton", emp2.getLastName());
			MainService.updateEmployee(emp1);
		}catch (SQLException e) {
			fail("SQL Exception has been thrown at updateEmployee(): " + e.getMessage());
		}
	}
	
	@Test
	public void updatePasswordAndTestLogin() {
		try { //generate a number between 32 and 126 for valid characters
			char[] randomPassword = new char[10];
			for(int i = 0; i < 10; i++) {
				double a = (Math.random() * 94) + 32;
				char c = (char)Math.floor(a);
				randomPassword[i] = c;
			}
			String newPassword = randomPassword.toString();
			Employee emp = MainService.getEmployee(1);
			emp.setPassword(newPassword);
			MainService.updateEmployee(emp);
			Employee emp1 = MainService.login(emp.getUserName(), newPassword);
			assertNotNull(emp1);
		} catch (SQLException e) {
			fail("SQL Exception has been thrown at updatePasswordAndTestLogin(): " + e.getMessage());
		}
	}
	
	@Test
	public void submitAndGetRequest() {
		try {
			double amount = Math.floor(Math.random() * 100); //a random number to uniquely identify a request
			MainService.submitReq(1, amount, null);
			ArrayList<ReimbRequest> reqs = MainService.getAllRRs();
			for(ReimbRequest r : reqs) {
				if(r.getAmount() == amount) {
					return;
				}
			}
			fail("Newly added request not found");
		} catch (SQLException e) {
			fail("SQL Exception has been thrown at submitAndGetRequest(): " + e.getMessage());
		}
	}
}
