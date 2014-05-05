package org.openiam.bpm.activiti.groovy;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.springframework.beans.factory.annotation.Value;

public class DefaultNewHireRequestApproverAssociationIdentifier extends AbstractApproverAssociationIdentifier {

	protected NewUserProfileRequestModel request;
	protected IdmAuditLog idmAuditLog;
	
	@Value("${org.openiam.idm.activiti.new.user.approver.association.order}")
	private String newUserApproverAssociationOrder;
	
	private List<AssociationType> newUserAssociationTypes = new LinkedList<AssociationType>();
	
	public DefaultNewHireRequestApproverAssociationIdentifier() {
		super();
		
		final String[] newUserApproverTypes = StringUtils.split(newUserApproverAssociationOrder, ",");
		if(newUserApproverTypes != null) {
			for(final String s : newUserApproverTypes) {
				final AssociationType type = AssociationType.getByValue(StringUtils.trimToNull(s));
				if(type != null) {
					newUserAssociationTypes.add(type);
				}
			}
		}
	}
	
	public final void init(final Map<String, Object> bindingMap) {
		request = (NewUserProfileRequestModel)bindingMap.get("REQUEST");
        idmAuditLog = (IdmAuditLog)bindingMap.get("BUILDER");
		super.init(bindingMap);
		calculateApprovers();
	}
	
	protected boolean isLimitToSingleApprover() {
		return true;
	}
	
	protected void calculateApprovers() {
		for(final AssociationType type : newUserAssociationTypes) {
			if(isLimitToSingleApprover()) {
				if(CollectionUtils.isNotEmpty(approverAssociationIds) || CollectionUtils.isNotEmpty(approverUserIds)) {
					break;
				}
			}
			
			if(type != null) {
				switch(type) {
					case ORGANIZATION:
						if(CollectionUtils.isNotEmpty(request.getOrganizationIds())) {
							for(final String associationId : request.getOrganizationIds()) {
								final List<ApproverAssociationEntity> entityList = approverAssociationDAO.getByAssociation(associationId, AssociationType.ORGANIZATION);
								if(CollectionUtils.isNotEmpty(entityList)) {
									for(final ApproverAssociationEntity entity : entityList) {
										approverAssociationIds.add(entity.getId());
									}
								}
							}
						}
						break;
					case GROUP:
						if(CollectionUtils.isNotEmpty(request.getGroupIds())) {
							for(final String associationId : request.getGroupIds()) {
								final List<ApproverAssociationEntity> entityList = approverAssociationDAO.getByAssociation(associationId, AssociationType.GROUP);
								if(CollectionUtils.isNotEmpty(entityList)) {
									for(final ApproverAssociationEntity entity : entityList) {
										approverAssociationIds.add(entity.getId());
									}
								}
							}
						}
						break;
					case ROLE:
						if(CollectionUtils.isNotEmpty(request.getRoleIds())) {
							for(final String associationId : request.getRoleIds()) {
								final List<ApproverAssociationEntity> entityList = approverAssociationDAO.getByAssociation(associationId, AssociationType.ROLE);
								if(CollectionUtils.isNotEmpty(entityList)) {
									for(final ApproverAssociationEntity entity : entityList) {
										approverAssociationIds.add(entity.getId());
									}
								}
							}
						}
						break;
					case SUPERVISOR:
						if(CollectionUtils.isNotEmpty(request.getSupervisorIds())) {
							for(final String id : request.getSupervisorIds()) {
								if(StringUtils.isNotBlank(id)) {
									approverUserIds.add(id);
								}
							}
						}
						break;
					default:
						break;
				}
			}
		}
		
		if(CollectionUtils.isEmpty(approverAssociationIds) && CollectionUtils.isEmpty(approverUserIds)) {
			/* get default approver assocaitions */
			final List<ApproverAssociationEntity> defaultApproverAssociations = getDefaultApproverAssociations();
			if(CollectionUtils.isNotEmpty(defaultApproverAssociations)) {
				for(final ApproverAssociationEntity entity : defaultApproverAssociations) {
					approverAssociationIds.add(entity.getId());
				}
			}
			
			/* if no default approvers, just use sysadmin */
			if(CollectionUtils.isEmpty(approverAssociationIds)) {
				approverUserIds.add(defaultApproverUserId);
			}
		}
	}
}
