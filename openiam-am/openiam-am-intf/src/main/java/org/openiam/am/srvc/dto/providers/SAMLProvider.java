package org.openiam.am.srvc.dto.providers;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SAMLProvider", propOrder = {
	"assertionConsumerURL",
	"requestIssuer",
	"responseIssuer",
	"publicKey",
	"privateKey",
	"signResponse",
	"audiences"
})
public class SAMLProvider extends AuthenticationProvider implements Serializable {

	private String assertionConsumerURL;
	private String requestIssuer;
	private String responseIssuer;
	private byte[] publicKey;
	private byte[] privateKey;
	private boolean signResponse;
	private Set<String> audiences;

	public String getAssertionConsumerURL() {
		return assertionConsumerURL;
	}

	public void setAssertionConsumerURL(String assertionConsumerURL) {
		this.assertionConsumerURL = assertionConsumerURL;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}

	public byte[] getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(byte[] privateKey) {
		this.privateKey = privateKey;
	}

	public boolean isSignResponse() {
		return signResponse;
	}

	public void setSignResponse(boolean signResponse) {
		this.signResponse = signResponse;
	}

	public String getRequestIssuer() {
		return requestIssuer;
	}

	public void setRequestIssuer(String requestIssuer) {
		this.requestIssuer = requestIssuer;
	}

	public Set<String> getAudiences() {
		return audiences;
	}

	public void setAudiences(Set<String> audienceList) {
		this.audiences = audienceList;
	}
	
	public void addAudience(final String audience) {
		if(this.audiences == null) {
			this.audiences = new LinkedHashSet<String>();
		}
		this.audiences.add(audience);
	}

	public String getResponseIssuer() {
		return responseIssuer;
	}

	public void setResponseIssuer(String responseIssuer) {
		this.responseIssuer = responseIssuer;
	}
}
