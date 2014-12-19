package org.openiam.service.integration.activiti;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.am.srvc.ws.AuthResourceAttributeWebService;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.ActivitiService;
import org.openiam.bpm.request.ActivitiRequestDecision;
import org.openiam.bpm.response.TaskListWrapper;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.auth.ws.LoginResponse;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.service.integration.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ActivitiServiceTest extends AbstractServiceTest {
	
	@Autowired
	@Qualifier("activitiClient")
	private ActivitiService activitiClient;
	
	@Autowired
	@Qualifier("loginServiceClient")
	private LoginDataWebService loginServiceClient;
	

}
