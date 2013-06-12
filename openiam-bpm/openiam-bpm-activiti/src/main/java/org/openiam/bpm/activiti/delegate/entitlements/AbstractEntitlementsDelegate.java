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

public abstract class AbstractEntitlementsDelegate implements JavaDelegate {
	
	private FixedValue operation;
	
	@Autowired
	private ApproverAssociationDAO approverAssociationDao;
	
	@Autowired
	private UserGroupDAO userGroupDAO;
	
	@Autowired
	private UserRoleDAO userRoleDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private UserDataService userManager;
	
	@Autowired
	private MailService mailService;

	protected AbstractEntitlementsDelegate() {
		SpringContextProvider.autowire(this);
	}
	
	protected List<ApproverAssociationEntity> getApproverAssociations(final DelegateExecution execution) {
		final Set<String> userIds = new HashSet<String>();
		final Set<String> approverAssociationIds = (Set<String>)execution.getVariable(ActivitiConstants.APPROVER_ASSOCIATION_IDS);
		return approverAssociationDao.findByIds(approverAssociationIds);
	}
	
	protected Set<String> getNotifyUserIds(final AssociationType notifyType, final String notifyId, final String targetUserId) {
		final Set<String> userIds = new HashSet<String>();
		if(notifyType != null && StringUtils.isNotBlank(notifyId)) {
			switch(notifyType) {
				case GROUP:
					final List<String> usersInGroup = userGroupDAO.getUserIdsInGroup(notifyId);
	        		if(CollectionUtils.isNotEmpty(usersInGroup)) {
	        			userIds.addAll(usersInGroup);
	        		}	
					break;
				case SUPERVISOR:
					if(StringUtils.isNotBlank(targetUserId)) {
						final List<SupervisorEntity> supervisors = userManager.getSupervisors(targetUserId);
		                if(CollectionUtils.isNotEmpty(supervisors)) {
		                	for(final SupervisorEntity supervisor : supervisors) {
		                		if(supervisor.getEmployee() != null) {
		                			userIds.add(supervisor.getEmployee().getUserId());
		                		}
		                	}
		                }
					}
	                break;
	        	case USER:
	        		userIds.add(notifyId);
	        		break;
				case ROLE:
					final List<String> usersInRole = userRoleDAO.getUserIdsInRole(notifyId);
					if(CollectionUtils.isNotEmpty(usersInRole)) {
						userIds.addAll(usersInRole);
					}
	        		break;
				case TARGET_USER:
					if(StringUtils.isNotBlank(targetUserId)) {
						userIds.add(targetUserId);
					}
					break;
				default:
					break;
			}
		}
		return userIds;
	}
	
	protected void sendNotification(final UserEntity toNotify, 
								  	final UserEntity owner, 
								  	final UserEntity targetUser, 
								  	final String comment, 
								  	final String requestName, 
								  	final String requestDescription) {
		final NotificationRequest request = new NotificationRequest();
        request.setUserId(toNotify.getUserId());
        request.setNotificationType(getNotificationType());
        request.getParamList().add(new NotificationParam("TO_NOTIFY", toNotify));
        request.getParamList().add(new NotificationParam("TARGET_USER", targetUser));
        request.getParamList().add(new NotificationParam("REQUESTOR", owner));
        request.getParamList().add(new NotificationParam("COMMENT", comment));
        request.getParamList().add(new NotificationParam("REQUEST_REASON", requestName));
        request.getParamList().add(new NotificationParam("REQUEST_DESCRIPTION", requestDescription));
        mailService.sendNotification(request);
	}
	
	protected abstract String getNotificationType();
	
	public String getOperation() {
		return (operation != null) ? StringUtils.trimToNull(operation.getExpressionText()) : null;
	}
}
