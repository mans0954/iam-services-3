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
	 * Returns a request
	 * @param requestId
	 * @return
	 */
	ProvisionRequestEntity getRequest(String requestId);
}
