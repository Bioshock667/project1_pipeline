package dao;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

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
public class RRDAO extends DAO<ReimbRequest> {

	public RRDAO(Connection conn) {
		super(conn);
	}

	public void create(ReimbRequest entry) throws SQLException {
		String sql = "insert into r_requests values(nextval('rr_id_generator'), ?, ?, ?, null, current_timestamp, null, ?, null)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, entry.getEmpID());
		ps.setDouble(2, entry.getAmount());
		ps.setString(3, entry.getStatus().toString());
		ps.setString(4, entry.getReason());
		ps.executeUpdate();
		ps.close();
	}

	public ReimbRequest get(int id) throws SQLException {
		String sql = "    select sub_query.*, employees.first_name || ' ' || employees.last_name as submitter_name from" + 
				"    ( select r.*," + 
				"        (case when e.first_name is null and e.last_name is null " +
				" 			then null  else  e.first_name || ' ' || e.last_name end)  as resolver_name" + 
				"        from r_requests r left join employees e on r.resolver_id=e.emp_id where r.rr_id = ?" + 
				"    ) sub_query" + 
				"    join employees on sub_query.emp_id=employees.emp_id;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, id);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			int emp_id = result.getInt("emp_id");
			double amount = result.getDouble("amount");
			System.out.println("What is wrong with "+ result.getString("status"));
			System.out.println(Status.PENDING.toString());
			Status status = Status.valueOf(result.getString("status"));
			int resolver_id = result.getInt("resolver_id");
			Timestamp date_submitted = result.getTimestamp("date_submitted");
			Timestamp date_resolved = result.getTimestamp("date_resolved");
			String submitterName = result.getString("submitter_name");
			String resolverName = result.getString("resolver_name");
			String reason = result.getString("reason");
			String r_reason = result.getString("rejection_reason");
			return new ReimbRequest(id,emp_id, amount, status, submitterName, resolverName, date_submitted, date_resolved, reason, r_reason);
		}
		else
			return null;
	}

	public void update(ReimbRequest newEntry) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	public void approve(int rr_id, int resolverId) throws SQLException {
		CallableStatement cs = conn.prepareCall("call approve_request(?,?)");
		cs.setInt(1, rr_id);
		cs.setInt(2, resolverId);
		cs.executeUpdate();
	}
	public void deny(int rr_id, int resolverId, String reason) throws SQLException {
		CallableStatement cs = conn.prepareCall("call deny_request(?,?,?)");
		cs.setInt(1, rr_id);
		cs.setInt(2, resolverId);
		cs.setString(3, reason);
		cs.executeUpdate();
	}
	public void delete(int id) throws SQLException {
		String sql = "delete from r_requests where rr_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, id);
		ps.executeUpdate();
	}
	public ArrayList<ReimbRequest> getRRs(Integer id, String... args) throws SQLException {
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
		
		String s = "    select sub_query.*, employees.first_name || ' ' || employees.last_name as submitter_name from" + 
				"    ( select r.*," + 
				"        (case when e.first_name is null and e.last_name is null " +
				" 			then null  else  e.first_name || ' ' || e.last_name end)  as resolver_name" + 
				"        from r_requests r left join employees e on r.resolver_id=e.emp_id" + 
				"    ) sub_query" + 
				"    join employees on sub_query.emp_id=employees.emp_id";
		
		StringBuilder sb = new StringBuilder(s);
		if(id != null || args.length >=2) {
			sb.append(" where ");
			if(id != null && args.length >= 2) {
				sb.append("sub_query.emp_id = ? and sub_query" + args[0] + " = ?");
			} else {
				if(id != null)
					sb.append("sub_query.emp_id = ?");
				else if (args.length >= 2) {
					sb.append(args[0] + " = ?");
				}
			}
		}
		String sql = sb.toString();
		
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
			Status status = Status.valueOf(result.getString("status").trim());
			int resolver_id = result.getInt("resolver_id");
			Timestamp date_submitted = result.getTimestamp("date_submitted");
			Timestamp date_resolved = result.getTimestamp("date_resolved");
			String submitterName = result.getString("submitter_name");
			String resolverName = result.getString("resolver_name");
			String reason = result.getString("reason");
			String r_reason = result.getString("rejection_reason");
			RRs.add( new ReimbRequest(rr_id, emp_id, amount, status, submitterName,
					resolverName, date_submitted, date_resolved, reason, r_reason));
		}
		return RRs;
	}
	
	
}
