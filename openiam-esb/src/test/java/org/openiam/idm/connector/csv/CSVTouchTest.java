package org.openiam.idm.connector.csv;

import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.interf.ConnectorService;
import org.openiam.spml2.msg.AddRequestType;
import org.openiam.spml2.msg.DeleteRequestType;
import org.openiam.spml2.msg.ModifyRequestType;
import org.openiam.spml2.msg.PSOIdentifierType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

	@Test
	public void addTouchCSVTest() {
		AddRequestType addRequest = new AddRequestType();
		PSOIdentifierType psoType = new PSOIdentifierType();
		psoType.setID("sysadmin");
		addRequest.setPsoID(psoType);
		addRequest.setTargetID("0");
		ProvisionUser pu = new ProvisionUser();
		pu.setEmail("email");
		pu.setEmployeeId("1");
		pu.setFirstName("1");
		addRequest.setpUser(pu);
		connectorService.add(addRequest);
	}

	@Test
	public void modifyTouchCSVTest() {
		ModifyRequestType addRequest = new ModifyRequestType();
		PSOIdentifierType psoType = new PSOIdentifierType();
		psoType.setID("sysadmin");
		psoType.setTargetID("0");
		addRequest.setPsoID(psoType);
		ProvisionUser pu = new ProvisionUser();
		pu.setEmail("email222");
		pu.setEmployeeId("15125");
		pu.setFirstName("14124");
		addRequest.setpUser(pu);
		connectorService.modify(addRequest);
	}

	@Test
	public void deleteTouchCSVTest() {
		DeleteRequestType addRequest = new DeleteRequestType();
		PSOIdentifierType psoType = new PSOIdentifierType();
		psoType.setID("sysadmin");
		psoType.setTargetID("0");
		addRequest.setPsoID(psoType);
		ProvisionUser pu = new ProvisionUser();
		pu.setEmail("email222");
		pu.setEmployeeId("15125");
		pu.setFirstName("14124");
		addRequest.setpUser(pu);
		connectorService.delete(addRequest);
	}

	@Test
	public void reconcileTouchCSVTest() {
		ReconciliationConfig rConf = new ReconciliationConfig();
		ResourceEntity res = new ResourceEntity();
		res.setManagedSysId("0");
		res.setName("TestName");
		rConf.setResourceId(`)
		connectorService.reconcileResource(config);
	}

}
