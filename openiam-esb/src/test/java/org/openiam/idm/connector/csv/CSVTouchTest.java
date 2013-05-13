package org.openiam.idm.connector.csv;

import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.interf.ConnectorService;
import org.openiam.spml2.msg.AddRequestType;
import org.openiam.spml2.msg.DeleteRequestType;
import org.openiam.spml2.msg.LookupRequestType;
import org.openiam.spml2.msg.ModifyRequestType;
import org.openiam.spml2.msg.PSOIdentifierType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

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
	@Value("${org.openiam.defaultManagedSysId}")
	protected String defaultManagedSysId;

	@Test
	public void addTouchCSVTest() {
		AddRequestType addRequest = new AddRequestType();
		PSOIdentifierType psoType = new PSOIdentifierType();
		psoType.setID("sysadmin");
		addRequest.setPsoID(psoType);
		addRequest.setTargetID(defaultManagedSysId);
		ProvisionUser pu = new ProvisionUser();
		pu.setEmail("email");
		pu.setEmployeeId("1");
		pu.setFirstName("firstName_test");
		addRequest.setpUser(pu);
		connectorService.add(addRequest);
	}

	@Test
	public void modifyTouchCSVTest() {
		ModifyRequestType addRequest = new ModifyRequestType();
		PSOIdentifierType psoType = new PSOIdentifierType();
		psoType.setID("sysadmin");
		psoType.setTargetID(defaultManagedSysId);
		addRequest.setPsoID(psoType);
		ProvisionUser pu = new ProvisionUser();
		pu.setEmail("e@mail.com");
		pu.setEmployeeId("2");
		pu.setFirstName("firstName_test_2");
		addRequest.setpUser(pu);
		connectorService.modify(addRequest);
	}

	@Test
	public void deleteTouchCSVTest() {
		DeleteRequestType addRequest = new DeleteRequestType();
		PSOIdentifierType psoType = new PSOIdentifierType();
		psoType.setID("sysadmin2");
		psoType.setTargetID(defaultManagedSysId);
		addRequest.setPsoID(psoType);
		ProvisionUser pu = new ProvisionUser();
		pu.setEmail("email@mail.co,");
		pu.setEmployeeId("1");
		pu.setFirstName("fn_2");
		addRequest.setpUser(pu);
		connectorService.delete(addRequest);
	}

	@Test
	public void testTouchCSVTest() {
		connectorService.testConnection(managedSysServiceClient
				.getManagedSys(defaultManagedSysId));
	}

	@Test
	public void lookupCSVTest() {
		LookupRequestType lookup = new LookupRequestType();
		PSOIdentifierType psoType = new PSOIdentifierType();
		psoType.setID("sysadmin2");
		psoType.setTargetID(defaultManagedSysId);
		lookup.setPsoID(psoType);
		connectorService.lookup(lookup);
	}
}
