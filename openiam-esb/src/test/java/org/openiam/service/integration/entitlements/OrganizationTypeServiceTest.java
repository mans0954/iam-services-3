package org.openiam.service.integration.entitlements;

import java.util.List;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.org.dto.OrganizationType;
import org.openiam.idm.srvc.org.service.OrganizationTypeDataService;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class OrganizationTypeServiceTest extends AbstractKeyNameServiceTest<OrganizationType, OrganizationTypeSearchBean> {
	
	@Autowired
	@Qualifier("organizationTypeClient")
	private OrganizationTypeDataService organizationTypeClient;

	@Override
	protected OrganizationType newInstance() {
		final OrganizationType type = new OrganizationType();
		type.setName(getRandomName());
		return type;
	}

	@Override
	protected OrganizationTypeSearchBean newSearchBean() {
		return new OrganizationTypeSearchBean();
	}

	@Override
	protected Response save(OrganizationType t) {
		return organizationTypeClient.save(t);
	}

	@Override
	protected Response delete(OrganizationType t) {
		return organizationTypeClient.delete(t.getId());
	}

	@Override
	protected OrganizationType get(String key) {
		return organizationTypeClient.findByIdLocalized(key, getDefaultLanguage());
	}

	@Override
	public List<OrganizationType> find(OrganizationTypeSearchBean searchBean,
			int from, int size) {
		return organizationTypeClient.findBeans(searchBean, from, size, getDefaultLanguage());
	}

}
