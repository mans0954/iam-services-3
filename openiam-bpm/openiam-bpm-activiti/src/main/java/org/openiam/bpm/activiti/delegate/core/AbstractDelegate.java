package org.openiam.bpm.activiti.delegate.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractDelegate implements JavaDelegate {
	
	protected static Logger LOG = Logger.getLogger(AbstractDelegate.class);
	
	@Autowired
	protected MailService mailService;
	
	@Autowired
	protected ActivitiHelper activitiHelper;

	@Autowired
	@Qualifier("defaultProvision")
	protected ProvisionService provisionService;

	@Autowired
	protected UserDataService userDataService;
	
	protected AbstractDelegate() {
		SpringContextProvider.autowire(this);
	}
	
	protected UserEntity getUserEntity(final String userId) {
		return userDataService.getUser(userId);
	}
	

	protected User getUser(final String userId) {
		return userDataService.getUserDto(userId);
	}
}
