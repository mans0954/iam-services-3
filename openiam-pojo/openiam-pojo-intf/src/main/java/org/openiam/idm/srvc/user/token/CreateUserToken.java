package org.openiam.idm.srvc.user.token;

import org.openiam.idm.srvc.user.domain.UserEntity;

public class CreateUserToken {

	private UserEntity user;
	private String password;
	private String login;
	public UserEntity getUser() {
		return user;
	}
	public void setUser(UserEntity user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	
}
