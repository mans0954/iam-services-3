package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditSource;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.service.ObjectProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class SaveGroupDelegate extends AbstractActivitiJob {

	@Autowired
	private GroupDataWebService groupDataService;

    @Autowired
    @Qualifier("groupProvision")
    private ObjectProvisionService<ProvisionGroup> groupProvisionService;
	
	public SaveGroupDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Group group = getObjectVariable(execution, ActivitiConstants.GROUP, Group.class);
		final IdmAuditLog idmAuditLog = createNewAuditLog(execution);
        boolean isNew = false;
        if (group.getId() == null) {
            idmAuditLog.setAction(AuditAction.ADD_GROUP.value());
            idmAuditLog.setAuditDescription("Create new group");
            isNew = true;
        } else {
            idmAuditLog.setAction(AuditAction.EDIT_GROUP.value());
            idmAuditLog.setAuditDescription("Edit group");
        }
        try {
        	idmAuditLog.setTargetGroup(group.getId(), group.getName());
            final Response wsResponse =  groupDataService.saveGroup(group, getRequestorId(execution));
            if (wsResponse.isSuccess()) {
                String groupId = (String) wsResponse.getResponseValue();

                Group createdGroup  = groupDataService.getGroup(groupId, getRequestorId(execution));
                ProvisionGroup provisionGroup = new ProvisionGroup(createdGroup);
                Response groupResponse = (isNew) ? groupProvisionService.add(provisionGroup) :
                        groupProvisionService.modify(provisionGroup);
                //TODO group provisioning processing the result

                idmAuditLog.setTargetGroup(groupId, group.getName());
                idmAuditLog.succeed();
            } else {
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(wsResponse.getErrorCode());
                idmAuditLog.setFailureReason(wsResponse.getErrorText());
                throw new RuntimeException(String.format("Save Group Failed failed; %s", wsResponse));
            }
        } catch(Throwable e) {
 			idmAuditLog.setException(e);
 			idmAuditLog.fail();
 			throw new RuntimeException(e);
 		} finally {
 			addAuditLogChild(execution, idmAuditLog);
 		}
	}
}
