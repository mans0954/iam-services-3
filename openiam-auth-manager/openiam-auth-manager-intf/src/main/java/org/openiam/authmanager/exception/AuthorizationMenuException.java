package org.openiam.authmanager.exception;

import org.openiam.base.ws.ResponseCode;

public class AuthorizationMenuException extends Exception {

	private ResponseCode code;
	private String menuName;
	
	public AuthorizationMenuException(final ResponseCode code, final String menuName) {
		this.code = code;
		this.menuName = menuName;
	}
	
	public ResponseCode getResponseCode() {
		return code;
	}
	
	public String getMenuName() {
		return menuName;
	}
}
