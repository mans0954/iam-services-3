package org.openiam.bpm.activiti.delegate.user.attestation;

import java.util.ArrayList;
import java.util.Collection;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class SendAttestationRequestDelegate implements JavaDelegate {
	
	private static Logger LOG = Logger.getLogger(SendAttestationRequestDelegate.class);
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private UserDataService userService;
	
	public SendAttestationRequestDelegate() {
		SpringContextProvider.autowire(this);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final StopWatch sw = new StopWatch();
		sw.start();
		final String employeeId = (String)execution.getVariable(ActivitiConstants.EMPLOYEE_ID);
		if(employeeId != null) {
			final UserEntity employee = userService.getUser(employeeId);
			if(employee != null) {
				final Object candidateUserIdsObj = execution.getVariable(ActivitiConstants.CANDIDATE_USERS_IDS);
				final Collection<String> candidateUsersIds = new ArrayList<String>();
				if(candidateUserIdsObj != null) {
					if((candidateUserIdsObj instanceof Collection<?>)) {
						for(final String candidateId : (Collection<String>)candidateUserIdsObj) {
							if(candidateId != null) {
								candidateUsersIds.add(candidateId);
							}
						}
					} else if(candidateUserIdsObj instanceof String) {
						if(StringUtils.isNotBlank(((String)candidateUserIdsObj))) {
							candidateUsersIds.add(((String)candidateUserIdsObj));
						}
					}
				}
				
				for(final String candidateId : candidateUsersIds) {
					final UserEntity supervisor = userService.getUser(candidateId);
					if(supervisor != null) {
						sendNotificationRequest(supervisor, employee);
					}
				}
			}
		}
		sw.stop();
		LOG.info(String.format("Took %s ms to send attestation requests for user %s", sw.getTime(), employeeId));
	}

	private void sendNotificationRequest(final UserEntity supervisor, final UserEntity employee) {
		final NotificationRequest request = new NotificationRequest();
        request.setUserId(supervisor.getUserId());
        request.setNotificationType("ATTESTATION_REQUEST");
        request.getParamList().add(new NotificationParam("EMPLOYEE", employee));
        request.getParamList().add(new NotificationParam("SUPERVISOR", supervisor));
        mailService.sendNotification(request);
	}
}
