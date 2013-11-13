package org.openiam.bpm.activiti.delegate.user.edit;

import java.util.ArrayList;
import java.util.Collection;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class SendEditUserRequestDelegate extends AbstractEntitlementsDelegate {
	
	public SendEditUserRequestDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
	}
}
