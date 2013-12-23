package org.openiam.bpm.activiti.delegate.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.activiti.model.ActivitiJSONStringWrapper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.util.CustomJacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ActivitiHelper {
	
	@Autowired
	private ApproverAssociationDAO approverAssociationDao;

	@Autowired
	private UserDataService userManager;

	@Transactional
	public List<ApproverAssociationEntity> getApproverAssociations(final DelegateExecution execution) {
		final List<String> approverAssociationIds = (List<String>)execution.getVariable(ActivitiConstants.APPROVER_ASSOCIATION_IDS.getName());
		return approverAssociationDao.findByIds(approverAssociationIds);
	}
	
	public Set<String> getOnRejectUserIds(final DelegateExecution execution, final String targetUserId, final List<String> supervisorIds) {
		final Set<String> userIds = new HashSet<String>();
		final List<ApproverAssociationEntity> approverAssociations = getApproverAssociations(execution);
		if(CollectionUtils.isNotEmpty(approverAssociations)) {
			for(final ApproverAssociationEntity entity : approverAssociations) {
				final AssociationType type = entity.getOnRejectEntityType();
				final String id = entity.getOnRejectEntityId();
				if(StringUtils.isNotEmpty(id)) {
					switch(type) {
						case GROUP:
							final List<String> groupUsers = userManager.getUserIdsInGroup(id, null);
							if(CollectionUtils.isNotEmpty(groupUsers)) {
								userIds.addAll(groupUsers);
							}
							break;
						case ROLE:
							final List<String> roleUsers = userManager.getUserIdsInRole(id, null);
							if(CollectionUtils.isNotEmpty(roleUsers)) {
								userIds.addAll(roleUsers);
							}
							break;
						case ORGANIZATION:
							break;
						case SUPERVISOR:
							if(StringUtils.isNotBlank(targetUserId)) {
								final List<UserEntity> supervisors = userManager.getSuperiors(targetUserId, 0, Integer.MAX_VALUE);
								if(CollectionUtils.isNotEmpty(supervisors)) {
									for(final UserEntity supervisor : supervisors) {
										if(supervisor != null ) {
											userIds.add(supervisor.getId());
										}
									}
								}
							} else if(CollectionUtils.isNotEmpty(supervisorIds)) {
								userIds.addAll(supervisorIds);
							}
							break;
						case TARGET_USER:
							if(StringUtils.isNotBlank(targetUserId)) {
								userIds.add(targetUserId);
							}
							break;
						case USER:
							userIds.add(id);
							break;
					}
				}
			}
		}
		return userIds;
	}
	
	@Transactional
	public Set<String> getOnAcceptUserIds(final DelegateExecution execution, final String targetUserId, final List<String> supervisorIds) {
		final Set<String> userIds = new HashSet<String>();
		final List<ApproverAssociationEntity> approverAssociations = getApproverAssociations(execution);
		if(CollectionUtils.isNotEmpty(approverAssociations)) {
			for(final ApproverAssociationEntity entity : approverAssociations) {
				final AssociationType type = entity.getOnApproveEntityType();
				final String id = entity.getOnApproveEntityId();
				if(StringUtils.isNotEmpty(id)) {
					switch(type) {
						case GROUP:
							final List<String> groupUsers = userManager.getUserIdsInGroup(id, null);
							if(CollectionUtils.isNotEmpty(groupUsers)) {
								userIds.addAll(groupUsers);
							}
							break;
						case ROLE:
							final List<String> roleUsers = userManager.getUserIdsInRole(id, null);
							if(CollectionUtils.isNotEmpty(roleUsers)) {
								userIds.addAll(roleUsers);
							}
							break;
						case ORGANIZATION:
							break;
						case SUPERVISOR:
							if(StringUtils.isNotBlank(targetUserId)) {
								final List<UserEntity> supervisors = userManager.getSuperiors(targetUserId, 0, Integer.MAX_VALUE);
								if(CollectionUtils.isNotEmpty(supervisors)) {
									for(final UserEntity supervisor : supervisors) {
										if(supervisor != null ) {
											userIds.add(supervisor.getId());
										}
									}
								}
							} else if(CollectionUtils.isNotEmpty(supervisorIds)) {
								userIds.addAll(supervisorIds);
							}
							break;
						case TARGET_USER:
							if(targetUserId != null) {
								userIds.add(targetUserId);
							}
							break;
						case USER:
							userIds.add(id);
							break;
					}
				}
			}
		}
		return userIds;
	}
	
	@Transactional
	public List<String> getCandidateUserIds(final DelegateExecution execution, final String targetUserId, final List<String> supervisorIds) {
		final List<String> candidateUsersIds = new ArrayList<String>();
		final ApproverAssociationEntity entity = getApproverAssociation(execution);
		if(entity != null) {
			if(entity.getApproverEntityType() != null && StringUtils.isNotBlank(entity.getApproverEntityId())) {
				final String approverId = entity.getApproverEntityId();
				switch(entity.getApproverEntityType()) {
					case GROUP:
						final List<String> groupUsers = userManager.getUserIdsInGroup(approverId, null);
						if(CollectionUtils.isNotEmpty(groupUsers)) {
							candidateUsersIds.addAll(groupUsers);
						}
						break;
					case ROLE:
						final List<String> roleUsers = userManager.getUserIdsInRole(approverId, null);
						if(CollectionUtils.isNotEmpty(roleUsers)) {
							candidateUsersIds.addAll(roleUsers);
						}
						break;
					case USER:
						candidateUsersIds.add(approverId);
						break;
					case SUPERVISOR:
						if(StringUtils.isNotBlank(targetUserId)) {
							final List<UserEntity> supervisors = userManager.getSuperiors(targetUserId, 0, Integer.MAX_VALUE);
							if(CollectionUtils.isNotEmpty(supervisors)) {
								for(final UserEntity supervisor : supervisors) {
									if(supervisor != null ) {
										candidateUsersIds.add(supervisor.getId());
									}
								}
							}
						} else if(CollectionUtils.isNotEmpty(supervisorIds)) {
							candidateUsersIds.addAll(supervisorIds);
						}
						break;
					default:
						break;
				}
			}
		} else {
			candidateUsersIds.addAll(getCandidateUserIds(execution));
		}
		return candidateUsersIds;
	}
	
	private List<String> getCandidateUserIds(final DelegateExecution execution) {
		final List<String> candidateUsersIds = new LinkedList<String>();
		Object cardinalityObject = null;
		if(execution.hasVariable(ActivitiConstants.CARDINALITY_OBJECT.getName())) {
			cardinalityObject = execution.getVariable(ActivitiConstants.CARDINALITY_OBJECT.getName());
			if((cardinalityObject instanceof Collection<?>)) {
				for(final String candidateId : (Collection<String>)cardinalityObject) {
					if(candidateId != null) {
						candidateUsersIds.add(candidateId);
					}
				}
			}
		}
		return candidateUsersIds;
	}
	
	private ApproverAssociationEntity getApproverAssociation(final DelegateExecution execution) {
		ApproverAssociationEntity association = null;
		Object cardinalityObject = null;
		if(execution.hasVariable(ActivitiConstants.CARDINALITY_OBJECT.getName())) {
			cardinalityObject = execution.getVariable(ActivitiConstants.CARDINALITY_OBJECT.getName());
			if(cardinalityObject instanceof String) {
				final String id = (String)cardinalityObject;
				association = approverAssociationDao.findById(id);
			}
		}
		return association;
	}
}
