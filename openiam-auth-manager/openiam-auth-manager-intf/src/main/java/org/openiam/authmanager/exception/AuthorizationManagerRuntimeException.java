package org.openiam.authmanager.exception;

public class AuthorizationManagerRuntimeException extends RuntimeException {

	public AuthorizationManagerRuntimeException(final String reason) {
		super(reason);
	}
}
