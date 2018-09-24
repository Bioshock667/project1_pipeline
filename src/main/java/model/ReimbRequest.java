package model;

import java.sql.Timestamp;

public class ReimbRequest {
	private int id;
	private int empID;
	private double amount;
	private Status status;
	private int resolver_id;
	private Timestamp dateSubmitted;
	private Timestamp dateResolved;
	private String reason;
	private String reject_reason;

	public ReimbRequest(int id, int empID, double amount, Status status, int resolver_id, Timestamp dateSubmitted,
			Timestamp dateResolved, String reason, String reject_reason) {
		super();
		this.id = id;
		this.empID = empID;
		this.amount = amount;
		this.status = status;
		this.resolver_id = resolver_id;
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
	public int getResolver_id() {
		return resolver_id;
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
