package serv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Employee;
import model.ReimbRequest;
import service.MainService;
import util.JDBConnection;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
public class MainHelper {
	private static final String HTMLDIR = "C:\\Users\\jaffa\\Documents\\workspace-sts-3.9.5.RELEASE\\Project1\\src\\main\\resources\\html\\";
	public static boolean hasSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		System.out.println("Check session = " + session + " and there is a not null user " + (session == null?"cant get":session.getAttribute("info")));
		System.out.println(session != null && session.getAttribute("info") != null);
		return session != null && session.getAttribute("info") != null;
	}
	public static void handleGetRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("incoming request  GET " + request.getRequestURI());
		if(request.getRequestURI().equals("/Project1/MainServlet/isloggedin")) {
			response.getWriter().append(Boolean.toString(hasSession(request, response)));
			return;
		}
		if(!hasSession(request, response)) {
			sendUnauthorized(response);
			System.out.println("test5");
			return;
		}
		try {
			switch(request.getRequestURI()) {
			case "/Project1/MainServlet/home":
				System.out.println("test6");
				response.getWriter().append("home");
				break;
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
			case "/Project1/MainServlet/logout":
				logout(request, response);
				break;
			}
		}catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().append("SQL Error: " + e.getMessage());
		}
	}
	
	public static void handlePostRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("incoming request  POST " + request.getRequestURI());
		if(request.getRequestURI().equals("/Project1/MainServlet/login"))
			login(request, response);
		else if(!hasSession(request, response)) {
			sendUnauthorized(response);
			return;
		}
		
		System.out.println("User has signed in");
		System.out.println(request.getRequestURI());
		try {
		if(request.getRequestURI().equals("/Project1/MainServlet/submitRequest")) {
			submitRequest(request, response);
		} else if (request.getRequestURI().equals("/Project1/MainServlet/changeAccount")) {
			
		}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().append("SQL Error: " + e.getMessage());
		}
		
	}
	
	private static void getRequests(boolean getAll, HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
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
			System.out.println("field"+field+"value" + value);
			System.out.println(emp.getId());
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
				
				jos.add( new JSONObject().put("id", r.getId())
						.put("status", r.getStatus())
						.put("amount", r.getAmount())
						.put("reason", r.getReason())
						.put("dateSub", r.getDateSubmitted().toString())
						.put("dateRes", (r.getDateResolved() == null?"":r.getDateResolved().toString())));
			}
			System.out.println(jos);
			response.getWriter().append(jos.toString());
		
	}
	public static void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String body = request.getReader().readLine();
		System.out.println(body);
		
		try {
			JSONObject jo;
			jo = new JSONObject(body);
		
			System.out.println(jo.getClass());
			System.out.println(jo.toString());
			String name = jo.getString("username");
			String password = jo.getString("password");
			Employee user = MainService.login(name, password);
			
			if(user != null) {
				HttpSession session = request.getSession();
				session.setAttribute("info", user);
				Cookie type;
				if(user.isManager())
					type = new Cookie("type", "manager");
				else
					type = new Cookie("type", "employee");
				type.setPath("/Project1");
				response.addCookie(type);
				response.getWriter().append("success");
			}
			else {
				response.getWriter().append("unsuccessful");
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().append("SQL Error: " + e.getMessage());
		} catch (JSONException e1) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().append("JSON Error: " + e1.getMessage());
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
			return;
		}
		Employee user = (Employee) session.getAttribute("info");
		if(user == null) {
			return;
		}
		String body = request.getReader().readLine();
		System.out.println(body);
		
		try {
			JSONObject jo;
			jo = new JSONObject(body);
			System.out.println(jo.getClass());
			System.out.println(jo.toString());
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
			return;
		}
		Employee user = (Employee) session.getAttribute("info");
		if(user == null) {
			return;
		}
		if(!user.isManager()) {
			sendForbidden(response);
		}
		String id = request.getParameter("id");
		if(id != null) {
			int rr_id = Integer.parseInt(id);
			MainService.approveReq(rr_id);
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
		}
		int rr_id = Integer.parseInt( request.getParameter("id"));
		String reason = request.getParameter("reason");
		MainService.denyReq(rr_id, reason);
	}
	public static void changeAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		HttpSession session = request.getSession(false);
		if(session == null) {
			sendUnauthorized(response);
			return;
		}
		Employee user = (Employee) session.getAttribute("info");
		if(user == null) {
			return;
		}
	}
	protected static void sendForbidden(HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.getWriter().append("You do not have permission to do this.");
	}
	
	public static void sendUnauthorized(HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().append("unauthorized");
	}
}
