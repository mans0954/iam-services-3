package org.openiam.elasticsearch.dao;

import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class OrganizationElasticSearchRepositoryTest extends AbstractElasticSearchRepositoryTest<OrganizationEntity, OrganizationElasticSearchRepository, OrganizationDAO> {

	@Autowired
	private OrganizationElasticSearchRepository repo;
	
	@Autowired
	private OrganizationDAO dao;
	
	@Override
	public OrganizationElasticSearchRepository getRepository() {
		return repo;
	}

	@Override
	public OrganizationDAO getDAO() {
		return dao;
	}

	@Test
	public void test() {}
}
