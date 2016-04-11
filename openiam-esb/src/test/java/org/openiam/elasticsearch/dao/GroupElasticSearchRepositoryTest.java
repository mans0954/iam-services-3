package org.openiam.elasticsearch.dao;

import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class GroupElasticSearchRepositoryTest extends AbstractElasticSearchRepositoryTest<GroupEntity, GroupElasticSearchRepository, GroupDAO> {

	@Autowired
	private GroupElasticSearchRepository repo;
	
	@Autowired
	private GroupDAO dao;
	
	@Override
	public GroupElasticSearchRepository getRepository() {
		return repo;
	}

	@Override
	public GroupDAO getDAO() {
		return dao;
	}

	@Test
	public void test() {}
}
