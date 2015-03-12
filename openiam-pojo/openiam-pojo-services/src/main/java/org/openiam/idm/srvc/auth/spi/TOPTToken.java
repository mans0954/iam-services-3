package org.openiam.idm.srvc.auth.spi;

public class TOPTToken {

	private String secret;
	private int code;
	
	private TOPTToken() {}
	
	public TOPTToken(final String secret, final int code) {
		this.secret = secret;
		this.code = code;
	}

	public String getSecret() {
		return secret;
	}
	
	public int getCode() {
		return code;
	}
}
