package org.openiam.bpm.activiti.groovy;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.springframework.beans.factory.annotation.Value;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CustomDefaultNewHireRequestApproverAssociationIdentifier extends DefaultNewHireRequestApproverAssociationIdentifier {

	protected boolean isLimitToSingleApprover() {
		return false;
	}
	
	protected void calculateApprovers() {
        if(CollectionUtils.isNotEmpty(request.getSupervisorIds())) {
            for(final String id : request.getSupervisorIds()) {
                if(StringUtils.isNotBlank(id)) {
                    approverUserIds.add(id);
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
