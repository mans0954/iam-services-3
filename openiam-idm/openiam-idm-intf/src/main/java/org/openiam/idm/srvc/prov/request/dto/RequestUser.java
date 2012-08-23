package org.openiam.idm.srvc.prov.request.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

// Generated Jan 9, 2009 5:33:58 PM by Hibernate Tools 3.2.2.GA

/**
 * RequestUserList generated by hbm2java
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestUser", propOrder = {
    "reqUserListId",
    "requestId",
    "userId",
    "firstName",
    "lastName",
    "middleInit",
    "deptCd",
    "division",
    "locationCd",
    "affiliation"
})
public class RequestUser implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -150644105593130127L;
	private String reqUserListId;
	private String requestId;
	private String userId;
	private String firstName;
	private String lastName;
	private String middleInit;
	private String deptCd;
	private String division;
	private String locationCd;
	private String affiliation;

	public RequestUser() {
	}



	public RequestUser(String affiliation, String deptCd, String division,
			String firstName, String lastName, String locationCd,
			String middleInit, String provRequestId, String reqUserListId,
			String userId) {
		super();
		this.affiliation = affiliation;
		this.deptCd = deptCd;
		this.division = division;
		this.firstName = firstName;
		this.lastName = lastName;
		this.locationCd = locationCd;
		this.middleInit = middleInit;
		this.requestId = provRequestId;
		this.reqUserListId = reqUserListId;
		this.userId = userId;
	}



	public String getReqUserListId() {
		return this.reqUserListId;
	}

	public void setReqUserListId(String reqUserListId) {
		this.reqUserListId = reqUserListId;
	}



	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleInit() {
		return this.middleInit;
	}

	public void setMiddleInit(String middleInit) {
		this.middleInit = middleInit;
	}

	public String getDeptCd() {
		return this.deptCd;
	}

	public void setDeptCd(String deptCd) {
		this.deptCd = deptCd;
	}

	public String getDivision() {
		return this.division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getLocationCd() {
		return this.locationCd;
	}

	public void setLocationCd(String locationCd) {
		this.locationCd = locationCd;
	}

	public String getAffiliation() {
		return this.affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}



	public String getRequestId() {
		return requestId;
	}



	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}




}
