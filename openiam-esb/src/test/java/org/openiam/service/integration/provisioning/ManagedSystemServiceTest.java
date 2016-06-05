package org.openiam.service.integration.provisioning;


import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.ManagedSysSearchBean;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.openiam.service.integration.AbstractServiceTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

public class ManagedSystemServiceTest extends AbstractKeyNameServiceTest<ManagedSysDto, ManagedSysSearchBean> {

	@Override
	protected ManagedSysDto newInstance() {
		final ManagedSysDto dto = new ManagedSysDto();
		dto.setConnectorId(provisionConnectorWebServiceClient.getProvisionConnectors(null, 0, 1).get(0).getId());
		return dto;
	}

	@Override
	protected ManagedSysSearchBean newSearchBean() {
		return new ManagedSysSearchBean();
	}

	@Override
	protected Response save(final ManagedSysDto t) {
		return managedSysServiceClient.saveManagedSystem(t);
	}

	@Override
	protected Response delete(ManagedSysDto t) {
		return managedSysServiceClient.removeManagedSystem(t.getId());
	}

	@Override
	protected ManagedSysDto get(String key) {
		final ManagedSysDto dto = managedSysServiceClient.getManagedSys(key);
		return dto;
	}

	@Override
	public List<ManagedSysDto> find(final ManagedSysSearchBean searchBean, int from,
			int size) {
		final List<ManagedSysDto> list = managedSysServiceClient.getManagedSystems(searchBean, from, size);
		return list;
	}

	@Test
	public void saveManagedSysObjectMatch() {
		ManagedSysDto dto = createBean();
		Response wsResponse = saveAndAssert(dto);
		dto = get((String)wsResponse.getResponseValue());
		try {
			final ManagedSystemObjectMatch matchObj = new ManagedSystemObjectMatch();
	        matchObj.setBaseDn(getRandomName());
	        matchObj.setSearchBaseDn(getRandomName());
	        matchObj.setKeyField(getRandomName());
	        matchObj.setObjectType(ManagedSystemObjectMatch.USER);
	        matchObj.setSearchFilter(getRandomName());
	        matchObj.setManagedSys(dto.getId());
	        wsResponse = managedSysServiceClient.saveManagedSystemObjectMatch(matchObj);
	        assertSuccess(wsResponse);
	        dto = get(dto.getId());
	        Assert.assertTrue(CollectionUtils.isNotEmpty(dto.getMngSysObjectMatchs()));
	        matchObj.setId((String)wsResponse.getResponseValue());
	        wsResponse = managedSysServiceClient.saveManagedSystemObjectMatch(matchObj);
	        assertSuccess(wsResponse);
	        Assert.assertTrue(CollectionUtils.isNotEmpty(dto.getMngSysObjectMatchs()));
		} finally {
			if(dto != null) {
				assertSuccess(delete(dto));
			}
		}
	}
}
