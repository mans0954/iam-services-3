package org.openiam.bpm.activiti.groovy;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;

public class DefaultEditUserApproverAssociationIdentifier extends AbstractApproverAssociationIdentifier {

	protected UserProfileRequestModel request;
	protected IdmAuditLogEntity idmAuditLog;
	
	public final void init(final Map<String, Object> bindingMap) {
		request = (UserProfileRequestModel)bindingMap.get("REQUEST");
        idmAuditLog = (IdmAuditLogEntity)bindingMap.get("BUILDER");
		super.init(bindingMap);
		postInit();
		calculateApprovers();
	}
	
	public void calculateApprovers() {
		final List<UserEntity> supervisors = userDataService.getSuperiors(request.getUser().getId(), 0, Integer.MAX_VALUE);
		if(CollectionUtils.isNotEmpty(supervisors)) {
			for(final UserEntity supervisor : supervisors) {
				approverUserIds.add(supervisor.getId());
			}
		}
		if(CollectionUtils.isEmpty(approverUserIds)) {
			LOG.warn("Could not found any approvers - using default user");
			final String defaultApproverUserId = propertyValueSweeper.getString("org.openiam.idm.activiti.default.approver.user");
			if(defaultApproverUserId != null) {
				approverUserIds.add(defaultApproverUserId);
			}
		}
	}
}
