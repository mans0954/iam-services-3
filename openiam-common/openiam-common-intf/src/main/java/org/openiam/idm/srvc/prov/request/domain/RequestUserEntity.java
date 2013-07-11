package org.openiam.idm.srvc.prov.request.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.prov.request.dto.RequestUser;

@Entity
@Table(name = "REQUEST_USER")
@DozerDTOCorrespondence(RequestUser.class)
public class RequestUserEntity {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "REQ_USER_ID", length = 32)
	private String id;
	
	@Column(name = "REQUEST_ID", length = 32)
	private String requestId;
	
	@Column(name = "USER_ID", length = 32)
	private String userId;
	
	@Column(name = "FIRST_NAME", length = 20)
	private String firstName;
	
	@Column(name = "LAST_NAME", length = 20)
	private String lastName;
	
	@Column(name = "MIDDLE_INIT", length = 20)
	private String middleInit;
	
	@Column(name = "LOCATION_CD", length = 20)
	private String locationCd;
	
	@Column(name = "AFFILIATION", length = 20)
	private String affiliation;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMiddleInit() {
		return middleInit;
	}
	public void setMiddleInit(String middleInit) {
		this.middleInit = middleInit;
	}
	public String getLocationCd() {
		return locationCd;
	}
	public void setLocationCd(String locationCd) {
		this.locationCd = locationCd;
	}
	public String getAffiliation() {
		return affiliation;
	}
	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((affiliation == null) ? 0 : affiliation.hashCode());
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result
				+ ((locationCd == null) ? 0 : locationCd.hashCode());
		result = prime * result
				+ ((middleInit == null) ? 0 : middleInit.hashCode());
		result = prime * result
				+ ((requestId == null) ? 0 : requestId.hashCode());
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
		RequestUserEntity other = (RequestUserEntity) obj;
		if (affiliation == null) {
			if (other.affiliation != null)
				return false;
		} else if (!affiliation.equals(other.affiliation))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (locationCd == null) {
			if (other.locationCd != null)
				return false;
		} else if (!locationCd.equals(other.locationCd))
			return false;
		if (middleInit == null) {
			if (other.middleInit != null)
				return false;
		} else if (!middleInit.equals(other.middleInit))
			return false;
		if (requestId == null) {
			if (other.requestId != null)
				return false;
		} else if (!requestId.equals(other.requestId))
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
		return "RequestUserEntity [id=" + id + ", requestId=" + requestId
				+ ", userId=" + userId + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", middleInit=" + middleInit
				+ ", locationCd=" + locationCd + ", affiliation=" + affiliation
				+ "]";
	}
	
	
	
}
