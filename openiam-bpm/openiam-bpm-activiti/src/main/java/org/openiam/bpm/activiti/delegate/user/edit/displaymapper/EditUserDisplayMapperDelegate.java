package org.openiam.bpm.activiti.delegate.user.edit.displaymapper;

import java.util.LinkedHashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.openiam.bpm.activiti.delegate.user.displaymapper.AbstractUserDisplayMapper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

public class EditUserDisplayMapperDelegate extends AbstractUserDisplayMapper{
	
	public EditUserDisplayMapperDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final UserProfileRequestModel profile = getObjectVariable(execution, ActivitiConstants.REQUEST, UserProfileRequestModel.class);
		final LinkedHashMap<String, String> metadataMap = getMetadataMap(profile, execution);
		
		execution.setVariable(ActivitiConstants.REQUEST_METADATA_MAP.getName(), metadataMap);
	}
}
