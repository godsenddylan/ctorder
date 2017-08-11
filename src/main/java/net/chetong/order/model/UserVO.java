package net.chetong.order.model;

import java.io.Serializable;

public class UserVO implements Serializable {
	private static final long serialVersionUID = -6582106442863175518L;
	
	private String created_by = null;
	private String created_date = null;
	private String updated_by = null;
	private String updated_date = null;
	private String id = null;
	private String name = null;
	private String user_login = null;
	private String pwd_login = null;
	private String birthday = null;
	private String age = null;
	private String mobile = null;
	private String phone = null;
    private String username = null;
    private String password = null;
	
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public String getCreated_date() {
		return created_date;
	}
	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	public String getUpdated_date() {
		return updated_date;
	}
	public void setUpdated_date(String updated_date) {
		this.updated_date = updated_date;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUser_login() {
		return user_login;
	}
	public void setUser_login(String user_login) {
		this.user_login = user_login;
	}
	public String getPwd_login() {
		return pwd_login;
	}
	public void setPwd_login(String pwd_login) {
		this.pwd_login = pwd_login;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getUsername() {
		return user_login;
	}
	public void setUsername(String username){
		this.username =username;
	}
	public String getPassword() {
		return pwd_login;
	}
	public void setPassword(String password){
		this.password =password;
	}
	
}
