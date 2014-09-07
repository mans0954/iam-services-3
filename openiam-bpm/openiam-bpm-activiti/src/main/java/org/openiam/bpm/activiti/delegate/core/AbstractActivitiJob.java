package org.openiam.bpm.activiti.delegate.core;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.bpm.activiti.model.ActivitiJSONStringWrapper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.idm.util.CustomJacksonMapper;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractActivitiJob implements JavaDelegate, TaskListener {
	
	protected static Logger LOG = Logger.getLogger(AbstractActivitiJob.class);
	
	private FixedValue notificationType;
	private FixedValue targetVariable;
	private FixedValue provisioningEnabled;

    @Autowired
    protected AuditLogService auditLogService;

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
	protected GroupDataWebService groupDataService;
	
	@Autowired
	protected RoleDataWebService roleDataService;
	
	@Autowired
	protected ResourceDataService resourceDataService;
	
	@Autowired
	protected OrganizationDataService organizationDataService;

	@Autowired
	private CustomJacksonMapper customJacksonMapper;
	
	@Autowired
	protected ApproverAssociationDAO approverAssociationDAO;
	
	@Autowired
	protected LoginDataService loginService;

    @Value("${org.openiam.idm.system.user.id}")
    protected String systemUserId;

    @Autowired
    @Qualifier("userWS")
    private UserDataWebService userDataWebService;
	
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
	
	protected Role getRole(final String roleId) {
		return roleDataService.getRoleLocalized(roleId, null, null);
	}
	
	protected Group getGroup(final String groupId) {
		return groupDataService.getGroupLocalize(groupId, null, null);
	}
	
	protected Organization getOrganization(final String organizationId) {
		return organizationDataService.getOrganizationLocalized(organizationId, null, null);
	}
	
	protected Resource getResource(final String resourceId) {
		return resourceDataService.getResource(resourceId, null);
	}
	
	protected List<String> getSupervisorsForUser(final UserEntity  user) {
		final List<String> supervisorIds = new LinkedList<String>();
		if(user != null) {
			final List<User> userList = userDataWebService.getSuperiors(user.getId(), 0, Integer.MAX_VALUE);
			if(CollectionUtils.isNotEmpty(userList)) {
				for(final User userDto : userList) {
					supervisorIds.add(userDto.getId());
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
	
	protected boolean isProvisioningEnabled(final DelegateExecution execution) {
		boolean retVal = true;
		if(provisioningEnabled != null) {
			if(StringUtils.equalsIgnoreCase(Boolean.FALSE.toString(), StringUtils.trimToNull(provisioningEnabled.getExpressionText()))) {
				retVal = false;
			}
		}
		return retVal;
	}
	
	protected String getComment(final DelegateExecution execution) {
		return getStringVariable(execution, ActivitiConstants.COMMENT);
	}
	
	protected String getAssociationId(final DelegateExecution execution) {
		return getStringVariable(execution, ActivitiConstants.ASSOCIATION_ID);
	}
	
	protected String getMemberAssociationId(final DelegateExecution execution) {
		return getStringVariable(execution, ActivitiConstants.MEMBER_ASSOCIATION_ID);
	}
	
	protected ActivitiRequestType getRequestType(final DelegateExecution execution) {
		return ActivitiRequestType.getByName(getStringVariable(execution, ActivitiConstants.WORKFLOW_NAME));
	}
	
	public String getRequestorId(final DelegateExecution execution) {
		return getStringVariable(execution, ActivitiConstants.REQUESTOR);
	}
	
	public String getTaskDescription(final DelegateExecution execution) {
		return getStringVariable(execution, ActivitiConstants.TASK_DESCRIPTION);
	}
	
	public String getTaskName(final DelegateExecution execution) {
		return getStringVariable(execution, ActivitiConstants.TASK_NAME);
	}
	
	protected void addStringReturnValue(final DelegateExecution execution, final ActivitiConstants key, final Object value) {
		execution.setVariable(key.getName(), value);
	}
	
	protected void setDisplayMap(final DelegateExecution execution, final LinkedHashMap<String, String> metadataMap) {
		execution.setVariable(ActivitiConstants.REQUEST_METADATA_MAP.getName(), metadataMap);
	}
	
	protected String getTargetUserId(final DelegateExecution execution) {
		ActivitiConstants targetVariable = getTargetVariable();
		if(targetVariable == null) {
			final ActivitiRequestType requestType = getRequestType(execution);
			if(requestType != null) {
				if(requestType.isUserCentric()) {
					targetVariable = ActivitiConstants.MEMBER_ASSOCIATION_ID;
				}
			}
		}
		
		String retVal = null;
		if(targetVariable != null) {
			retVal = getStringVariable(execution, targetVariable);
		}
		return retVal;
	}
	
	protected String getNotificationType(final DelegateExecution execution) {
		return (notificationType != null) ? StringUtils.trimToNull(notificationType.getExpressionText()) : null;
	}
	
	protected ActivitiConstants getTargetVariable() {
		final ActivitiConstants retVal =  (targetVariable != null) ? ActivitiConstants.getByDeclarationName(StringUtils.trimToNull(targetVariable.getExpressionText())) : null;
		return retVal;
	}
}
