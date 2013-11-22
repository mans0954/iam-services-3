package org.openiam.idm.srvc.prov.request.service;

import java.util.List;

import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.dto.SearchRequest;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
/*
 * Service interface to manage provisioning requests
 */
public interface RequestDataService {

	void addRequest(ProvisionRequestEntity request);
	void updateRequest(ProvisionRequestEntity request);
	/**
	 * Removes a request from the system.
	 * @param requestId
	 */
	void removeRequest(String requestId);
	
	/**
	 * Sets the status of the request.
	 * @param requestId
	 * @param approverId - The person who changed the request
	 * @param status - New status of the request
	 */
	void setRequestStatus(String requestId, String approverId, String status);
	
	/**
	 * Returns a request
	 * @param requestId
	 * @return
	 */
	ProvisionRequestEntity getRequest(String requestId);
	
	/**
	 * Method to carry out adhoc search;
	 * @param search
	 * @return
	 */
	//List<ProvisionRequestEntity> search(SearchRequest search);
	
	List<ProvisionRequestEntity> requestByApprover(String approverId, String status);
}
