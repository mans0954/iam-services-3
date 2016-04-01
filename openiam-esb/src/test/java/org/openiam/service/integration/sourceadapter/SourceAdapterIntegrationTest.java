package org.openiam.service.integration.sourceadapter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.srcadapter.SourceAdapterAttributeRequest;
import org.openiam.provision.dto.srcadapter.SourceAdapterKey;
import org.openiam.provision.dto.srcadapter.SourceAdapterKeyEnum;
import org.openiam.provision.dto.srcadapter.SourceAdapterMemberhipKey;
import org.openiam.provision.dto.srcadapter.SourceAdapterOperationEnum;
import org.openiam.provision.dto.srcadapter.SourceAdapterRequest;
import org.openiam.provision.dto.srcadapter.SourceAdapterResponse;
import org.openiam.provision.service.SourceAdapter;
import org.openiam.service.integration.AbstractServiceTest;

public class SourceAdapterIntegrationTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("sourceAdapterClient")
	private SourceAdapter sourceAdapter;
	
	@Autowired
    @Qualifier("userServiceClient")
    protected UserDataWebService userServiceClient;
	
	@Test(threadPoolSize = 10, invocationCount = 6000)
	public void testSourceAdapter() {
		final SourceAdapterRequest request = new SourceAdapterRequest();
		request.setAction(SourceAdapterOperationEnum.ADD);
		request.setEmployeeId(getRandomName());
		request.setFirstName(getRandomName());
		request.setForceMode(true);
		request.setLastName(getRandomName());
		request.setMaidenName(getRandomName());
		request.setMiddleName(getRandomName());
		request.setNickname(getRandomName());
		request.setPrefix(getRandomName(2));
		request.setPrefixLastName(getRandomName());
		request.setSuffix(getRandomName(2));
		request.setTitle(getRandomName());
		
		final SourceAdapterKey requestor = new SourceAdapterKey();
		requestor.setValue("3000");
		requestor.setName(SourceAdapterKeyEnum.USERID);
		request.setRequestor(requestor);
		final Set<SourceAdapterMemberhipKey> supervisors = new HashSet<SourceAdapterMemberhipKey>();
		final SourceAdapterMemberhipKey supervisor = new SourceAdapterMemberhipKey();
		supervisor.setName(SourceAdapterKeyEnum.USERID);
		supervisor.setValue("3000");
		supervisors.add(supervisor);
		request.setSupervisors(supervisors);
		
		final Set<SourceAdapterAttributeRequest> userAttributes = new HashSet<SourceAdapterAttributeRequest>();
		for(int i = 0; i < 3; i++) {
			final SourceAdapterAttributeRequest attribute = new SourceAdapterAttributeRequest();
			attribute.setName(getRandomName());
			attribute.setValue(getRandomName());
			userAttributes.add(attribute);
		}
		request.setUserAttributes(userAttributes);
		
		/*
		request.setPhones(phones);
		request.setResources(resources);
		request.setOrganizations(organizations);
		request.setLogins(logins);
		request.setGroups(groups);
		request.setAddresses(addresses);
		request.setEmails(emails);
		request.setRoles(roles);
		*/
		
		final SourceAdapterResponse response = sourceAdapter.perform(request);
		Assert.assertNotNull(response);
		Assert.assertEquals(response.getStatus(), ResponseStatus.SUCCESS);
	}
}
