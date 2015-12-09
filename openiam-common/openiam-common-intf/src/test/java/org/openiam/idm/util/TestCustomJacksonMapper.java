package org.openiam.idm.util;

import java.io.IOException;

import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestCustomJacksonMapper {

	private CustomJacksonMapper mapper = new CustomJacksonMapper(); 
	
	@Test
	public void testIdmAuditLogSerialization() throws IOException {
		final IdmAuditLogEntity log = new IdmAuditLogEntity();
		log.setAction("a");
		log.setClientIP("b");
		log.put("c", "d");
		
		final String s = mapper.writeValueAsString(log);
		final IdmAuditLogEntity afterSerialization = mapper.readValue(s, IdmAuditLogEntity.class);
		
		Assert.assertEquals(afterSerialization.getAction(), log.getAction());
		Assert.assertEquals(afterSerialization.getClientIP(), log.getClientIP());
		Assert.assertEquals(afterSerialization.get("c"), log.get("c"));
	}
	
	@Test
	public void testIdmAuditLogSerializationWithoutMap() throws IOException {
		final IdmAuditLogEntity log = new IdmAuditLogEntity();
		log.setAction("a");
		log.setClientIP("b");
		
		final String s = mapper.writeValueAsString(log);
		final IdmAuditLogEntity afterSerialization = mapper.readValue(s, IdmAuditLogEntity.class);
		
		Assert.assertEquals(afterSerialization.getAction(), log.getAction());
		Assert.assertEquals(afterSerialization.getClientIP(), log.getClientIP());
	}
}
