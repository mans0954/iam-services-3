package org.openiam.bpm.activiti.groovy;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.bpm.request.GenericWorkflowRequest;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.util.CustomJacksonMapper;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultGenericWorkflowRequestApproverAssociationIdentifier extends AbstractApproverAssociationIdentifier {
	
	protected GenericWorkflowRequest request;
	protected AuditLogBuilder builder;
	
	public DefaultGenericWorkflowRequestApproverAssociationIdentifier() {
		super();
	}
	
	public final void init(final Map<String, Object> bindingMap) {
		request = (GenericWorkflowRequest)bindingMap.get("REQUEST");
		builder = (AuditLogBuilder)bindingMap.get("BUILDER");
		super.init(bindingMap);
		calculateApprovers();
	}
	
	public void calculateApprovers() {
		if(CollectionUtils.isNotEmpty(request.getCustomApproverIds())) {
			approverUserIds.addAll(request.getCustomApproverIds());
		} else if(isRequestForEntityCreation(request)) {
			approverUserIds.addAll(getApproversForEntityCreation(request));
		} else {
			List<ApproverAssociationEntity> approverAssocationList = null;
			
			/* for user target objects, use the supervisors - no approver association */
			if(AssociationType.USER.equals(request.getAssociationType()) && StringUtils.isNotEmpty(request.getUserCentricUserId())) {
				final List<UserEntity> supervisors = userDataService.getSuperiors(request.getUserCentricUserId(), 0, Integer.MAX_VALUE);
				if(CollectionUtils.isNotEmpty(supervisors)) {
					for(final UserEntity supervisor : supervisors) {
						if(supervisor != null ) {
							approverUserIds.add(supervisor.getId());
						}
					}
				}
			} else {
				if(CollectionUtils.isNotEmpty(request.getCustomApproverAssociationIds())) {
					approverAssocationList = approverAssociationDAO.findByIds(request.getCustomApproverAssociationIds());
				} else {
					approverAssocationList = new LinkedList<ApproverAssociationEntity>();
					
					final List<ApproverAssociationEntity> associationApprovers = approverAssociationDAO.getByAssociation(request.getAssociationId(), request.getAssociationType());
					if(CollectionUtils.isNotEmpty(associationApprovers)) {
						approverAssocationList.addAll(associationApprovers);
					}
					
					final List<ApproverAssociationEntity> memberAssociationApprovers = approverAssociationDAO.getByAssociation(request.getMemberAssociationId(), request.getMemberAssociationType());
					if(CollectionUtils.isNotEmpty(memberAssociationApprovers)) {
						approverAssocationList.addAll(memberAssociationApprovers);
					}
				}
				if(CollectionUtils.isEmpty(approverAssocationList)) {
					final String message = String.format("Can't find approver association for %s %s, using default approver association", request.getAssociationType(), request.getAssociationId());
					builder.addAttribute(AuditAttributeName.WARNING, message);
					LOG.warn(message);
					approverAssocationList = getDefaultApproverAssociations();
				}
				if(CollectionUtils.isNotEmpty(approverAssocationList)) {
					for(final ApproverAssociationEntity entity : approverAssocationList) {
						approverAssociationIds.add(entity.getId());
					}
				}
			}
			builder.addAttributeAsJson(AuditAttributeName.APPROVER_ASSOCIATIONS, approverAssocationList, jacksonMapper);
		}
		
		if(CollectionUtils.isNotEmpty(request.getAdditionalApproverIds())) {
			approverUserIds.addAll(request.getAdditionalApproverIds());
		}
		
		if(CollectionUtils.isEmpty(approverUserIds) && CollectionUtils.isEmpty(approverAssociationIds)) {
			final String message = "Could not found any approvers - using default user";
			builder.addWarning(message);
			LOG.warn(message);
        	approverUserIds.add(defaultApproverUserId);
		}
	}
}
