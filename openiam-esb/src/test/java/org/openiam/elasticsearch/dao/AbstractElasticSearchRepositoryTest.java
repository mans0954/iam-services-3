package org.openiam.elasticsearch.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.runner.RunWith;
import org.openiam.base.BaseIdentity;
import org.openiam.config.UnitTestConfig;
import org.openiam.core.dao.BaseDao;
import org.openiam.elasticsearch.service.ElasticsearchReindexProcessor;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.srvc.common.MetadataWebService;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({UnitTestConfig.class})
@Transactional
@Rollback(true)
public abstract class AbstractElasticSearchRepositoryTest<E extends BaseIdentity, R extends OpeniamElasticSearchRepository, D extends BaseDao> 
			  extends AbstractTransactionalTestNGSpringContextTests {

	@Autowired
	protected ElasticsearchReindexProcessor reindexer;
	
	@Autowired
	private MetadataWebService metadataService;
	

	@Autowired
	protected UserDAO userDAO;
	
	@Autowired
	protected MetadataTypeDAO metadataTypeDAO;
	
	@Autowired
	@Qualifier("transactionManager")
	protected PlatformTransactionManager transactionManager;
	
	public abstract R getRepository();
	public abstract D getDAO();
	
	protected List<MetadataTypeEntity> getMetadataTypesByGrouping(final MetadataTypeGrouping grouping) {
    	final MetadataTypeSearchBean searchBean = new MetadataTypeSearchBean();
    	searchBean.setGrouping(grouping);
    	searchBean.setActive(true);
    	return metadataTypeDAO.getByExample(searchBean);
    }
	
	protected String randomString() {
		return RandomStringUtils.randomAlphanumeric(5);
	}
	
	@Test
	public void testReindex() {
		//getRepository().deleteAll();
		Assert.assertTrue(reindexer.reindex(getRepository().getDocumentClass()) > 0);
	}
	
	@Test
	public void testFindOne() {
		//reindexer.reindex(getRepository().getEntityClass());
		final List<E> entities = getDAO().find(0, 1);
		Assert.assertTrue(CollectionUtils.isNotEmpty(entities));
		final E dbEntity = entities.get(0);
		final Iterable<E> all = getRepository().findAll();
		final E repoEntity = (E)getRepository().findOne(dbEntity.getId());
		Assert.assertNotNull(repoEntity);
		Assert.assertNotNull(dbEntity);
		Assert.assertEquals(repoEntity.getId(), dbEntity.getId());
	}
}
