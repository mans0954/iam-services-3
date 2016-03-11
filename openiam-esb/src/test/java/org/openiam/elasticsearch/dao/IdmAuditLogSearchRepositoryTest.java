package org.openiam.elasticsearch.dao;

import java.util.Date;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.common.lang3.StringUtils;
import org.junit.Test;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditTarget;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.audit.service.IdmAuditLogDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

public class IdmAuditLogSearchRepositoryTest extends AbstractElasticSearchRepositoryTest<IdmAuditLogEntity, OpeniamElasticSearchRepository, IdmAuditLogDAO> {

	@Autowired
	private IdmAuditLogDAO dao;
	
	@Autowired
	private AuditLogService service;
	
	private IdmAuditLogEntity entity;
	
	@BeforeClass
	public void init() throws InterruptedException {
		entity = newEntity();
		entity.addTarget("3000", AuditTarget.USER.value(), "sysadmin");
		entity.addTarget("3006", AuditTarget.USER.value(), "sysadmin");
		service.save(entity);
		Assert.assertNotNull(entity);
		Assert.assertTrue(StringUtils.isNotBlank(entity.getId()));
	}
	
	@Autowired
	private AuditLogElasticSearchRepository repo;
	
	@Override
	public OpeniamElasticSearchRepository getRepository() {
		return repo;
	}

	@Override
	public IdmAuditLogDAO getDAO() {
		return dao;
	}
	
	@Test
	public void testSaveWithChildrenAndTargets() {
		IdmAuditLogEntity entity = newEntity();
		entity.addChild(newEntity());
		entity.addChild(newEntity());
		entity.addTarget("3000", AuditTarget.USER.value(), "sysadmin");
		entity.addTarget("3006", AuditTarget.USER.value(), "sysadmin");
		service.save(entity);
		
		entity = repo.findOne(entity.getId());
		Assert.assertNotNull(entity);
		Assert.assertTrue(CollectionUtils.isNotEmpty(entity.getChildLogs()));
		Assert.assertEquals(entity.getChildLogs().size(), 2);
		
		Assert.assertNotNull(entity);
		Assert.assertTrue(CollectionUtils.isNotEmpty(entity.getTargets()));
		Assert.assertEquals(entity.getTargets().size(), 2);
	}
	
	private IdmAuditLogEntity newEntity() {
		final IdmAuditLogEntity entity = new IdmAuditLogEntity();
		entity.setAction(randomString());
		entity.setResult(randomString());
		entity.setTimestamp(new Date());
		entity.setManagedSysId(randomString());
		entity.setSource(randomString());
		entity.setParentId(randomString());
		entity.setUserId(randomString());
		return entity;
	}
	
	@Test
	public void testEnqueue() {
		final IdmAuditLogEntity entity = newEntity();
		service.enqueue(entity);
	}

	@Test
	public void testFind() {
		AuditLogSearchBean sb = null;
		assertEmpty(sb);
		
		sb = new AuditLogSearchBean();
		assertEmpty(sb);
		
		sb.setAction(randomString());
		assertEmpty(sb);
		
		sb.setAction(entity.getAction());
		assertNotEmpty(sb);
		
		sb.setResult(randomString());
		assertEmpty(sb);
		
		sb.setResult(entity.getResult());
		assertNotEmpty(sb);
		
		sb.setManagedSysId(randomString());
		assertEmpty(sb);
		
		sb.setManagedSysId(entity.getManagedSysId());
		assertNotEmpty(sb);
		
		sb.setSource(randomString());
		assertEmpty(sb);;
		
		sb.setSource(entity.getSource());
		assertNotEmpty(sb);
		
		sb.setUserId(randomString());
		assertEmpty(sb);
		
		sb.setUserId(entity.getUserId());
		assertNotEmpty(sb);
		
		sb.setFrom(DateUtils.addDays(entity.getTimestamp(), -1));
		assertNotEmpty(sb);
		
		sb.setFrom(null);
		sb.setTo(DateUtils.addDays(entity.getTimestamp(), 1));
		assertNotEmpty(sb);
		
		sb.setFrom(DateUtils.addDays(entity.getTimestamp(), -1));
		assertNotEmpty(sb);
		
		sb.setTo(new Date());
		sb.setFrom(new Date());
		assertEmpty(sb);
		
		sb.setTo(null);
		sb.setFrom(null);
		
		sb = new AuditLogSearchBean();
		sb.setTargetId("3000");
		sb.setTargetType(AuditTarget.USER.value());
		assertNotEmpty(sb);
	}
	
	private void assertEmpty(final AuditLogSearchBean sb) {
		final Page<IdmAuditLogEntity> page = repo.find(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		Assert.assertEquals(page.getContent().get(0).getId(), entity.getId());
	}
	
	private void assertNotEmpty(final AuditLogSearchBean sb) {
		final Page<IdmAuditLogEntity> page = repo.find(sb, new PageRequest(0, 100));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		Assert.assertTrue(page.getContent().stream().filter(e -> e.getId().equals(entity.getId())).findFirst().isPresent());
	}
}
