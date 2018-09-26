import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Test;

import junit.framework.Assert;
import model.*;
import service.MainService;
public class ServiceTester{

	public ServiceTester() {
	}
	
	@Test
	public void getEmployee() {
		try {
			Employee emp = MainService.getEmployee(0);
			Assert.assertEquals("seth", emp.getUserName());
			Assert.assertEquals("Seth", emp.getFirstName());
			Assert.assertEquals("Lemanek", emp.getLastName());
			Assert.assertEquals(true , emp.isManager());
			Assert.assertNull(emp.getPassword());
		} catch (SQLException e) {
			Assert.fail("SQL Exception has been thrown at getEmployee(): " + e.getMessage());
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
			Assert.assertEquals("Sheldon", emp2.getFirstName());
			Assert.assertEquals("Plankton", emp2.getLastName());
			MainService.updateEmployee(emp1);
		}catch (SQLException e) {
			Assert.fail("SQL Exception has been thrown at updateEmployee(): " + e.getMessage());
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
			Assert.assertNotNull(emp1);
		} catch (SQLException e) {
			Assert.fail("SQL Exception has been thrown at updatePasswordAndTestLogin(): " + e.getMessage());
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
			Assert.fail("Newly added request not found");
		} catch (SQLException e) {
			Assert.fail("SQL Exception has been thrown at submitAndGetRequest(): " + e.getMessage());
		}
	}
}
