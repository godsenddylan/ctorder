package net.chetong.order.model;

import java.io.Serializable;

public class SysUserVO implements Serializable {
	private static final long serialVersionUID = 2404704176615791937L;
	
	private String id = null;
	private String username = null;
	private String password = null;
	private String salt = null;
	private String locked = null;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getLocked() {
		return locked;
	}
	public void setLocked(String locked) {
		this.locked = locked;
	}
	
	
}
