package org.openiam.service.integration;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.base.BaseIdentity;
import org.openiam.base.KeyDTO;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.testng.Assert;
import org.testng.annotations.Test;

public abstract class AbstractKeyServiceTest<T extends KeyDTO, S extends AbstractSearchBean<T, String>> extends AbstractServiceTest {
	protected abstract T newInstance();
	protected abstract S newSearchBean();
	protected abstract Response save(T t);
	protected abstract Response delete(T t);
	protected abstract T get(final String key);
	public abstract List<T> find(final S searchBean, final int from, final int size);
	
	protected T createBean() {
		final T bean = newInstance();
		return bean;
	}
	
	protected T assertClusteredSave(final S searchBean) throws InterruptedException {
		Thread.sleep(2000L);
		final List<T> list1 = find(searchBean, 0, 5);
    	final List<T> list2 = find(searchBean, 0, 5);
    	Assert.assertTrue(CollectionUtils.isNotEmpty(list1));
    	Assert.assertEquals(list1, list2, String.format("Multiclustered hit failed"));
    	return list1.get(0);
	}

	protected Response saveAndAssert(T t) {
		final Response response = save(t);
		Assert.assertTrue(response.isSuccess(), String.format("Could not save entity.  %s", response));
		return response;
	}
	
	@Test
	public void clusterTest() throws Exception {
		ClusterKey<T, S> key = doClusterTest();
		T instance = key.getDto();
		if(instance != null && instance.getId() != null) {
			deleteAndAssert(instance);
    	}
	}
	
	protected Response deleteAndAssert(final T instance) {
		Response response = delete(instance);
		Assert.assertTrue(response.isSuccess(), String.format("Could not delete element '%s' with ID '%s.  Response: %s", instance, instance.getId(), response));
		return response;
	}
	
	public ClusterKey<T, S> doClusterTest() throws Exception {
		/* create and save */
		T instance = createBean();
		Response response = saveAndAssert(instance);
		instance.setId((String)response.getResponseValue());
		
		/* find */
		final S searchBean = newSearchBean();
		searchBean.setDeepCopy(useDeepCopyOnFindBeans());
		searchBean.setKey(instance.getId());
    	
    	/* confirm save on both nodes */
    	instance = assertClusteredSave(searchBean);
    	return new ClusterKey<T, S>(instance, searchBean);
	}
	
	protected boolean useDeepCopyOnFindBeans() {
		return false;
	}
	
	protected class ClusterKey<T, S> {
		
		private T dto;
		private S searchBean;
		
		public ClusterKey(T dto, S searchBean) {
			this.dto = dto;
			this.searchBean = searchBean;
		}

		public T getDto() {
			return dto;
		}

		public void setDto(T dto) {
			this.dto = dto;
		}

		public S getSearchBean() {
			return searchBean;
		}

		public void setSearchBean(S searchBean) {
			this.searchBean = searchBean;
		}
	}

	protected void assertAuditLogSize(final List<IdmAuditLogEntity> logs, final int expectedSize) {
		if(expectedSize == 0) {
			Assert.assertTrue(CollectionUtils.isEmpty(logs));
		} else {
			Assert.assertTrue(CollectionUtils.isNotEmpty(logs));
			Assert.assertEquals(expectedSize, logs.size());
		}
	}
	
	protected void assertCacheHit(final Date now, final String cacheName, final int numOfExpectedEntities) {
		sleep(1); /* wait for persist due to redis*/
		
		/* only way to confirm that there haven't been multiple puts() into the same cache */
		final AuditLogSearchBean auditLogSearchBean = new AuditLogSearchBean();
		auditLogSearchBean.setAction(AuditAction.CACHE_PUT.value());
		auditLogSearchBean.setFrom(now);
		auditLogSearchBean.addAttribute(AuditAttributeName.CACHE_NAME.name(), cacheName);
		List<IdmAuditLogEntity> logs = auditLogService.findBeans(auditLogSearchBean, 0, Integer.MAX_VALUE);
		assertAuditLogSize(logs, 1);
		Assert.assertEquals(Integer.toString(numOfExpectedEntities), logs.get(0).get(AuditAttributeName.NUM_OF_MULTIKEYS.name()));
	}
	
	protected void assertCachePurge(final Date now, final String cacheName, final int numOfExpectedEntities) {
		sleep(1); /* wait for persist due to redis*/
		
		/* only way to confirm that there haven't been multiple puts() into the same cache */
		final AuditLogSearchBean auditLogSearchBean = new AuditLogSearchBean();
		auditLogSearchBean.setAction(AuditAction.CACHE_EVICT.value());
		auditLogSearchBean.setFrom(now);
		auditLogSearchBean.addAttribute(AuditAttributeName.CACHE_NAME.name(), cacheName);
		List<IdmAuditLogEntity> logs = auditLogService.findBeans(auditLogSearchBean, 0, Integer.MAX_VALUE);
		assertAuditLogSize(logs, numOfExpectedEntities);
		final int numOfMultikies = logs.stream().map(e -> e.get(AuditAttributeName.NUM_OF_MULTIKEYS.name()))
			.filter(e -> StringUtils.isNotBlank(e)).mapToInt(e -> Integer.valueOf(e)).sum();
		Assert.assertEquals(numOfExpectedEntities, numOfMultikies);
	}
	
	protected void saveAndAssertCachePurge(final S sb, final T entity, final String[] cacheNames, final int numOfExpectedEntities) {
		final Date now = new Date();
		save(entity);
		for(final String cacheName : cacheNames) {
			assertCachePurge(now, cacheName, numOfExpectedEntities);
		}
	}
	
	protected void searchAndAssertCacheHit(final S sb, final T entity, final String cacheName) {
		final Date now = new Date();
		List<T> orgs = null;
		for(int i = 0; i < 3; i++) {
			orgs = find(sb, 0, Integer.MAX_VALUE);
			Assert.assertNotNull(orgs);
			Assert.assertEquals(entity.getId(), orgs.get(0).getId());
		}
		assertCacheHit(now, cacheName, orgs.size());
	}
	
}
