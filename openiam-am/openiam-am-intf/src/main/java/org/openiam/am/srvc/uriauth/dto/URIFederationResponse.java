package org.openiam.am.srvc.uriauth.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.dto.ContentProviderServer;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;

/**
 * @author Lev Bornovalov
 * Class used by the Apache Reverse Proxy to digest the result of URI Federation.
 * Be careful when changing this class, as it may have unexpected consequences on the Proxy (which is written in C)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIFederationResponse", propOrder = {
	"ruleTokenList",
	"authLevelTokenList",
	"server",
	"patternId",
	"cpId",
	"loginURL",
	"postbackURLParamName"
})
public class URIFederationResponse extends Response {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<URIAuthLevelToken> authLevelTokenList;
	private List<URIPatternRuleToken> ruleTokenList;
	private ContentProviderServer server;
	private String patternId;
	private String cpId;
	private String loginURL;
	private String postbackURLParamName;
	
	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}
	
	public void addRuleToken(final URIPatternRuleToken token) {
		if(token != null) {
			if(ruleTokenList == null) {
				this.ruleTokenList = new LinkedList<URIPatternRuleToken>();
			}
			this.ruleTokenList.add(token);
		}
	}

	public List<URIAuthLevelToken> getAuthLevelTokenList() {
		return authLevelTokenList;
	}

	public void setAuthLevelTokenList(List<URIAuthLevelToken> authLevelTokenList) {
		this.authLevelTokenList = authLevelTokenList;
	}
	
	public void addAuthLevelToken(final URIAuthLevelToken token) {
		if(token != null) {
			if(authLevelTokenList == null) {
				authLevelTokenList = new LinkedList<URIAuthLevelToken>();
			}
			authLevelTokenList.add(token);
		}
	}

	public ContentProviderServer getServer() {
		return server;
	}

	public void setServer(ContentProviderServer server) {
		this.server = server;
	}

	public List<URIPatternRuleToken> getRuleTokenList() {
		return ruleTokenList;
	}

	public String getPatternId() {
		return patternId;
	}

	public void setPatternId(String patternId) {
		this.patternId = patternId;
	}

	public String getCpId() {
		return cpId;
	}

	public void setCpId(String cpId) {
		this.cpId = cpId;
	}
	
	public String getLoginURL() {
		return loginURL;
	}

	public void setLoginURL(String loginURL) {
		this.loginURL = loginURL;
	}

	public String getPostbackURLParamName() {
		return postbackURLParamName;
	}

	public void setPostbackURLParamName(String postbackURLParamName) {
		this.postbackURLParamName = postbackURLParamName;
	}

	@Override
	public String toString() {
		return String
				.format("URIFederationResponse [ruleTokenList=%s, server=%s, status=%s, errorCode=%s, errorText=%s, responseValue=%s]",
						ruleTokenList, server, status,
						errorCode, errorText, responseValue);
	}

	
	
}
