package org.openiam.bpm.activiti.tasklistener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

public class NewUserCandidateTaskListener extends AbstractCandidateTaskListener {
	
	public NewUserCandidateTaskListener() {
		super();
	}

	@Override
	public void notify(DelegateTask delegateTask) {
		final NewUserProfileRequestModel profileModel = getObjectVariable(delegateTask.getExecution(), ActivitiConstants.REQUEST, NewUserProfileRequestModel.class);
		
		super.notify(delegateTask, profileModel.getSupervisorIds());
	}

	@Override
	public String getTargetUserId(DelegateExecution execution) {
		return null;
	}

}
