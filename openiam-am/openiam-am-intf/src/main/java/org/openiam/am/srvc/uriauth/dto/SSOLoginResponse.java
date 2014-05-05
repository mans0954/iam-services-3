package org.openiam.am.srvc.uriauth.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.auth.dto.SSOToken;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SSOLoginResponse", propOrder = {
        "ssoToken",
        "loginError",
        "openiamPrincipal"
})
public class SSOLoginResponse extends Response {

	private SSOToken ssoToken;
	private Integer loginError;
	private String openiamPrincipal;
	
	public SSOLoginResponse() {
		super();
	}
	
	public SSOLoginResponse(final ResponseStatus status) {
		super(status);
	}

	public SSOToken getSsoToken() {
		return ssoToken;
	}

	public void setSsoToken(SSOToken ssoToken) {
		this.ssoToken = ssoToken;
	}

	public Integer getLoginError() {
		return loginError;
	}

	public void setLoginError(Integer loginError) {
		this.loginError = loginError;
	}

	public String getOpeniamPrincipal() {
		return openiamPrincipal;
	}

	public void setOpeniamPrincipal(String openiamPrincipal) {
		this.openiamPrincipal = openiamPrincipal;
	}
	
	
}
