package org.openiam.service.integration;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.KeyDTO;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.testng.Assert;
import org.testng.annotations.Test;

public abstract class AbstractKeyServiceTest<T, S extends AbstractSearchBean<T, String>> extends AbstractServiceTest {
	protected abstract T newInstance();
	protected abstract S newSearchBean();
	protected abstract Response save(T t) throws Exception;
	protected abstract Response delete(T t);
	protected abstract T get(final String key);
	public abstract List<T> find(final S searchBean, final int from, final int size);

	protected abstract String getId(T bean);
	protected abstract void setId(T bean, String id);
	
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

	protected Response saveAndAssert(T t) throws Exception {
		final Response response = save(t);
		Assert.assertTrue(response.isSuccess(), String.format("Could not save entity.  %s", response));
		return response;
	}
	
	protected Response deleteAndAssert(final T instance) {
		Response response = delete(instance);
		Assert.assertTrue(response.isSuccess(), String.format("Could not delete element '%s' with ID '%s.  Response: %s", instance, getId(instance), response));
		return response;
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
}
