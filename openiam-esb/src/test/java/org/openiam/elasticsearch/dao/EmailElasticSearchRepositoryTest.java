package org.openiam.elasticsearch.dao;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchParam;
import org.openiam.idm.searchbeans.EmailSearchBean;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.service.EmailAddressDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class EmailElasticSearchRepositoryTest extends AbstractElasticSearchRepositoryTest<EmailAddressEntity, EmailElasticSearchRepository, EmailAddressDAO> {

	@Autowired
	private EmailElasticSearchRepository repo;
	
	@Autowired
	private EmailAddressDAO dao;
	
	private EmailAddressEntity entity;
	
	@BeforeClass
	public void init() throws InterruptedException {
		final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
        entity = transactionTemplate.execute(new TransactionCallback<EmailAddressEntity>() {

			@Override
			public EmailAddressEntity doInTransaction(TransactionStatus status) {
				final EmailAddressEntity entity = new EmailAddressEntity();
				entity.setParent(userDAO.findById("3000"));
				entity.setEmailAddress("foo.bar@gmail.com");
				entity.setType(metadataTypeDAO.findById("HOME_ADDRESS"));
				getDAO().save(entity);
				return entity;
			}
        });
        Assert.assertNotNull(entity);
        Thread.sleep(5000L);
        Assert.assertNotNull(getRepository().findOne(entity.getId()));
	}
	
	@Override
	public EmailElasticSearchRepository getRepository() {
		return repo;
	}

	@Override
	public EmailAddressDAO getDAO() {
		return dao;
	}

	@Test
	public void testFindByUserIdsEmpty() {
		Page<String> page = repo.findUserIds(null, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
		
		page = repo.findUserIds(new EmailSearchBean(), new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
	}
	
	@Test
	public void testFindByUserIdsNotEmpty() {
		final EmailSearchBean sb = new EmailSearchBean();
		sb.setEmailMatchToken(new SearchParam("foo", MatchType.STARTS_WITH));
		Page<String> page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		
		sb.setEmailMatchToken(new SearchParam("foo", MatchType.CONTAINS));
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		
		sb.setEmailMatchToken(new SearchParam("foo", MatchType.END_WITH));
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
		
		sb.setEmailMatchToken(new SearchParam("bar", MatchType.END_WITH));
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		
		sb.setEmailMatchToken(new SearchParam("bar", MatchType.CONTAINS));
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		
		sb.setEmailMatchToken(new SearchParam("bar", MatchType.STARTS_WITH));
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
		
		sb.setEmailMatchToken(new SearchParam("bar", MatchType.EXACT));
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
		
		sb.setEmailMatchToken(new SearchParam("foo.bar@gmail.com", MatchType.EXACT));
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
	}
}
