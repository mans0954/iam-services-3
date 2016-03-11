package org.openiam.elasticsearch.dao;

import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class ResourceElasticSearchRepositoryTest extends AbstractElasticSearchRepositoryTest<ResourceEntity, ResourceElasticSearchRepository, ResourceDAO> {

	@Autowired
	private ResourceElasticSearchRepository repo;
	
	@Autowired
	private ResourceDAO dao;
	
	@Override
	public ResourceElasticSearchRepository getRepository() {
		return repo;
	}

	@Override
	public ResourceDAO getDAO() {
		return dao;
	}

	@Test
	public void test() {}
}
