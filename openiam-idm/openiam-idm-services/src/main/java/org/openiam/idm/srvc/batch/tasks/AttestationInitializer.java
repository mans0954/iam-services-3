package org.openiam.idm.srvc.batch.tasks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.bpm.activiti.ActivitiService;
import org.openiam.bpm.dto.BasicWorkflowResponse;
import org.openiam.bpm.request.GenericWorkflowRequest;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.SupervisorDAO;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author lbornov2
 * Called by BatchTask thread, if configured in the DB
 */
@Component("attestationInitializer")
public class AttestationInitializer {
	
	
	private static final Log LOG = LogFactory.getLog(AttestationInitializer.class);
	
	@Autowired
    @Qualifier("activitiBPMService")
	private ActivitiService activitiService;
	
	@Autowired
	private SupervisorDAO supervisorDAO;
	
	@Value("${org.openiam.activiti.attestation.selfservice.url}")
	private String attestationURL;
	
	@Value("${org.openiam.idm.system.user.id}")
	private String systemUserId;
	
	@Autowired
	private UserDataWebService userService;

	public void initializeAttestation() {
		final StopWatch sw = new StopWatch();
		sw.start();
		final Set<String> employeeIds = supervisorDAO.getUniqueEmployeeIds();
		if(CollectionUtils.isNotEmpty(employeeIds)) {
			for(final String employeeId : employeeIds) {
				final User user = userService.getUserWithDependent(employeeId,systemUserId,false);
				final List<User> supervisords = userService.getSuperiors(employeeId, 0, Integer.MAX_VALUE);
				final Set<String> supervisorIds = new HashSet<String>();
				if(CollectionUtils.isEmpty(supervisords)) {
					LOG.info(String.format("Employee %s has no supervisor", employeeId));
					continue;
				}
				
				for(final User supevisor : supervisords) {
					if(supevisor != null && supevisor.getId() != null) {
						supervisorIds.add(supevisor.getId());
					}
				}
				
				if(CollectionUtils.isNotEmpty(supervisorIds)) {
					final String taskName = String.format("Re-Certification Request for %s", user.getDisplayName());
					
					final GenericWorkflowRequest request = new GenericWorkflowRequest();
					request.setActivitiRequestType(ActivitiRequestType.ATTESTATION.getKey());
					request.setDescription(taskName);
					request.setName(taskName);
					request.setCustomApproverIds(supervisorIds);
					request.addParameter(ActivitiConstants.EMPLOYEE_ID.getName(), employeeId);
					request.addParameter(ActivitiConstants.ATTESTATION_URL.getName(), attestationURL);
					request.setRequestorUserId(systemUserId);
					request.setDeletable(false);
					final BasicWorkflowResponse response = activitiService.initiateWorkflow(request);
					if(!ResponseStatus.SUCCESS.equals(response.getStatus())) {
						LOG.info(String.format("Could not initialize re-certification task for user %s.  Reason: %s", employeeId, response.getErrorCode()));
					}
				}
			}
		}
		sw.stop();
		LOG.info(String.format("Took %s ms to start re-certification requests", sw.getTime()));
	}
}
