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
}
