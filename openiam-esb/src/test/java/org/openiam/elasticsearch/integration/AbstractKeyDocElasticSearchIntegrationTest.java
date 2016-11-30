package org.openiam.elasticsearch.integration;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.openiam.base.KeyDTO;
import org.openiam.elasticsearch.model.AbstractKeyDoc;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.service.integration.AbstractServiceTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public abstract class AbstractKeyDocElasticSearchIntegrationTest<D extends AbstractKeyDoc, S extends AbstractSearchBean, DTO extends KeyDTO> extends AbstractServiceTest {

	private DTO dto = null;

	@BeforeClass
	public void before() {
		dto = createDTO();
		int numOfAttempts = 0;
		while(!isIndexed(dto.getId())) {
			sleep(5);
			if(numOfAttempts++ >= 5) {
				Assert.fail(String.format("Entity %s not indexed in ElasticSearch - can't continue", dto));
			}
		}
	}
	
	@AfterClass
	public void after() {
		if(dto != null && dto.getId() != null) {
			delete(dto);
		}
	}
	
	protected S createSearchBean() {
		S searchBean;
		try {
			searchBean = getSearchBeanClass().newInstance();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		searchBean.setUseElasticSearch(true);
		searchBean.setLanguage(getDefaultLanguage());
		return searchBean;
	}
	
	protected DTO createDTO() {
		DTO dto = null;
		try {
			dto = getDTOClass().newInstance();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		return dto;
	}
	
	protected abstract boolean isIndexed(String id);
	protected abstract void delete(DTO dto);
	protected abstract Class<S> getSearchBeanClass();
	protected abstract Class<D> getDocumentClass();
	protected abstract Class<DTO> getDTOClass();
	protected abstract List<DTO> findBeans(S searchBean);
	protected DTO getDTO() {
		return dto;
	}
	
	protected void assertFindBeans(final S sb) {
		final List<DTO> dtos = findBeans(sb);
		Assert.assertTrue(CollectionUtils.isNotEmpty(dtos));
		Assert.assertTrue(dtos.stream().filter(e -> e.getId().equals(getDTO().getId())).count() > 0);
	}
	
	@Test
	public void testFindById() {
		final S sb = createSearchBean();
		sb.addKey(getDTO().getId());
		assertFindBeans(sb);
	}
}
