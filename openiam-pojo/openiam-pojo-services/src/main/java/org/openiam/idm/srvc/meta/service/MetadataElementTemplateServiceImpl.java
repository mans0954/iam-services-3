package org.openiam.idm.srvc.meta.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("metadataElementTemplateService")
public class MetadataElementTemplateServiceImpl implements MetadataElementTemplateService {
	
	@Autowired
	private MetadataElementPageTemplateDAO pageTemplateDAO;

	@Override
	public List<MetadataElementPageTemplateEntity> findBeans(final MetadataElementPageTemplateEntity entity, final int from, final int size) {
		return pageTemplateDAO.getByExample(entity, from, size);
	}

	@Override
	public int count(final MetadataElementPageTemplateEntity entity) {
		return pageTemplateDAO.count(entity);
	}

	@Override
	public void save(final MetadataElementPageTemplateEntity template) {
		if(StringUtils.isNotBlank(template.getId())) {
			
		}
	}

	@Override
	public void delete(final String id) {
		final MetadataElementPageTemplateEntity entity = pageTemplateDAO.findById(id);
		if(entity != null) {
			pageTemplateDAO.delete(entity);
		}
	}

}
