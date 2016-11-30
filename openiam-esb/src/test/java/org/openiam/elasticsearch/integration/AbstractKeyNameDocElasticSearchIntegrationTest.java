package org.openiam.elasticsearch.integration;

import org.apache.commons.lang.RandomStringUtils;
import org.openiam.base.KeyNameDTO;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchParam;
import org.openiam.elasticsearch.model.AbstractKeyNameDoc;
import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;
import org.testng.annotations.Test;

public abstract class AbstractKeyNameDocElasticSearchIntegrationTest<D extends AbstractKeyNameDoc, S extends AbstractKeyNameSearchBean, DTO extends KeyNameDTO> 
				extends AbstractKeyDocElasticSearchIntegrationTest<D, S, DTO>{
	

	@Override
	protected DTO createDTO() {
		final DTO dto = super.createDTO();
		dto.setName(RandomStringUtils.randomAlphanumeric(10));
		return dto;
	}

	@Test
	public void testFindByNameStartingWith() {
		final S sb = super.createSearchBean();
		sb.setNameToken(new SearchParam(getDTO().getName().substring(0, dtoName().length() / 2), MatchType.STARTS_WITH));
		assertFindBeans(sb);
	}
	
	@Test
	public void testFindByNameEndingWith() {
		final S sb = super.createSearchBean();
		sb.setNameToken(new SearchParam(getDTO().getName().substring(dtoName().length() / 2, dtoName().length()), MatchType.END_WITH));
		assertFindBeans(sb);
	}
	
	@Test
	public void testFindByNameContaining() {
		final S sb = super.createSearchBean();
		sb.setNameToken(new SearchParam(getDTO().getName().substring(dtoName().length() / 4, (int)(dtoName().length() / 1.5)), MatchType.CONTAINS));
		assertFindBeans(sb);
	}
	
	@Test
	public void testFindByNameExact() {
		final S sb = super.createSearchBean();
		sb.setNameToken(new SearchParam(getDTO().getName(), MatchType.EXACT));
		assertFindBeans(sb);
	}
	
	private String dtoName() {
		return getDTO().getName();
	}
}
