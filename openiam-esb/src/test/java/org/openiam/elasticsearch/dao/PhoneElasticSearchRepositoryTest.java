package org.openiam.elasticsearch.dao;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.searchbeans.PhoneSearchBean;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.service.PhoneDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PhoneElasticSearchRepositoryTest extends AbstractElasticSearchRepositoryTest<PhoneEntity, PhoneElasticSearchRepository, PhoneDAO> {

	@Autowired
	private PhoneDAO dao;
	
	@Autowired
	private PhoneElasticSearchRepository repo;
	
	@BeforeClass
	public void init() throws InterruptedException {
		final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
        transactionTemplate.execute(new TransactionCallback<Void>() {

			@Override
			public Void doInTransaction(TransactionStatus status) {
				
				final PhoneEntity entity1 = new PhoneEntity();
				entity1.setAreaCd("abcd");
				entity1.setPhoneNbr("12345");
				entity1.setParent(userDAO.findById("3000"));
				getDAO().save(entity1);
				
				final PhoneEntity entity2 = new PhoneEntity();
				entity2.setAreaCd("efgh");
				entity2.setPhoneNbr("67890");
				entity2.setParent(userDAO.findById("3000"));
				getDAO().save(entity2);
				
				return null;
			}
        });
        Thread.sleep(5000L);
	}
	
	@Override
	public PhoneElasticSearchRepository getRepository() {
		return repo;
	}

	@Override
	public PhoneDAO getDAO() {
		return dao;
	}

	@Test
	public void testFindByUserIdsEmpty() {
		Page<String> page = repo.findUserIds(null, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
		
		page = repo.findUserIds(new PhoneSearchBean(), new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
	}
	
	@Test
	public void testFindByUserIdsNotEmpty() {
		final PhoneSearchBean sb = new PhoneSearchBean();
		sb.setPhoneAreaCd("xxx");
		Page<String> page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
		
		
		sb.setPhoneAreaCd("a");
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
		
		sb.setPhoneAreaCd("ab");
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
		
		sb.setPhoneAreaCd("abcd");
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		
		sb.setPhoneAreaCd("abcd");
		sb.setPhoneNbr("12");
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		
		
		sb.setPhoneAreaCd("abcd");
		sb.setPhoneNbr("1234");
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		
		sb.setPhoneAreaCd(null);
		sb.setPhoneNbr("1234");
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		
		sb.setPhoneAreaCd("abcd");
		sb.setPhoneNbr("5");
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isEmpty(page.getContent()));
		
		sb.setPhoneAreaCd("abcd");
		sb.setPhoneNbr("12345");
		page = repo.findUserIds(sb, new PageRequest(0, 10));
		Assert.assertNotNull(page);
		Assert.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
	}
}
