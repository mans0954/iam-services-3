package org.openiam.bpm.activiti.delegate.user.newuser;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.bpm.activiti.delegate.core.ActivitiHelper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.domain.RequestApproverEntity;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

public class RejectProfileProvisionDelegate implements JavaDelegate {
	
	 private static Logger log = Logger.getLogger(RejectProfileProvisionNotifierDelegate.class);
     
     @Autowired
     @Qualifier("mailService")
     private MailService mailService;
     
     @Autowired
     @Qualifier("approverAssociationDAO")
     private ApproverAssociationDAO approverAssociationDao;
     
     @Autowired
     @Qualifier("defaultProvision")
     private ProvisionService provisionService;
     
     @Autowired
     private UserDataService userManager;
     
     @Autowired
     @Qualifier("provRequestService")
     private RequestDataService provRequestService;
     
     @Autowired
     @Qualifier("userDAO")
     private UserDAO userDAO;
     
     @Autowired
     private ActivitiHelper activitiHelper;

     public RejectProfileProvisionDelegate() {
             SpringContextProvider.autowire(this);
     }
     
     @Override
     public void execute(DelegateExecution execution) throws Exception {
             final String lastCaller = (String)execution.getVariable(ActivitiConstants.EXECUTOR_ID.getName());
             final String provisionRequestId = (String)execution.getVariable(ActivitiConstants.PROVISION_REQUEST_ID.getName());
             
             final Set<String> emails = new HashSet<String>();
             
             final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(provisionRequestId);
             final NewUserProfileRequestModel profileModel = (NewUserProfileRequestModel)new XStream().fromXML(provisionRequest.getRequestXML());
             if(CollectionUtils.isNotEmpty(profileModel.getEmails())) {
                     for(final EmailAddress address : profileModel.getEmails()) {
                             if(StringUtils.isNotBlank(address.getEmailAddress())) {
                                     emails.add(address.getEmailAddress());
                             }
                     }
             }
             
             final Collection<String> userIds = activitiHelper.getOnRejectUserIds(execution, null, profileModel.getSupervisorIds());
             
             final UserEntity requestor = userManager.getUser(lastCaller);
             sendEmails(requestor, provisionRequest, profileModel.getUser(), userIds, emails);
     }
     
     private void sendEmails(final UserEntity requestor, final ProvisionRequestEntity provisionRequest, final User user, final Collection<String> userIds, final Collection<String> emailAddresses) {
             if(CollectionUtils.isNotEmpty(userIds)) {
                     for(final String userId : userIds) {
                             sendEmail(requestor, provisionRequest, user, userId, null);
                     }
             }
             
             if(CollectionUtils.isNotEmpty(emailAddresses)) {
                     for(final String email : emailAddresses) {
                             sendEmail(requestor, provisionRequest, user, null, email);
                     }
             }
     }
     
     private void sendEmail(final UserEntity requestor, final ProvisionRequestEntity provisionRequest, final User user, final String userId, final String email) {
	     final NotificationRequest request = new NotificationRequest();
	     request.setUserId(userId);
	     request.setNotificationType("REQUEST_REJECTED");
	     request.setTo(email);
	     request.getParamList().add(new NotificationParam("REQUEST_ID", provisionRequest.getId()));
	     request.getParamList().add(new NotificationParam("REQUEST_REASON", provisionRequest.getRequestReason()));
	     request.getParamList().add(new NotificationParam("REQUESTOR", String.format("%s %s", requestor.getFirstName(), requestor.getLastName())));
	     request.getParamList().add(new NotificationParam("TARGET_USER", String.format("%s %s", user.getFirstName(), user.getLastName())));
	     mailService.sendNotification(request);
     }
}
