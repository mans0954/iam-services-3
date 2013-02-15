package org.openiam.am.srvc.uriauth.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

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
	"requiredAuthLevel",
	"ruleTokenList"
})
public class URIFederationResponse extends Response {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer requiredAuthLevel;
	private List<URIPatternRuleToken> ruleTokenList;
	
	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public Integer getRequiredAuthLevel() {
		return requiredAuthLevel;
	}

	public void setRequiredAuthLevel(Integer requiredAuthLevel) {
		this.requiredAuthLevel = requiredAuthLevel;
	}
	
	public void addRuleToken(final URIPatternRuleToken token) {
		if(token != null) {
			if(ruleTokenList == null) {
				this.ruleTokenList = new LinkedList<URIPatternRuleToken>();
			}
			this.ruleTokenList.add(token);
		}
	}

	@Override
	public String toString() {
		return String
				.format("URIFederationResponse [requiredAuthLevel=%s, ruleTokenList=%s]",
						requiredAuthLevel, ruleTokenList);
	}
	
	
}
