package model;

import java.sql.Timestamp;

public class ReimbRequest {
	private int id;
	private int empID;
	private double amount;
	private Status status;
	private String submitterName;
	private String resolverName;
	private Timestamp dateSubmitted;
	private Timestamp dateResolved;
	private String reason;
	private String reject_reason;

	public ReimbRequest(int id, int empID, double amount, Status status, String sName, String rName, Timestamp dateSubmitted,
			Timestamp dateResolved, String reason, String reject_reason) {
		super();
		this.id = id;
		this.empID = empID;
		this.amount = amount;
		this.status = status;
		this.submitterName = sName;
		this.resolverName = rName;
		this.dateSubmitted = dateSubmitted;
		this.dateResolved = dateResolved;
		this.reason = reason;
		this.reject_reason = reject_reason;
	}
	public int getEmpID() {
		return empID;
	}
	public int getId() {
		return id;
	}
	public double getAmount() {
		return amount;
	}
	public String getReason() {
		return reason;
	}
	
	public String getSubmitterName() {
		return submitterName;
	}
	public String getResolverName() {
		return resolverName;
	}
	public Status getStatus() {
		return status;
	}
	public Timestamp getDateSubmitted() {
		return dateSubmitted;
	}
	public Timestamp getDateResolved() {
		return dateResolved;
	}
	public String getReject_reason() {
		return reject_reason;
	}
	
}
