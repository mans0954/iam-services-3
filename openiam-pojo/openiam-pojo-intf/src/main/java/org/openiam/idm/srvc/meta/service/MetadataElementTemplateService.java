package org.openiam.idm.srvc.meta.service;

import java.util.List;

import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;

public interface MetadataElementTemplateService {

	public List<MetadataElementPageTemplateEntity> findBeans(final MetadataElementPageTemplateEntity entity, final int from, final int size);
	public int count(final MetadataElementPageTemplateEntity entity);
	public void save(final MetadataElementPageTemplateEntity template);
	public void delete(final String id);
}
