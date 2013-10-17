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

	public void removeRequest(String requestId) {
		ProvisionRequestEntity request = requestDao.findById(requestId);
		if(request != null) {
			requestDao.delete(request);
		}

	}

	/*
	public List<ProvisionRequestEntity> search(SearchRequest search) {
		return requestDao.search(search);
	}
	*/
	
	public List<ProvisionRequestEntity> requestByApprover(String approverId, String status) {
		return requestDao.findRequestByApprover(approverId, status);
	}

	public void setRequestStatus(String requestId, String approverId, String status) {
		final ProvisionRequestEntity request = requestDao.findById(requestId);
		request.setStatus(status);
		request.setStatusDate(new Date(System.currentTimeMillis()));
		requestDao.update(request);
	}

	public void updateRequest(ProvisionRequestEntity request) {
		requestDao.merge(request);
	}
	
	private Set<RequestApprover> getApprover(List<ApproverAssociation> approverList,	Supervisor supervisor) {
		Set<RequestApprover> reqApproverList = new HashSet<RequestApprover>();
		
		// look at the first approver to figure the type of approver 
		if (approverList == null || approverList.isEmpty()) {
			 return null;
		}
		ApproverAssociation approver = approverList.get(0);
		AssociationType assocType = approver.getAssociationType();
		if (assocType == null) {
			throw new IllegalArgumentException("Approver association is not defined.");
		}
		
		
		if (AssociationType.SUPERVISOR.equals(assocType)) {

			String supervisorUserId = supervisor.getSupervisor().getUserId();
			RequestApprover app = new RequestApprover();
			
			app.setApproverId(supervisorUserId);
			app.setApproverType("SUPERVISOR");
			app.setApproverLevel(approver.getApproverLevel());
			reqApproverList.add(app);
			return reqApproverList;
		}
		if (AssociationType.GROUP.equals(assocType) || AssociationType.ROLE.equals(assocType)) {

			for ( ApproverAssociation assoc : approverList) {
				RequestApprover app = new RequestApprover();
				app.setApproverType(assocType.getValue());
				app.setApproverId(assoc.getOnApproveEntityId());
				app.setApproverLevel(app.getApproverLevel());
				reqApproverList.add(app);
			}

			return reqApproverList;
		}

		return null;
		
	}
}
