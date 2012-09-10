package org.openiam.bpm.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestorInformation", propOrder = {
    "login",
    "domain",
    "ip",
    "callerUserId"
})
public class RequestorInformation implements Serializable {

	private String login;
	private String domain;
	private String ip;
	private String callerUserId;
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getCallerUserId() {
		return callerUserId;
	}
	
	public void setCallerUserId(String callerUserId) {
		this.callerUserId = callerUserId;
	}
}
