package serv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.*;

/**
 * Servlet implementation class MainServlet
 */
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("test1");
		MainHelper.handleGetRequest(request, response);
//		HttpSession session = request.getSession(false);
//		if(session == null) {
//			System.out.println("User Timed out");
//			response.getWriter().append("session expired");
//			return;
//		}
//		Object n = session.getAttribute("notes");
//		if(n != null) {
//			ArrayList<String> notes = (ArrayList<String>)n;
//			System.out.println(request.getRequestURI());
//			System.out.println(notes);
//			if(request.getRequestURI().equals("/Project1/MainServlet/notes")) {
//				response.getWriter().append(notes.toString());
//			}
//			else
//				response.getWriter().append("Hello World");
//		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MainHelper.handlePostRequest(request, response);
//		BufferedReader body = request.getReader();
//		String input = body.readLine();
//		System.out.println(input);
//		if(request.getRequestURI().equals("/Project1/MainServlet/login")) {
//		String[] values = URLDecoder.decode(input, "utf-8").split("&");
//		String name = values[0].split("=")[1];
//		String password = values[1].split("=")[1];
//		System.out.println(name + " " + password);
//		if(name.equals("seth") && password.equals("password")) {
//			HttpSession session = request.getSession(true);
//			session.setMaxInactiveInterval(5);
//			ArrayList<String> notes = new ArrayList<String>();
//			notes.add("\"This is a note\"");
//			notes.add("\"This is another note\"");
//			session.setAttribute("notes", notes);
//			response.getWriter().append("Session=" + session.getId());
//		}
//		}
//		else if(request.getRequestURI().equals("/Project1/MainServlet/notes")) {
//			String note = URLDecoder.decode(input, "utf-8");
//			HttpSession session = request.getSession(false);
//			if(session == null) {
//				response.getWriter().append("session expired");
//				return;
//			}
//			Object n = session.getAttribute("notes");
//			if(n != null) {
//				ArrayList<String> notes = (ArrayList<String>) n;
//				notes.add("\"" + note + "\"");
//				response.getWriter().append("success");
//			}
//			
//		}
	}
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
}
