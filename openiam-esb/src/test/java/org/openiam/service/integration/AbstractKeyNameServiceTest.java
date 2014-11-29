package org.openiam.service.integration;

import java.util.List;

import org.openiam.base.KeyNameDTO;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;

public abstract class AbstractKeyNameServiceTest<T extends KeyNameDTO, S extends AbstractKeyNameSearchBean<T, String>> extends AbstractKeyServiceTest<T, S> {

	protected abstract T newInstance();
	protected abstract S newSearchBean();
	protected abstract Response save(T t);
	protected abstract Response delete(T t);
	protected abstract T get(final String key);
	public abstract List<T> find(final S searchBean, final int from, final int size);
	
	@Override
	protected T createBean() {
		final T bean = super.createBean();
		bean.setName(getRandomName());
		return bean;
	}
	
	public ClusterKey<T, S> doClusterTest() throws Exception {
		/* create and save */
		T instance = createBean();
		Response response = saveAndAssert(instance);
		instance.setId((String)response.getResponseValue());
		
		/* find */
		final S searchBean = newSearchBean();
		searchBean.setDeepCopy(useDeepCopyOnFindBeans());
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
}
