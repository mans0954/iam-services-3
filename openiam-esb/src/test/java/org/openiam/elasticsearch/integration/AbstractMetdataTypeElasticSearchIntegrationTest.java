package org.openiam.elasticsearch.integration;

import org.openiam.elasticsearch.model.AbstractMetadataTypeDoc;
import org.openiam.idm.searchbeans.EntitlementsSearchBean;
import org.openiam.idm.srvc.entitlements.AbstractEntitlementsDTO;
import org.testng.annotations.Test;

public abstract class AbstractMetdataTypeElasticSearchIntegrationTest<D extends AbstractMetadataTypeDoc, S extends EntitlementsSearchBean, DTO extends AbstractEntitlementsDTO> 
				extends AbstractKeyNameDocElasticSearchIntegrationTest<D, S, DTO> {

	
	
	@Override
	protected DTO createDTO() {
		final DTO dto = super.createDTO();
		dto.setMdTypeId(getRandomMetadataType().getId());
		return dto;
	}

	@Test
	public void testFindByMetadataType() {
		final S sb = super.createSearchBean();
		sb.setMetadataType(getDTO().getMdTypeId());
		assertFindBeans(sb);
	}
}
