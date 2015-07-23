package org.openiam.service.integration.access;

import java.util.List;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AccessRightSearchBean;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.access.ws.AccessRightDataService;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

public class AccessRightServiceTest extends AbstractKeyNameServiceTest<AccessRight, AccessRightSearchBean> {

	@Autowired
	@Qualifier("accessRightServiceClient")
	private AccessRightDataService accessRightServiceClient;
	
	@Override
	protected AccessRight newInstance() {
		final AccessRight instance = new AccessRight();
		return instance;
	}

	@Override
	protected AccessRightSearchBean newSearchBean() {
		final AccessRightSearchBean sb = new AccessRightSearchBean();
		return sb;
	}

	@Override
	protected Response save(AccessRight t) {
		return accessRightServiceClient.save(t);
	}

	@Override
	protected Response delete(AccessRight t) {
		return accessRightServiceClient.delete(t.getId());
	}

	@Override
	protected AccessRight get(String key) {
		return accessRightServiceClient.get(key);
	}

	@Override
	public List<AccessRight> find(AccessRightSearchBean searchBean, int from,
			int size) {
		return accessRightServiceClient.findBeans(searchBean, from, size, getDefaultLanguage());
	}

	@Test
	public void foo() {}
}
