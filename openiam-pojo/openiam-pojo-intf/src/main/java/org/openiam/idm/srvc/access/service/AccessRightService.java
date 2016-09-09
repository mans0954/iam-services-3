package org.openiam.idm.srvc.access.service;

import java.util.Collection;
import java.util.List;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AccessRightSearchBean;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.lang.dto.Language;

public interface AccessRightService {

	String save(AccessRight entity) throws BasicDataServiceException;
	void delete(String id) throws BasicDataServiceException;
	AccessRight get(String id);
	List<AccessRight> findBeans(final AccessRightSearchBean searchBean, final int from, final int size, final Language language);
	int count(AccessRightSearchBean searchBean);
	List<AccessRight> findByIds(Collection<String> ids);
}
