package org.openiam.idm.srvc.meta.service;

import java.util.Collection;
import java.util.List;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataFieldTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.MetadataFieldTemplateXrefIDEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;
import org.springframework.stereotype.Repository;

@Repository
public class MetadataFieldTemplateXrefDAOImpl extends BaseDaoImpl<MetadataFieldTemplateXrefEntity, MetadataFieldTemplateXrefIDEntity> implements MetadataFieldTemplateXrefDAO {

	@Override
	protected String getPKfieldName() {
		return "id";
	}

}
