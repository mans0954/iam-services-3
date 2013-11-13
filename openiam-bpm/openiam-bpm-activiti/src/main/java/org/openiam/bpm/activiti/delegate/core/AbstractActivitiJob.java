package org.openiam.bpm.activiti.delegate.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.bpm.activiti.model.ActivitiJSONStringWrapper;
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
import org.openiam.idm.util.CustomJacksonMapper;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractActivitiJob implements JavaDelegate, TaskListener {
	
	protected static Logger LOG = Logger.getLogger(AbstractActivitiJob.class);
	
	@Autowired
	protected MailService mailService;
	
	@Autowired
	protected ActivitiHelper activitiHelper;

	@Autowired
	@Qualifier("defaultProvision")
	protected ProvisionService provisionService;

	@Autowired
	protected UserDataService userDataService;
	

	@Autowired
	private CustomJacksonMapper customJacksonMapper;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		throw new RuntimeException("notify() not overridden");
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		throw new RuntimeException("execute() not overridden");
	}
	
	protected AbstractActivitiJob() {
		SpringContextProvider.autowire(this);
		SpringContextProvider.resolveProperties(this);
	}
	
	protected UserEntity getUserEntity(final String userId) {
		return userDataService.getUser(userId);
	}
	

	protected User getUser(final String userId) {
		return userDataService.getUserDto(userId);
	}
	
	protected List<String> getSupervisorsForUser(final UserEntity  user) {
		final List<String> supervisorIds = new LinkedList<String>();
		if(user != null) {
			if(CollectionUtils.isNotEmpty(user.getSupervisors())) {
				for(final SupervisorEntity supervisor : user.getSupervisors()) {
					if(supervisor != null && supervisor.getSupervisor() != null) {
						supervisorIds.add(supervisor.getSupervisor().getUserId());
					}
				}
			}
		}
		return supervisorIds;
	}
	
	public <T extends Object> T getObjectVariable(final DelegateExecution execution, final ActivitiConstants key, final Class<T> clazz) {
		final Object obj = execution.getVariable(key.getName());
		return (obj instanceof ActivitiJSONStringWrapper) ? ((ActivitiJSONStringWrapper)obj).getObject(key.getName(), customJacksonMapper, clazz) : null;
	}
	
	public String getStringVariable(final DelegateExecution execution, final ActivitiConstants key) {
		try {
			return (String)execution.getVariable(key.getName());
		} catch(Throwable e) {
			LOG.warn(String.format("Can't get variable '%s", key), e);
			return null;
		}
	}
	
	public String getRequestorId(final DelegateExecution execution) {
		return getStringVariable(execution, ActivitiConstants.REQUESTOR);
	}
}
