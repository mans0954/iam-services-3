package org.openiam.idm.srvc.access.service;

import java.util.Collection;
import java.util.List;

import org.openiam.idm.searchbeans.AccessRightSearchBean;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.access.dto.AccessRight;

public interface AccessRightService {

	void save(AccessRightEntity entity);
	void delete(String id);
	AccessRightEntity get(String id);
	List<AccessRightEntity> findBeans(final AccessRightSearchBean searchBean, final int from, final int size);
	int count(AccessRightSearchBean searchBean);
	List<AccessRightEntity> findByIds(Collection<String> ids);
}
