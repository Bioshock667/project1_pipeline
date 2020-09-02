package serv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.SSLEngineResult.Status;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import model.Employee;
import model.ReimbRequest;
import service.MainService;
import util.JDBConnection;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class MainHelper {
	public static boolean hasSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		return session != null ;
	}
	public static void handleGetRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//System.out.println("incoming request  GET " + request.getRequestURI());
		if(request.getRequestURI().equals("/Project1/MainServlet/isloggedin")) {
			response.getWriter().append(Boolean.toString(hasSession(request, response)));
			return;
		}
		if(!hasSession(request, response)) {
			sendUnauthorized(response);
			return;
		}
		try {
			switch(request.getRequestURI()) {
			case "/Project1/MainServlet/getRequests":
				getRequests(false, request, response);
				break;
			case "/Project1/MainServlet/getAllRequests":
				getRequests(true, request, response);
			case "/Project1/MainServlet/approveRequest":
				approveRequest(request, response);
				break;
			case "/Project1/MainServlet/denyRequest":
				denyRequest(request, response);
				break;
			case "/Project1/MainServlet/getMyAccount":
				getAccount(false, request, response);
				break;
			case "/Project1/MainServlet/logout":
				logout(request, response);
				break;
			}
		}catch (SQLException e) {
			sendServerError(response, e.getMessage());
		}
	}
	
	public static void handlePostRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//System.out.println("incoming request  POST " + request.getRequestURI());
		try {
		if(request.getRequestURI().equals("/Project1/MainServlet/login"))
			login(request, response);
		else if(!hasSession(request, response)) {
			sendUnauthorized(response);
			return;
		}
		
		if(request.getRequestURI().equals("/Project1/MainServlet/submitRequest")) {
			submitRequest(request, response);
		} else if (request.getRequestURI().equals("/Project1/MainServlet/updateUser")) {
			changeAccount(request, response);
		}
		} catch (Exception e) {
			sendServerError(response, e.getMessage());
		}
		
	}
	
	public static void getRequests(boolean getAll, HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		HttpSession session = request.getSession(false);
		if(session == null) {
			sendUnauthorized(response);
			return;
		}
		Employee emp = (Employee) session.getAttribute("info");
		if(emp == null) {
			sendUnauthorized(response);
			return;
		}
		if(getAll && !emp.isManager()) {
			sendForbidden(response);
			return;
		}
			String field = request.getParameter("field");
			String value = request.getParameter("value");
			ArrayList<ReimbRequest> rrs;
			
			if(field == null || value == null) {
				if(getAll)
					rrs = MainService.getAllRRs();
				else
					rrs = MainService.getRRsbyId(emp.getId());
			}
			else {
				if(getAll)
					rrs = MainService.getAllRRs(field, value);
				else
					rrs = MainService.getRRsbyId(emp.getId(), field, value);
			}
			ArrayList<JSONObject> jos = new ArrayList<>();
			for(ReimbRequest r : rrs) {
				
				jos.add(RRtoJSON(r));
			}
			response.getWriter().append(jos.toString());
		
	}
	public static void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSuchAlgorithmException {
		String body = request.getReader().readLine();
		try {
			JSONObject jo;
			jo = new JSONObject(body);
		
			String name = jo.getString("username");
			String password = jo.getString("password");
			Employee user = MainService.login(name, password);
			
			if(user != null) {
				HttpSession session = request.getSession();
				session.setAttribute("info", user);
				JSONObject userInfo = EtoJSON(user);
				response.getWriter().append(userInfo.toString());
			}
			else {
				response.getWriter().append("unsuccessful");
			}
		} catch (SQLException e) {
			String s = e.getMessage();
			sendServerError(response, s);
		} catch (JSONException e1) {
			sendServerError(response, e1.getMessage());
		}
	}

	public static void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if(session != null) {
			session.invalidate();
			response.getWriter().append("logout successful");
		}
	}
	
	public static void submitRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		HttpSession session = request.getSession(false);
		if(session == null) {
			sendUnauthorized(response);
			return;
		}
		Employee user = (Employee) session.getAttribute("info");
		if(user == null) {
			sendUnauthorized(response);
			return;
		}
		String body = request.getReader().readLine();
		
		try {
			JSONObject jo;
			jo = new JSONObject(body);
			double amount = jo.getDouble("amount");
			String reason = jo.getString("reason");
			MainService.submitReq(user.getId(), amount, reason);
			response.getWriter().append("success");
		} catch (JSONException e1) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().append("JSON Error: " + e1.getMessage());
		}
	}
	public static void approveRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		HttpSession session = request.getSession(false);
		if(session == null) {
			sendUnauthorized(response);
			return;
		}
		Employee user = (Employee) session.getAttribute("info");
		if(user == null) {
			sendUnauthorized(response);
			return;
		}
		if(!user.isManager()) {
			sendForbidden(response);
			return;
		}
		String id = request.getParameter("id");
		if(id != null) {
			int rr_id = Integer.parseInt(id);
			MainService.approveReq(rr_id, user.getId());
		}
	}
	public static void denyRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		HttpSession session = request.getSession(false);
		if(session == null) {
			return;
		}
		Employee user = (Employee) session.getAttribute("info");
		if(user == null) {
			return;
		}
		if(!user.isManager()) {
			sendForbidden(response);
			return;
		}
		int rr_id = Integer.parseInt( request.getParameter("id"));
		String reason = request.getParameter("reason");
		MainService.denyReq(rr_id, user.getId(), reason);
	}
	public static void changeAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException, NoSuchAlgorithmException {
		HttpSession session = request.getSession(false);
		if(session == null) {
			sendUnauthorized(response);
			return;
		}
		Employee user = (Employee) session.getAttribute("info");
		if(user == null) {
			sendUnauthorized(response);
			return;
		}
		String body = request.getReader().readLine();
		JSONObject jo = new JSONObject(body);
		Employee newCreds = JSONtoE(jo, user.isManager());
		MainService.updateEmployee(newCreds);
		if(newCreds.getPassword() != null) {
			logout(request, response);
		} else {
			session.setAttribute("info", newCreds);
		}
	}
	public static void getAccount(boolean getAll, HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		HttpSession session = request.getSession(false);
		if(session == null) {
			sendUnauthorized(response);
			return;
		}
		Employee user = (Employee) session.getAttribute("info");
		if(user == null) {
			sendUnauthorized(response);
			return;
		}
		JSONObject jo = EtoJSON(user);
		response.getWriter().append(jo.toString());
	}
	public static void sendForbidden(HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.getWriter().append("You do not have permission to do this.");
	}
	
	public static void sendUnauthorized(HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().append("unauthorized");
	}
	private static void sendServerError(HttpServletResponse response, String error_message) throws IOException {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.getWriter().append(error_message);
	}
	private static JSONObject RRtoJSON(ReimbRequest r) {
		return new JSONObject().put("id", r.getId())
		.put("status", r.getStatus())
		.put("amount", r.getAmount())
		.put("reason", r.getReason())
		.put("subName", r.getSubmitterName())
		.put("resName", r.getResolverName())
		.put("dateSub", r.getDateSubmitted().toString())
		.put("dateRes", (r.getDateResolved() == null?"":r.getDateResolved().toString()));
	}
	private static JSONObject EtoJSON(Employee user) {
		return new JSONObject().put("id", user.getId()).put("firstName", user.getFirstName()).put("lastName", user.getLastName())
			.put("type",user.isManager()?"manager":"employee").put("userName", user.getUserName());
	}

	private static Employee JSONtoE(JSONObject jo, boolean isManager) {
		Object p = jo.get("password");
		String pass = null;
		if(!p.equals(null)) {
			pass = (String)p;
		}
		return new Employee(jo.getInt("id"), jo.getString("userName"), pass, jo.getString("firstName")
				, jo.getString("lastName") , isManager);
	}
}
