package org.openiam.idm.srvc.batch.tasks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.bpm.activiti.ActivitiService;
import org.openiam.bpm.request.GenericWorkflowRequest;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.SupervisorDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.opensaml.saml1.core.validator.ResponseSchemaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lbornov2
 * Called by BatchTask thread, if configured in the DB
 */
@Component("attestationInitializer")
public class AttestationInitializer {
	
	
	private static Logger LOG = Logger.getLogger(AttestationInitializer.class);
	
	@Autowired
	private ActivitiService activitiService;
	
	@Autowired
	private SupervisorDAO supervisorDAO;
	
	@Value("${org.openiam.activiti.attestation.selfservice.url}")
	private String attestationURL;
	
	@Value("${org.openiam.idm.system.user.id}")
	private String systemUserId;
	
	@Autowired
	private UserDataService userService;

	@Transactional
	public void initializeAttestation() {
		final StopWatch sw = new StopWatch();
		sw.start();
		final Set<String> employeeIds = supervisorDAO.getUniqueEmployeeIds();
		if(CollectionUtils.isNotEmpty(employeeIds)) {
			for(final String employeeId : employeeIds) {
				final UserEntity user = userService.getUser(employeeId);
				final List<UserEntity> supervisords = userService.getSuperiors(employeeId, 0, Integer.MAX_VALUE);
				final Set<String> supervisorIds = new HashSet<String>();
				if(CollectionUtils.isEmpty(supervisords)) {
					LOG.info(String.format("Employee %s has no supervisor", employeeId));
					continue;
				}
				
				for(final UserEntity supevisor : supervisords) {
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
					final Response response = activitiService.initiateWorkflow(request);
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
