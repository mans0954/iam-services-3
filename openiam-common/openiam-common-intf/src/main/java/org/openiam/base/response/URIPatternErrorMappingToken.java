package org.openiam.base.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPatternErrorMappingToken", propOrder = {
	"errorCode",
	"redirectURL"
})
public class URIPatternErrorMappingToken {

	private int errorCode;
	private String redirectURL;
	
	private URIPatternErrorMappingToken() {
		
	}
	
	public URIPatternErrorMappingToken(final int code, final String redirectURL) {
		this.errorCode = code;
		this.redirectURL = redirectURL;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getRedirectURL() {
		return redirectURL;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + errorCode;
		result = prime * result
				+ ((redirectURL == null) ? 0 : redirectURL.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URIPatternErrorMappingToken other = (URIPatternErrorMappingToken) obj;
		if (errorCode != other.errorCode)
			return false;
		if (redirectURL == null) {
			if (other.redirectURL != null)
				return false;
		} else if (!redirectURL.equals(other.redirectURL))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "URIPatternErrorMapping [errorCode=" + errorCode
				+ ", redirectURL=" + redirectURL + "]";
	}
	
	
}
