package org.openiam.bpm.activiti.groovy;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.bpm.request.GenericWorkflowRequest;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;

public class DefaultEditUserApproverAssociationIdentifier extends AbstractApproverAssociationIdentifier {

	protected UserProfileRequestModel request;
	protected AuditLogBuilder builder;
	
	public final void init(final Map<String, Object> bindingMap) {
		request = (UserProfileRequestModel)bindingMap.get("REQUEST");
		builder = (AuditLogBuilder)bindingMap.get("BUILDER");
		super.init(bindingMap);
		calculateApprovers();
	}
	
	public void calculateApprovers() {
		final List<UserEntity> supervisors = userDataService.getSuperiors(request.getUser().getUserId(), 0, Integer.MAX_VALUE);
		if(CollectionUtils.isNotEmpty(supervisors)) {
			for(final UserEntity supervisor : supervisors) {
				approverUserIds.add(supervisor.getUserId());
			}
		}
		if(CollectionUtils.isEmpty(approverUserIds)) {
			LOG.warn("Could not found any approvers - using default user");
			approverUserIds.add(defaultApproverUserId);
		}
	}
}
