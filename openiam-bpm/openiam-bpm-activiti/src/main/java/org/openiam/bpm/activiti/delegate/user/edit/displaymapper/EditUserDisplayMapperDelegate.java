package org.openiam.bpm.activiti.delegate.user.edit.displaymapper;

import java.util.LinkedHashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.openiam.bpm.activiti.delegate.user.displaymapper.AbstractUserDisplayMapper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.util.SpringContextProvider;

import com.thoughtworks.xstream.XStream;

public class EditUserDisplayMapperDelegate extends AbstractUserDisplayMapper implements JavaDelegate {

	public EditUserDisplayMapperDelegate() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final UserProfileRequestModel request = (UserProfileRequestModel)new XStream().fromXML((String)execution.getVariable(ActivitiConstants.USER_PROFILE));
		final LinkedHashMap<String, String> metadataMap = getMetadataMap(request, execution);
		
		execution.setVariable(ActivitiConstants.REQUEST_METADATA_MAP, metadataMap);
	}
}
