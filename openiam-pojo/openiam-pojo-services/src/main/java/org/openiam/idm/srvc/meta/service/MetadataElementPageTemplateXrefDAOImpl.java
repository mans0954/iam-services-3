package org.openiam.idm.srvc.meta.service;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.pk.MetadataElementPageTemplateXrefIdEntity;
import org.springframework.stereotype.Repository;

@Repository("metadataElementPageTemplateXrefDAO")
public class MetadataElementPageTemplateXrefDAOImpl 
	extends BaseDaoImpl<MetadataElementPageTemplateXrefEntity, MetadataElementPageTemplateXrefIdEntity> 
		implements MetadataElementPageTemplateXrefDAO {

	@Override
	protected String getPKfieldName() {
		return "id";
	}

}
