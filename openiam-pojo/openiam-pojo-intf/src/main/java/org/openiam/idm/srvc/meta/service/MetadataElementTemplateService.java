package org.openiam.idm.srvc.meta.service;

import java.util.List;

import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;

public interface MetadataElementTemplateService {

	public List<MetadataElementPageTemplateEntity> findBeans(final MetadataElementPageTemplateSearchBean searchBean, final int from, final int size);
	public int count(final MetadataElementPageTemplateSearchBean searchBean);
	public void save(final MetadataElementPageTemplateEntity template);
	public void delete(final String id);
}
