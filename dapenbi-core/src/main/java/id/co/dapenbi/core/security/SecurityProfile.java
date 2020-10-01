package id.co.dapenbi.core.security;

import java.io.Serializable;
import java.util.List;

public class SecurityProfile implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long userId;
	private String userName;
	private String fullName;

	private List<String> roles;
	private Security security;
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public Security getSecurity() {
		return security;
	}
	public void setSecurity(Security security) {
		this.security = security;
	}
	@Override
	public String toString() {
		return "SecurityProfile [userId=" + userId + ", userName=" + userName
				+ ", fullName=" + fullName + ", employeeNumber="
				+ ", roles=" + roles + ", security=" + security + "]";
	}	
}
