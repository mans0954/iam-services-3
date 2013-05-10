package org.openiam.idm.srvc.file;

import org.openiam.idm.srvc.file.ws.FileWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {
		"classpath:test-integration-environment.xml",
		"classpath:test-esb-integration.xml" })
public class FileTouchTest extends AbstractTestNGSpringContextTests {
	@Autowired
	@Qualifier("fileWebService")
	private FileWebService fileWebService;

	@Test
	public void touchFileTest() {
		fileWebService.saveFile("test.txt", "Test message write");
		fileWebService.getFile("test.txt");
	}
}
