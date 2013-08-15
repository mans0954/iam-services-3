package org.openiam.idm.srvc.meta.service;

import java.util.List;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;

public interface MetadataElementPageTemplateDAO extends BaseDao<MetadataElementPageTemplateEntity, String> {

	public List<MetadataElementPageTemplateEntity> getByResourceId(final String resourceId);
}
