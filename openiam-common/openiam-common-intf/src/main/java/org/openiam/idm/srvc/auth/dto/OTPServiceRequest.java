package org.openiam.idm.srvc.auth.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.continfo.dto.Phone;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SMSOTPRequest", propOrder = {
		"userId",
        "phone",
        "patternId",
        "otpCode",
        "requestType",
        "secret"
})
public class OTPServiceRequest {
	private String userId;
	private Phone phone;
	private String patternId;
	private String otpCode;
	private OTPRequestType requestType;
	
	/* if set, it should override secret in DB */
	private String secret;
	
	public OTPServiceRequest() {}

	public Phone getPhone() {
		return phone;
	}

	public void setPhone(Phone phone) {
		this.phone = phone;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOtpCode() {
		return otpCode;
	}

	public void setOtpCode(String otpCode) {
		this.otpCode = otpCode;
	}

	public String getPatternId() {
		return patternId;
	}

	public void setPatternId(String patternId) {
		this.patternId = patternId;
	}

	public OTPRequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(OTPRequestType requestType) {
		this.requestType = requestType;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((patternId == null) ? 0 : patternId.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result
				+ ((requestType == null) ? 0 : requestType.hashCode());
		result = prime * result + ((otpCode == null) ? 0 : otpCode.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((secret == null) ? 0 : secret.hashCode());
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
		OTPServiceRequest other = (OTPServiceRequest) obj;
		if (patternId == null) {
			if (other.patternId != null)
				return false;
		} else if (!patternId.equals(other.patternId))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (requestType != other.requestType)
			return false;
		if (otpCode == null) {
			if (other.otpCode != null)
				return false;
		} else if (!otpCode.equals(other.otpCode))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		
		if (secret == null) {
			if (other.secret != null)
				return false;
		} else if (!secret.equals(other.secret))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OTPServiceRequest [userId=" + userId + ", phone=" + phone
				+ ", patternId=" + patternId + ", otpCode=" + otpCode
				+ ", requestType=" + requestType + "]";
	}

	
}
