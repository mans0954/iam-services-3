package org.openiam.idm.connector.csv;

import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.request.RequestType;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.UserAttributeHelper;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleEmailAddress;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.connector.ConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

@ContextConfiguration(locations = {
		"classpath:test-integration-environment.xml",
		"classpath:test-esb-integration.xml" })
public class CSVTouchTest extends AbstractTestNGSpringContextTests {
	@Autowired
	@Qualifier("csvConnector")
	private ConnectorService connectorService;
	@Autowired
	@Qualifier("managedSysServiceClient")
	protected ManagedSystemWebService managedSysServiceClient;
	@Value("${openiam.default_managed_sys}")
	protected String defaultManagedSysId;

	@Test
	public void addTouchCSVTest() {
        ProvisionUser pu = new ProvisionUser();
        pu.setEmail("email");
        pu.setEmployeeId("1");
        pu.setFirstName("firstName_test");

        CrudRequest<ExtensibleUser> userReq = new CrudRequest<ExtensibleUser>();
        userReq.setObjectIdentity("sysadmin");
        userReq.setRequestID("1");
        userReq.setTargetID(defaultManagedSysId);
        userReq.setHostLoginId("1");
        userReq.setHostLoginPassword("");
        userReq.setHostUrl("http://localhost");
        userReq.setBaseDN(null);
        userReq.setOperation("EDIT");
        ExtensibleUser extUser = null;

        // TODO - Move to use groovy script based on
        // attribute policies so that this is dynamic.
        try {
            extUser = UserAttributeHelper.newUser(pu);
        } catch (Exception e) {
            e.printStackTrace();
        }
        userReq.setExtensibleObject(extUser);
		connectorService.add(userReq);
	}

	@Test
	public void modifyTouchCSVTest() {
        CrudRequest<ExtensibleUser> addRequest = new CrudRequest<ExtensibleUser>();
		addRequest.setObjectIdentity("sysadmin");
        addRequest.setTargetID(defaultManagedSysId);
        ExtensibleUser ex = new ExtensibleUser();
        List<ExtensibleEmailAddress> emailAddresses = new LinkedList<ExtensibleEmailAddress>();
        emailAddresses.add(new ExtensibleEmailAddress(new EmailAddress("e@mail.com")));
        ex.setEmail(emailAddresses);
        List<ExtensibleAttribute> attributes = new LinkedList<ExtensibleAttribute>();
        attributes.add(new ExtensibleAttribute("employeeId", "2"));
        attributes.add(new ExtensibleAttribute("firstName","firstName_test_2"));
        ex.setAttributes(attributes);
        addRequest.setExtensibleObject(ex);
		connectorService.modify(addRequest);
	}

	@Test
	public void deleteTouchCSVTest() {
		CrudRequest<ExtensibleUser> addRequest = new CrudRequest<ExtensibleUser>();
		addRequest.setObjectIdentity("sysadmin2");
        addRequest.setTargetID(defaultManagedSysId);
		ExtensibleUser eu = new ExtensibleUser();
        ExtensibleEmailAddress emailAddress = new ExtensibleEmailAddress();
        EmailAddress address = new EmailAddress();
        address.setEmailAddress("email@mail.co,");
        emailAddress.setEmailAddress(address);

        eu.setObjectId("1");
        eu.setName("fn_2");
		addRequest.setExtensibleObject(eu);
		connectorService.delete(addRequest);
	}

	@Test
	public void testTouchCSVTest() {
        RequestType<ExtensibleUser> testRequest = new RequestType<ExtensibleUser>();
        testRequest.setTargetID(defaultManagedSysId);
		connectorService.testConnection(testRequest);
	}

	@Test
	public void lookupCSVTest() {
		LookupRequest lookup = new LookupRequest();
        lookup.setTargetID(defaultManagedSysId);
        lookup.setSearchValue("sysadmin2");
        lookup.setSearchQuery("UserPrincipalName");
		connectorService.lookup(lookup);
	}
}
