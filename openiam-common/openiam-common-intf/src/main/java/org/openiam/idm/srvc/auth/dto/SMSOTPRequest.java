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
        "smsCode"
})
public class SMSOTPRequest {
	private String userId;
	private Phone phone;
	private String patternId;
	private String smsCode;
	
	public SMSOTPRequest() {}

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

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public String getPatternId() {
		return patternId;
	}

	public void setPatternId(String patternId) {
		this.patternId = patternId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((patternId == null) ? 0 : patternId.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((smsCode == null) ? 0 : smsCode.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		SMSOTPRequest other = (SMSOTPRequest) obj;
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
		if (smsCode == null) {
			if (other.smsCode != null)
				return false;
		} else if (!smsCode.equals(other.smsCode))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SMSOTPRequest [userId=" + userId + ", phone=" + phone
				+ ", patternId=" + patternId + ", smsCode=" + smsCode + "]";
	}

	
}
