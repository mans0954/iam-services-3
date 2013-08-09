package org.openiam.idm.srvc.searchbean.converter;

import org.openiam.idm.searchbeans.AbstractSearchBean;

public interface SearchBeanConverter<T, K extends AbstractSearchBean> {

	public T convert(K searchBean);
}
