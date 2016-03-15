package org.openiam.service.integration.log;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.ws.IdmAuditLogWebDataService;
import org.openiam.service.integration.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

public class IdmAuditLogServiceTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("auditServiceClient")
	private IdmAuditLogWebDataService service;
	
	private IdmAuditLogEntity newInstance() {
		final IdmAuditLogEntity entity = new IdmAuditLogEntity();
		entity.setAction(getRandomName());
		entity.setResult(getRandomName());
		entity.setTimestamp(new Date());
		entity.setManagedSysId(getRandomName());
		entity.setSource(getRandomName());
		entity.setParentId(getRandomName());
		entity.setUserId(getRandomName());
		final String targetType = Arrays.asList(new String[] {"ROLE", "USER", "GROUP", "RESOURCE", "ORG"}).get(RandomUtils.nextInt(0, 5));
		entity.addTarget(getRandomName(), targetType, getRandomName());
		return entity;
	}


	@Test(threadPoolSize=1, invocationCount=10)
	public void testEnqueue() throws InterruptedException {
		final List<IdmAuditLogEntity> logs = new LinkedList<IdmAuditLogEntity>();
		for(int i = 0; i < 200; i++) {
			logs.add(newInstance());
		}
		service.addLogs(logs);
		Thread.sleep(1000L);
	}
}
