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

import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.ContentProviderServer;
import org.openiam.am.srvc.dto.RoundRobinServer;
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
	"authCookieName",
	"authCookieDomain",
	"methodId",
	"redirectTo",
	"substitutionList",
	"errorMappingList",
	"loginURL",
	"postbackURLParamName",
	"cacheable",
	"cacheTTL",
	"configured"
})
public class URIFederationResponse extends Response {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<URIPatternErrorMappingToken> errorMappingList;
	private List<URISubstitutionToken> substitutionList;
	private List<URIAuthLevelToken> authLevelTokenList;
	private List<URIPatternRuleToken> ruleTokenList;
	private RoundRobinServer server;
	private String patternId;
	private String cpId;
	private String loginURL;
	private String postbackURLParamName;
	private String authCookieName;
	private String authCookieDomain;
	private String methodId;
	private String redirectTo;
	private boolean cacheable;
	private Integer cacheTTL;
	private boolean configured;
	
	public URIFederationResponse() {}
	
	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}
	
	public List<URIPatternErrorMappingToken> getErrorMappingList() {
		return errorMappingList;
	}
	
	public void addErrorMapping(final int code, final String redirectURL) {
		if(redirectURL != null) {
			if(this.errorMappingList == null) {
				this.errorMappingList = new LinkedList<URIPatternErrorMappingToken>();
			}
			this.errorMappingList.add(new URIPatternErrorMappingToken(code, redirectURL));
		}
	}

	public List<URISubstitutionToken> getSubstitutionList() {
		return substitutionList;
	}
	
	public void addSubstitution(final URISubstitutionToken token) {
		if(token != null) {
			if(this.substitutionList == null) {
				this.substitutionList = new LinkedList<URISubstitutionToken>();
			}
			this.substitutionList.add(token);
		}
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
			if(authLevelTokenList.size() == 0 || !authLevelTokenList.contains(token)) {
				authLevelTokenList.add(token);
			}
		}
	}

	public RoundRobinServer getServer() {
		return server;
	}

	public void setServer(final RoundRobinServer server) {
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

	public String getAuthCookieName() {
		return authCookieName;
	}

	public void setLoginURL(String loginURL) {
		this.loginURL = loginURL;
	}
	
	public void setAuthCookieName(String authCookieName) {
		this.authCookieName = authCookieName;
	}

	public String getPostbackURLParamName() {
		return postbackURLParamName;
	}
	
	public String getAuthCookieDomain() {
		return authCookieDomain;
	}

	public void setPostbackURLParamName(String postbackURLParamName) {
		this.postbackURLParamName = postbackURLParamName;
	}
	
	public void setAuthCookieDomain(String authCookieDomain) {
		this.authCookieDomain = authCookieDomain;
	}

	public String getMethodId() {
		return methodId;
	}

	public void setMethodId(String methodId) {
		this.methodId = methodId;
	}
	
	public String getRedirectTo() {
		return redirectTo;
	}

	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}
	
	public boolean isCacheable() {
		return cacheable;
	}

	public void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
	}

	public Integer getCacheTTL() {
		return cacheTTL;
	}

	public void setCacheTTL(Integer cacheTTL) {
		this.cacheTTL = cacheTTL;
	}
	
	

	public boolean isConfigured() {
		return configured;
	}

	public void setConfigured(boolean configured) {
		this.configured = configured;
	}

	@Override
	public String toString() {
		return String
				.format("URIFederationResponse [ruleTokenList=%s, server=%s, status=%s, errorCode=%s, errorText=%s, responseValue=%s, redirectTo=%s]",
						ruleTokenList, server, status, errorCode, errorText, responseValue, redirectTo);
	}

	
	
}
