package org.openiam.idm.srvc.prov.request.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.dto.RequestApprover;
import org.openiam.idm.srvc.prov.request.dto.SearchRequest;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/*
 * Service implementation to manage provisioning requests
 */
@Service("provRequestService")
public class RequestDataServiceImpl implements RequestDataService {
	private static final Log log = LogFactory.getLog(RequestDataServiceImpl.class);
	
	@Autowired
	private ProvisionRequestDAO requestDao;
	
	public void addRequest(ProvisionRequestEntity request) {
		requestDao.add(request);

	}

	public ProvisionRequestEntity getRequest(String requestId) {
		return requestDao.findById(requestId);
	}

	public void updateRequest(ProvisionRequestEntity request) {
		requestDao.merge(request);
	}
}
