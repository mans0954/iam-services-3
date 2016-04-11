package org.openiam.elasticsearch.dao;

import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class RoleElasticSearchRepositoryTest extends AbstractElasticSearchRepositoryTest<RoleEntity, RoleElasticSearchRepository, RoleDAO> {

	@Autowired
	private RoleDAO roleDAO;
	
	@Autowired
	private RoleElasticSearchRepository repo;
	
	@Override
	public RoleElasticSearchRepository getRepository() {
		return repo;
	}

	@Override
	public RoleDAO getDAO() {
		return roleDAO;
	}

	@Test
	public void test() {}
}
