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
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDelegate implements JavaDelegate {
	
	protected static Logger LOG = Logger.getLogger(AbstractDelegate.class);

	@Autowired
	private ApproverAssociationDAO approverAssociationDao;
	
	@Autowired
	private UserGroupDAO userGroupDAO;
	
	@Autowired
	private UserRoleDAO userRoleDAO;
	
	@Autowired
	private UserDataService userManager;
	
	@Autowired
	private MailService mailService;
	
	protected AbstractDelegate() {
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
		                		if(supervisor.getSupervisor() != null) {
		                			userIds.add(supervisor.getSupervisor().getUserId());
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
	
	protected List<String> getCandidateUserIds(final DelegateExecution execution) {
		final Object candidateUserIdsObj = execution.getVariable(ActivitiConstants.CANDIDATE_USERS_IDS);
		final List<String> candidateUsersIds = new ArrayList<String>();
		if(candidateUserIdsObj != null) {
			if((candidateUserIdsObj instanceof Collection<?>)) {
				for(final String candidateId : (Collection<String>)candidateUserIdsObj) {
					if(candidateId != null) {
						candidateUsersIds.add(candidateId);
					}
				}
			} else if(candidateUserIdsObj instanceof String) {
				if(StringUtils.isNotBlank(((String)candidateUserIdsObj))) {
					candidateUsersIds.add(((String)candidateUserIdsObj));
				}
			}
		}
		return candidateUsersIds;
	}

	protected abstract String getNotificationType();
}
