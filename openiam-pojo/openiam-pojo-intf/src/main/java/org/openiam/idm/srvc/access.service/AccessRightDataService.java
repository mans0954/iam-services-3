package org.openiam.idm.srvc.access.service;

import java.util.Collection;
import java.util.List;

import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AccessRightSearchBean;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.lang.dto.Language;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/access/service", name = "AccessRightDataService")
public interface AccessRightDataService {

	Response save(AccessRight entity, String requesterId);
	Response delete(String id, String requesterId);
	AccessRight get(String id);
	List<AccessRight> findBeans(final AccessRightSearchBean searchBean, final int from, final int size, final Language language);
	public int count(final AccessRightSearchBean searchBean);
	List<AccessRight> getByIds(final Collection<String> ids);
}
