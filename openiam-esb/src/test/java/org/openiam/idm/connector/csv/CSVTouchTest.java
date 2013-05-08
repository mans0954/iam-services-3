package org.openiam.idm.connector.csv;

import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.spml2.interf.ConnectorService;
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
	public void touchCSVTest() {
		connectorService.reconcileResource(new ReconciliationConfig());
	}
}
