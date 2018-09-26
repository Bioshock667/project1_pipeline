package model;

public class Employee {

	private int id;
	private String userName;

	private String password;
	private String firstName;
	private String lastName;
	private boolean isManager;
	
	public Employee(int id, String userName, String password, String firstName, String lastName, boolean isManager) {
		super();
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.isManager = isManager;
	}
	/**
	 * compares every property but password to determine equality
	 * @param other
	 * @return
	 */
	public boolean equals(Employee other) {
		if(this.firstName == null || other.firstName == null)
			return false;
		if(this.lastName == null ||other.lastName == null)
			return false;
		if(this.userName == null || other.userName == null)
			return false;
		if(!this.firstName.equals(other.firstName))
			return false;
		if(!this.lastName.equals(other.lastName))
			return false;
		if(!this.userName.equals(other.userName))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Employee [id=" + id + ", userName=" + userName + ", password=" + password + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", isManager=" + isManager + "]";
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public boolean isManager() {
		return isManager;
	}
	public void setManager(boolean isManager) {
		this.isManager = isManager;
	}
	public int getId() {
		return id;
	}
}
