package org.openiam.bpm.activiti.delegate.entitlements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class SendUserCentricEntitlementsRequestDelegate extends AbstractEntitlementsDelegate {
	
	public SendUserCentricEntitlementsRequestDelegate() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
	}
	
	@Override
	protected String getTargetUserId(final DelegateExecution execution) {
		return (String)execution.getVariable(ActivitiConstants.MEMBER_ASSOCIATION_ID);
	}
}
