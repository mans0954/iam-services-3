package org.openiam.idm.srvc.prov.request.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.dto.RequestApprover;
import org.openiam.idm.srvc.prov.request.dto.RequestUser;

@Entity
@Table(name = "PROV_REQUEST")
@DozerDTOCorrespondence(ProvisionRequest.class)
public class ProvisionRequestEntity {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "REQUEST_ID", length = 32)
	private String id;
	
	@Column(name = "REQUESTOR_ID", length = 20)
	private String requestorId;
	
	@Column(name="REQUEST_DATE",length=19)
    @Temporal(TemporalType.TIMESTAMP)
	private Date requestDate;
	
	@Column(name = "STATUS", length = 20)
	private String status;
	
	@Column(name="STATUS_DATE",length=19)
    @Temporal(TemporalType.TIMESTAMP)
	private Date statusDate;
	
	@Column(name = "REQUEST_REASON", length = 500)
	private String requestReason;
	
	@Column(name = "REQUEST_TYPE", length = 20)
	private String requestType;

    @Lob
	@Column(name = "REQUEST_XML")
	private String requestXML;
	
	@Column(name = "MANAGED_RESOURCE_ID", length = 32)
	private String managedResourceId;
	
	@Column(name = "CHANGE_ACCESS_BY", length = 20)
	private String changeAccessBy;
	
	@Column(name = "NEW_ROLE_ID", length = 32)
	private String newRoleId;
	
	@Column(name = "NEW_SERVICE_ID", length = 20)
	private String newServiceId;
	
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name="REQUEST_ID", referencedColumnName="REQUEST_ID")
    @Fetch(FetchMode.SUBSELECT)
	private Set<RequestUserEntity> requestUsers;
	
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name="REQUEST_ID", referencedColumnName="REQUEST_ID")
    @Fetch(FetchMode.SUBSELECT)
	private Set<RequestApproverEntity> requestApprovers;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRequestorId() {
		return requestorId;
	}

	public void setRequestorId(String requestorId) {
		this.requestorId = requestorId;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public String getRequestReason() {
		return requestReason;
	}

	public void setRequestReason(String requestReason) {
		this.requestReason = requestReason;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getRequestXML() {
		return requestXML;
	}

	public void setRequestXML(String requestXML) {
		this.requestXML = requestXML;
	}

	public String getManagedResourceId() {
		return managedResourceId;
	}

	public void setManagedResourceId(String managedResourceId) {
		this.managedResourceId = managedResourceId;
	}

	public String getChangeAccessBy() {
		return changeAccessBy;
	}

	public void setChangeAccessBy(String changeAccessBy) {
		this.changeAccessBy = changeAccessBy;
	}

	public String getNewRoleId() {
		return newRoleId;
	}

	public void setNewRoleId(String newRoleId) {
		this.newRoleId = newRoleId;
	}

	public String getNewServiceId() {
		return newServiceId;
	}

	public void setNewServiceId(String newServiceId) {
		this.newServiceId = newServiceId;
	}

	public Set<RequestUserEntity> getRequestUsers() {
		return requestUsers;
	}

	public void setRequestUsers(Set<RequestUserEntity> requestUsers) {
		this.requestUsers = requestUsers;
	}

	public Set<RequestApproverEntity> getRequestApprovers() {
		return requestApprovers;
	}
	
	public void addRequestApprover(final RequestApproverEntity approver) {
		if(this.requestApprovers == null) {
			this.requestApprovers = new HashSet<RequestApproverEntity>();
		}
		this.requestApprovers.add(approver);
	}

	public void setRequestApprovers(Set<RequestApproverEntity> requestApprovers) {
		this.requestApprovers = requestApprovers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((changeAccessBy == null) ? 0 : changeAccessBy.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((managedResourceId == null) ? 0 : managedResourceId
						.hashCode());
		result = prime * result
				+ ((newRoleId == null) ? 0 : newRoleId.hashCode());
		result = prime * result
				+ ((newServiceId == null) ? 0 : newServiceId.hashCode());
		result = prime * result
				+ ((requestDate == null) ? 0 : requestDate.hashCode());
		result = prime * result
				+ ((requestReason == null) ? 0 : requestReason.hashCode());
		result = prime * result
				+ ((requestType == null) ? 0 : requestType.hashCode());
		result = prime * result
				+ ((requestXML == null) ? 0 : requestXML.hashCode());
		result = prime * result
				+ ((requestorId == null) ? 0 : requestorId.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((statusDate == null) ? 0 : statusDate.hashCode());
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
		ProvisionRequestEntity other = (ProvisionRequestEntity) obj;
		if (changeAccessBy == null) {
			if (other.changeAccessBy != null)
				return false;
		} else if (!changeAccessBy.equals(other.changeAccessBy))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (managedResourceId == null) {
			if (other.managedResourceId != null)
				return false;
		} else if (!managedResourceId.equals(other.managedResourceId))
			return false;
		if (newRoleId == null) {
			if (other.newRoleId != null)
				return false;
		} else if (!newRoleId.equals(other.newRoleId))
			return false;
		if (newServiceId == null) {
			if (other.newServiceId != null)
				return false;
		} else if (!newServiceId.equals(other.newServiceId))
			return false;
		if (requestDate == null) {
			if (other.requestDate != null)
				return false;
		} else if (!requestDate.equals(other.requestDate))
			return false;
		if (requestReason == null) {
			if (other.requestReason != null)
				return false;
		} else if (!requestReason.equals(other.requestReason))
			return false;
		if (requestType == null) {
			if (other.requestType != null)
				return false;
		} else if (!requestType.equals(other.requestType))
			return false;
		if (requestXML == null) {
			if (other.requestXML != null)
				return false;
		} else if (!requestXML.equals(other.requestXML))
			return false;
		if (requestorId == null) {
			if (other.requestorId != null)
				return false;
		} else if (!requestorId.equals(other.requestorId))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (statusDate == null) {
			if (other.statusDate != null)
				return false;
		} else if (!statusDate.equals(other.statusDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProvisionRequestEntity [id=" + id + ", requestorId="
				+ requestorId + ", requestDate=" + requestDate + ", status="
				+ status + ", statusDate=" + statusDate + ", requestReason="
				+ requestReason + ", requestType=" + requestType
				+ ", requestXML=" + requestXML + ", managedResourceId="
				+ managedResourceId + ", changeAccessBy=" + changeAccessBy
				+ ", newRoleId=" + newRoleId + ", newServiceId=" + newServiceId + "]";
	}
	
	
}
