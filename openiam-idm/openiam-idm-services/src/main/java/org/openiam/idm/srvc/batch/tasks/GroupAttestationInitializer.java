package org.openiam.idm.srvc.batch.tasks;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.SetStringResponse;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.bpm.activiti.ActivitiService;
import org.openiam.bpm.dto.BasicWorkflowResponse;
import org.openiam.bpm.request.GenericWorkflowRequest;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;

/**
* @author Alexadner Duckardt
* Called by BatchTask thread, if configured in the DB
*/
@Component("groupAttestationInitializer")
public class GroupAttestationInitializer {

	private static final Log LOG = LogFactory.getLog(GroupAttestationInitializer.class);

    @Autowired
    @Qualifier("activitiBPMService")
    private ActivitiService activitiService;
    @Autowired
    private AuthorizationManagerAdminService adminService;

    @Value("${org.openiam.activiti.group.attestation.selfservice.url}")
    private String attestationURL;

    @Value("${org.openiam.idm.system.user.id}")
    private String systemUserId;

    @Autowired
    private UserDataWebService userService;

    @Autowired
    @Qualifier("groupManager")
    private GroupDataService groupDataService;

    public void initializeAttestation() {
        final StopWatch sw = new StopWatch();
        sw.start();
        final Set<String> groupIds = groupDataService.getGroupIdList();
        if(CollectionUtils.isNotEmpty(groupIds)) {
            HashMap<String, SetStringResponse> groupOwnerMap = adminService.getOwnerIdsForGroupSet(groupIds);
            if(groupOwnerMap!=null && !groupOwnerMap.isEmpty()){
                for(final String groupId : groupOwnerMap.keySet()) {
                    SetStringResponse groupOwnerResponse = groupOwnerMap.get(groupId);
                    if(groupOwnerResponse!=null && CollectionUtils.isNotEmpty(groupOwnerResponse.getSetString())){

                        final GroupEntity group = groupDataService.getGroup(groupId);

                        final String taskName = String.format("Group Attestation Request for %s", group.getName());
                        final GenericWorkflowRequest request = new GenericWorkflowRequest();
                        request.setActivitiRequestType(ActivitiRequestType.GROUP_ATTESTATION.getKey());
                        request.setDescription(taskName);
                        request.setName(taskName);
                        request.setCustomApproverIds(groupOwnerResponse.getSetString());
                        request.addParameter(ActivitiConstants.EMPLOYEE_ID.getName(), groupId);
                        request.addParameter(ActivitiConstants.ATTESTATION_URL.getName(), attestationURL);
                        request.setRequestorUserId(systemUserId);
                        request.setDeletable(false);
                        final BasicWorkflowResponse response = activitiService.initiateWorkflow(request);
                        if(!ResponseStatus.SUCCESS.equals(response.getStatus())) {
                            LOG.info(String.format("Could not initialize Group Attestation task for group %s.  Reason: %s", groupId, response.getErrorCode()));
                        }
                    }
                }
            }
        }
        sw.stop();
        LOG.info(String.format("Took %s ms to start Group Attestation  requests", sw.getTime()));
    }

}
