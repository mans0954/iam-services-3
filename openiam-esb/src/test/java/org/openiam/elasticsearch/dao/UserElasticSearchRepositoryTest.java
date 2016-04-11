package org.openiam.elasticsearch.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchParam;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UserElasticSearchRepositoryTest extends AbstractElasticSearchRepositoryTest<UserEntity, UserElasticSearchRepository, UserDAO> {

	@Autowired
	private UserElasticSearchRepository repo;
	
	@Autowired
	private UserDAO dao;
	
	@Override
	public UserElasticSearchRepository getRepository() {
		return repo;
	}

	@Override
	public UserDAO getDAO() {
		return dao;
	}
	
	private UserEntity user = null;
	
	@BeforeClass
	public void init() throws InterruptedException {
		final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
        transactionTemplate.execute(new TransactionCallback<Void>() {

			@Override
			public Void doInTransaction(TransactionStatus status) {
				user = new UserEntity();
				user.setFirstName(random());
				user.setLastName(String.format("%s %s", random(), random()));
				user.setMaidenName(random());
				user.setEmployeeId(random());
				user.setStatus(UserStatusEnum.LEAVE);
				user.setSecondaryStatus(UserStatusEnum.PENDING_START_DATE);
				user.setJobCode(getMetadataTypesByGrouping(MetadataTypeGrouping.JOB_CODE).get(0));
				user.setEmployeeType(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE).get(0));
				user.setType(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_TYPE).get(0));
				dao.save(user);
				return null;
			}
        });
        Thread.sleep(5000L);
	}
	
	private String random() {
		return RandomStringUtils.randomAlphanumeric(10);
	}

	@Test
	public void testFindEmpty() {
		List<String> page = repo.findIds(null, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isEmpty(page));
		
		final UserSearchBean sb = new UserSearchBean();
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isEmpty(page));
		
		sb.setFirstNameMatchToken(new SearchParam(random(), MatchType.STARTS_WITH));
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isEmpty(page));
	}
	
	@Test
	public void testFindNotEmpty() {
		final UserSearchBean sb = new UserSearchBean();
		sb.setFirstNameMatchToken(new SearchParam(user.getFirstName().substring(0, 5), MatchType.STARTS_WITH));
		List<String> page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
		
		sb.setFirstNameMatchToken(new SearchParam(user.getFirstName().substring(5, 10), MatchType.END_WITH));
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
		
		sb.setFirstNameMatchToken(new SearchParam(user.getFirstName(), MatchType.EXACT));
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
		
		
		
		
		
		sb.setLastNameMatchToken(new SearchParam(user.getLastName().substring(0, 5), MatchType.STARTS_WITH));
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
		
		sb.setLastNameMatchToken(new SearchParam(user.getLastName().substring(5, 10), MatchType.END_WITH));
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
		
		sb.setLastNameMatchToken(new SearchParam(user.getLastName(), MatchType.EXACT));
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
		
		
		
		
		
		sb.setMaidenNameMatchToken(new SearchParam(user.getMaidenName().substring(0, 5), MatchType.STARTS_WITH));
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
		
		sb.setMaidenNameMatchToken(new SearchParam(user.getMaidenName().substring(5, 10), MatchType.END_WITH));
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
		
		sb.setMaidenNameMatchToken(new SearchParam(user.getMaidenName(), MatchType.EXACT));
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
		
		
		
		
		sb.setEmployeeIdMatchToken(new SearchParam(user.getEmployeeId().substring(0, 5), MatchType.STARTS_WITH));
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
		
		sb.setEmployeeIdMatchToken(new SearchParam(user.getEmployeeId().substring(5, 10), MatchType.END_WITH));
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
		
		sb.setEmployeeIdMatchToken(new SearchParam(user.getEmployeeId(), MatchType.EXACT));
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());

		sb.setUserStatus(user.getStatus().getValue());
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
		
		sb.setAccountStatus(user.getSecondaryStatus().getValue());
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
		
		sb.setJobCode(user.getJobCode().getId());
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
		
		sb.setEmployeeType(user.getEmployeeType().getId());
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
		
		sb.setUserType(user.getType().getId());
		page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
	}
	
	@Test
	public void testMultipleSearchTerms() {
		final int endIndex = user.getLastName().split(" ")[0].length() + (user.getLastName().split(" ")[1].length() / 2);
		final String searchTerm = user.getLastName().substring(0, endIndex);
		
		final UserSearchBean sb = new UserSearchBean();
		sb.setLastNameMatchToken(new SearchParam(searchTerm, MatchType.STARTS_WITH));
		List<String> page = page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
		
		sb.setLastNameMatchToken(new SearchParam(searchTerm, MatchType.CONTAINS));
		page = page = repo.findIds(sb, new PageRequest(0, 10));
		Assert.assertTrue(CollectionUtils.isNotEmpty(page));
		Assert.assertEquals(page.get(0), user.getId());
	}
}
