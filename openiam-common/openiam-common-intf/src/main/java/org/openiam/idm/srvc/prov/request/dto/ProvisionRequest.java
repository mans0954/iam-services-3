package org.openiam.idm.srvc.prov.request.dto;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvisionRequest", propOrder = {
    "id",
    "requestorId",
    "requestDate",
    "status",
    "statusDate",
    "requestReason",
    "requestType",
    "requestXML",
    "managedResourceId",
    "changeAccessBy",
    "newRoleId",
    "newServiceId",
    "requestApprovers",
    "requestUsers"
})
@DozerDTOCorrespondence(ProvisionRequestEntity.class)
public class ProvisionRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5432383771223874649L;
	private String id;
	private String requestorId;
	private Date requestDate;
	private String status;
	private Date statusDate;
	private String requestReason;
	private String requestType;
	private String requestXML;
	private String managedResourceId;
	
	private String changeAccessBy;
	private String newRoleId;
	private String newServiceId;
	
	private Set<RequestUser> requestUsers = new HashSet<RequestUser>(0);
	private Set<RequestApprover> requestApprovers = new HashSet<RequestApprover>(0);

	public ProvisionRequest() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRequestorId() {
		return this.requestorId;
	}

	public void setRequestorId(String requestorId) {
		this.requestorId = requestorId;
	}

	public Date getRequestDate() {
		return this.requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStatusDate() {
		return this.statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public String getRequestReason() {
		return this.requestReason;
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

	public Set<RequestApprover> getRequestApprovers() {
		return requestApprovers;
	}

	public void setRequestApprovers(Set<RequestApprover> requestApprovers) {
		this.requestApprovers = requestApprovers;
	}



	public String getChangeAccessBy() {
		return changeAccessBy;
	}

	public void setChangeAccessBy(String changeAccessBy) {
		this.changeAccessBy = changeAccessBy;
	}

	public String getRequestXML() {
		return requestXML;
	}

	public void setRequestXML(String requestXML) {
		this.requestXML = requestXML;
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

	public String getManagedResourceId() {
		return managedResourceId;
	}

	public void setManagedResourceId(String managedResourceId) {
		this.managedResourceId = managedResourceId;
	}

	public Set<RequestUser> getRequestUsers() {
		return requestUsers;
	}

	public void setRequestUsers(Set<RequestUser> requestUsers) {
		this.requestUsers = requestUsers;
	}
}
