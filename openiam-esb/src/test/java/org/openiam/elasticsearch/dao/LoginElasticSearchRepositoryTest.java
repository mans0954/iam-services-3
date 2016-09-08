package org.openiam.elasticsearch.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchParam;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LoginElasticSearchRepositoryTest extends AbstractElasticSearchRepositoryTest<LoginEntity, LoginElasticSearchRepository, LoginDAO> {

	@Autowired
	private LoginElasticSearchRepository repo;
	
	@Autowired
	private LoginDAO dao;
	
	@Override
	public LoginElasticSearchRepository getRepository() {
		return repo;
	}

	@Override
	public LoginDAO getDAO() {
		return dao;
	}
	
	@BeforeClass
	public void init() {
        testReindex();
	}
	
	@Test
	public void testFindByUserId() {
		final List<LoginEntity> logins = repo.findByUserId("3000");
		Assert.assertTrue(CollectionUtils.isNotEmpty(logins));
	}

	@Test
	public void testFindByUserIdsEmpty() {
		Page<String> page = repo.findUserIds(null, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
		
		page = repo.findUserIds(new LoginSearchBean(), new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
	}
	
	@Test
	public void testFindByUserIdsNotEmpty() {
		final LoginSearchBean sb = new LoginSearchBean();
		sb.setLoginMatchToken(new SearchParam("1234", MatchType.STARTS_WITH));
		Page<String> page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
		
		sb.setLoginMatchToken(new SearchParam("1234", MatchType.END_WITH));
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
		
		sb.setLoginMatchToken(new SearchParam("1234", MatchType.EXACT));
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
		
		sb.setLoginMatchToken(new SearchParam("sys", MatchType.STARTS_WITH));
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		
		sb.setLoginMatchToken(new SearchParam("admin", MatchType.END_WITH));
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		
		sb.setLoginMatchToken(new SearchParam("sysadmin", MatchType.EXACT));
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		
		sb.setManagedSysId("a23");
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
		
		sb.setManagedSysId("0");
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		
		sb.setUserId("2345");
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
		
		sb.setUserId("3000");
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
	}
	
	@Test
	public void testCount() {
		LoginSearchBean sb = null;
		Assert.assertTrue(repo.count(sb) == 0);
		
		sb = new LoginSearchBean();
		Assert.assertTrue(repo.count(sb) == 0);
		
		sb = new LoginSearchBean();
		sb.setUserId("3000");
		Assert.assertTrue(repo.count(sb) > 0);
	}
	
	@Test
	public void testFindIds() {
		LoginSearchBean sb = null;
		Assert.assertTrue(CollectionUtils.isEmpty(repo.findIds(sb, 0, 10)));
		
		sb = new LoginSearchBean();
		Assert.assertTrue(CollectionUtils.isEmpty(repo.findIds(sb, 0, 10)));
		
		sb = new LoginSearchBean();
		sb.setUserId("3000");
		Assert.assertTrue(CollectionUtils.isNotEmpty(repo.findIds(sb, 0, 10)));
	}
}
