package org.openiam.authmanager.exception;

import org.openiam.authmanager.ws.response.MenuError;

public class AuthorizationMenuException extends Exception {

	private MenuError menuError;
	private String menuName;
	
	public AuthorizationMenuException(final MenuError menuError, final String menuName) {
		this.menuError = menuError;
		this.menuName = menuName;
	}
	
	public MenuError getMenuError() {
		return menuError;
	}
	
	public String getMenuName() {
		return menuName;
	}
}
