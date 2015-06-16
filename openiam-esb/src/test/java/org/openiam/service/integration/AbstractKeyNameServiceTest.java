package org.openiam.service.integration;

import java.util.List;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AbstractSearchBean;

public abstract class AbstractKeyNameServiceTest<T, S extends AbstractSearchBean<T, String>> extends AbstractKeyServiceTest<T, S> {

	protected abstract T newInstance();
	protected abstract S newSearchBean();
	protected abstract Response save(T t) throws Exception;
	protected abstract Response delete(T t);
	protected abstract T get(final String key);
	public abstract List<T> find(final S searchBean, final int from, final int size);


	protected abstract void setName(T bean, String name);
	protected abstract String getName(T bean);

	protected abstract void setNameForSearch(S searchBean, String name);
//	protected abstract void setDeepCopy(S searchBean, boolean deepCopy);

	@Override
	protected T createBean() {
		final T bean = super.createBean();
		setName(bean, getRandomName());
		return bean;
	}
	
	public ClusterKey<T, S> doClusterTest() throws Exception {
		/* create and save */
		T instance = createBean();
		Response response = saveAndAssert(instance);
		setId(instance, (String) response.getResponseValue());

		/* find */
		final S searchBean = newSearchBean();
		searchBean.setDeepCopy(useDeepCopyOnFindBeans());
		setNameForSearch(searchBean, getName(instance));

    	/* confirm save on both nodes */
    	instance = assertClusteredSave(searchBean);

    	/* change name */
    	setName(instance, getRandomName());
    	response = saveAndAssert(instance);

    	/* confirm update went through on both nodes */
		setNameForSearch(searchBean, getName(instance));
    	instance = assertClusteredSave(searchBean);
    	return new ClusterKey<T, S>(instance, searchBean);
	}
}
