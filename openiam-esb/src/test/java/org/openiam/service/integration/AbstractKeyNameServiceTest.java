package org.openiam.service.integration;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.openiam.base.KeyNameDTO;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public abstract class AbstractKeyNameServiceTest<T extends KeyNameDTO, S extends AbstractKeyNameSearchBean<T, String>> extends AbstractTestNGSpringContextTests {

	protected abstract T newInstance();
	protected abstract S newSearchBean();
	protected abstract Response save(T t);
	protected abstract Response delete(T t);
	protected abstract T get(final String key);
	public abstract List<T> find(final S searchBean, final int from, final int size);
	
	protected T createBean() {
		final T bean = newInstance();
		bean.setName(getRandomName());
		return bean;
	}
	
	protected String getRandomName() {
		return RandomStringUtils.randomAlphanumeric(5);
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
    		delete(instance);
    	}
	}
	
	public ClusterKey<T, S> doClusterTest() throws Exception {
		/* create and save */
		T instance = createBean();
		Response response = saveAndAssert(instance);
		instance.setId((String)response.getResponseValue());
		
		/* find */
		final S searchBean = newSearchBean();
		searchBean.setDeepCopy(false);
    	searchBean.setName(instance.getName());
    	
    	/* confirm save on both nodes */
    	instance = assertClusteredSave(searchBean);
    	
    	/* change name */
    	instance.setName(getRandomName());
    	response = saveAndAssert(instance);
    	
    	/* confirm update went through on both nodes */
    	searchBean.setName(instance.getName());
    	instance = assertClusteredSave(searchBean);
    	return new ClusterKey<T, S>(instance, searchBean);
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
}
