package org.openiam.bpm.activiti.delegate.user.edit.displaymapper;

import java.util.LinkedHashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.openiam.bpm.activiti.delegate.user.displaymapper.AbstractUserDisplayMapper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

public class EditUserDisplayMapperDelegate extends AbstractUserDisplayMapper{

	@Autowired
	@Qualifier("provRequestService")
	private RequestDataService provRequestService;
	
	public EditUserDisplayMapperDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String provisionRequestId = (String)execution.getVariable(ActivitiConstants.PROVISION_REQUEST_ID.getName());
		final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(provisionRequestId);
		final UserProfileRequestModel profile = (UserProfileRequestModel)new XStream().fromXML(provisionRequest.getRequestXML());
		final LinkedHashMap<String, String> metadataMap = getMetadataMap(profile, execution);
		
		execution.setVariable(ActivitiConstants.REQUEST_METADATA_MAP.getName(), metadataMap);
	}
}
