package org.openiam.exception;

import org.openiam.base.ws.ResponseCode;

public class AuthorizationMenuException extends BasicDataServiceException {

	private String menuName;
	
	public AuthorizationMenuException(final ResponseCode code, final String menuName) {
		super(code);
		this.menuName = menuName;
	}
	
	public String getMenuName() {
		return menuName;
	}
}
