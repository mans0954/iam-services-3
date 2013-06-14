package org.openiam.bpm.activiti.delegate.entitlements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.el.FixedValue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.activiti.delegate.core.AbstractDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.grp.service.UserGroupDAO;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.role.service.UserRoleDAO;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.service.UserMgr;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractEntitlementsDelegate extends AbstractDelegate {
	
	private FixedValue operation;

	protected AbstractEntitlementsDelegate() {
		super();
	}
	
	public String getOperation() {
		return (operation != null) ? StringUtils.trimToNull(operation.getExpressionText()) : null;
	}
}
