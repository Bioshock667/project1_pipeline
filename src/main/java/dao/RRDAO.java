package dao;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

import model.ReimbRequest;
import model.Status;
import util.JDBConnection;

/*CREATE TABLE r_requests (
    rr_id NUMBER(10) PRIMARY KEY,
    emp_id NUMBER(10),
    amount DECIMAL(10,2),
    status CHAR(10),
    resolver_id NUMBER(10),
    date_submitted TIMESTAMP,
    date_resolved TIMESTAMP,
    reason VARCHAR2(80),
    rejection_reason VARCHAR2(80),
    CONSTRAINT FK_RR_SUBMITTER_ID FOREIGN KEY (emp_id) REFERENCES employees(emp_id) ON DELETE CASCADE
    );
    */
public class RRDAO implements DAO<ReimbRequest> {

	public void create(ReimbRequest entry) throws SQLException {
		Connection conn = JDBConnection.getConnection();
		String sql = "insert into r_requests values(rr_id_generator.nextval, ?, ?, ?, null, current_timestamp, null, ?, null)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, entry.getEmpID());
		ps.setDouble(2, entry.getAmount());
		ps.setString(3, entry.getStatus().toString());
		ps.setString(4, entry.getReason());
		ps.executeUpdate();
		ps.close();
	}

	public ReimbRequest get(int id) throws SQLException {
		Connection conn = JDBConnection.getConnection();
		String sql = "select * from r_requests where rr_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, id);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			int emp_id = result.getInt("emp_id");
			double amount = result.getDouble("amount");
			Status status = Status.valueOf(result.getString("status"));
			int resolver_id = result.getInt("resolver_id");
			Timestamp date_submitted = result.getTimestamp("date_submitted");
			Timestamp date_resolved = result.getTimestamp("date_resolved");
			String reason = result.getString("reason");
			String r_reason = result.getString("rejection_reason");
			return new ReimbRequest(id,emp_id, amount, status, resolver_id, date_submitted, date_resolved, reason, r_reason);
		}
		else
			return null;
	}

	public void update(ReimbRequest newEntry) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	public void approve(int rr_id) throws SQLException {
		Connection conn = JDBConnection.getConnection();
		CallableStatement cs = conn.prepareCall("{ call approve_request(?) }");
		cs.setInt(1, rr_id);
		cs.executeUpdate();
	}
	public void deny(int rr_id, String reason) throws SQLException {
		Connection conn = JDBConnection.getConnection();
		CallableStatement cs = conn.prepareCall("{ call deny_request(?,?) }");
		cs.setInt(1, rr_id);
		cs.setString(2, reason);
		cs.executeUpdate();
	}
	public void delete(int id) throws SQLException {
		Connection conn = JDBConnection.getConnection();
		String sql = "delete from r_requests where rr_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, id);
		ps.executeUpdate();
	}
	public ArrayList<ReimbRequest> getRRs(Integer id, String... args) throws SQLException {
		System.out.println("num args" + args.length);
		System.out.println(id);
		if(args.length !=0 && args.length != 2 && args.length != 4 && args.length != 5)
			throw new IllegalArgumentException("Illegal amount of arguments");
		String[] columns = {"amount","date_resolved","date_submitted","emp_id","reason",
				"rejection_reason","resolver_id","rr_id","status"};
		
		if (args.length > 0) {
			boolean found = false;
			for (String c : columns) {
				if (args[0].equals(c)) {
					found = true;
					break;
				}
			}
			if (!found)
				throw new IllegalArgumentException("Illegal amount of arguments");;
		}
		ArrayList<ReimbRequest> RRs = new ArrayList<ReimbRequest>();
		Connection conn = JDBConnection.getConnection();
		StringBuilder sb = new StringBuilder("select * from r_requests");
		if(id != null || args.length >=2) {
			sb.append(" where ");
			if(id != null && args.length >= 2) {
				sb.append("emp_id = ? and " + args[0] + " = ?");
			} else {
				if(id != null)
					sb.append("emp_id = ?");
				else if (args.length >= 2) {
					sb.append(args[0] + " = ?");
				}
			}
		}
		String sql = sb.toString();
		System.out.println(sql);
		
		PreparedStatement ps = conn.prepareStatement(sql);
		if(id != null) {
			ps.setInt(1, id);
			if(args.length == 2) {
				ps.setString(2, args[1]);
			}
		} else if (args.length == 2){
			ps.setString(1, args[1]);
		}
		ResultSet result = ps.executeQuery();
		while(result.next()) {
			int rr_id = result.getInt("rr_id");
			int emp_id = result.getInt("emp_id");
			double amount = result.getDouble("amount");
			Status status = Status.valueOf(result.getString("status"));
			int resolver_id = result.getInt("resolver_id");
			Timestamp date_submitted = result.getTimestamp("date_submitted");
			Timestamp date_resolved = result.getTimestamp("date_resolved");
			String reason = result.getString("reason");
			String r_reason = result.getString("rejection_reason");
			RRs.add( new ReimbRequest(rr_id, emp_id, amount, status, resolver_id,
					date_submitted, date_resolved, reason, r_reason));
		}
		return RRs;
	}
	
	
}
