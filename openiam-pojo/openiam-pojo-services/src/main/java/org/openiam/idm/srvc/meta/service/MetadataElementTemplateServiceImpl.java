package org.openiam.idm.srvc.meta.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.dao.URIPatternDao;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.pk.MetadataElementPageTemplateXrefIdEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.searchbean.converter.MetadataElementTemplateSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("metadataElementTemplateService")
public class MetadataElementTemplateServiceImpl implements MetadataElementTemplateService {
	
	@Autowired
	private MetadataElementPageTemplateDAO pageTemplateDAO;
	
	@Autowired
	private MetadataElementPageTemplateXrefDAO xrefDAO;
	
	@Autowired
	private ResourceDAO resourceDAO;
	
	@Autowired
	private MetadataElementDAO elementDAO;
	
	@Autowired
	private ResourceTypeDAO resourceTypeDAO;
	
	@Autowired
	private URIPatternDao uriPatternDAO;
	
	@Autowired
	private MetadataElementTemplateSearchBeanConverter templateSearchBeanConverter;
	
	@Value("${org.openiam.resource.type.ui.template}")
    private String uiTemplateResourceType;

	@Override
	public List<MetadataElementPageTemplateEntity> findBeans(final MetadataElementPageTemplateSearchBean searchBean, final int from, final int size) {
		List<MetadataElementPageTemplateEntity> retVal = null;
		if(searchBean.hasMultipleKeys()) {
			retVal = pageTemplateDAO.findByIds(searchBean.getKeys());
		} else {
			final MetadataElementPageTemplateEntity entity = templateSearchBeanConverter.convert(searchBean);
			retVal = pageTemplateDAO.getByExample(entity, from, size);
		}
		return retVal;
	}

	@Override
	public int count(final MetadataElementPageTemplateSearchBean searchBean) {
		int count = 0;
		if(searchBean.hasMultipleKeys()) {
			count = pageTemplateDAO.findByIds(searchBean.getKeys()).size();
		} else {
			final MetadataElementPageTemplateEntity entity = templateSearchBeanConverter.convert(searchBean);
			count = pageTemplateDAO.count(entity);
		}
		return count;
	}

	@Override
	@Transactional
	public void save(final MetadataElementPageTemplateEntity entity) {
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getId())) {
				final MetadataElementPageTemplateEntity dbEntity = pageTemplateDAO.findById(entity.getId());
				if(dbEntity != null) {
					entity.setResource(dbEntity.getResource());
				}
			} else {
				final ResourceEntity resource = new ResourceEntity();
				resource.setName(String.format("%s_%s", entity.getName(), "" + System.currentTimeMillis()));
	            resource.setResourceType(resourceTypeDAO.findById(uiTemplateResourceType));
	            resourceDAO.save(resource);
	            entity.setResource(resource);
			}
			
			URIPatternEntity uriPattern = null;
			if(entity.getUriPattern() != null && StringUtils.isNotBlank(entity.getUriPattern().getId())) {
				uriPattern = uriPatternDAO.findById(entity.getUriPattern().getId());
			}
			entity.setUriPattern(uriPattern);
			
			final Set<MetadataElementPageTemplateXrefEntity> renewedXrefs = new LinkedHashSet<MetadataElementPageTemplateXrefEntity>();
			if(CollectionUtils.isNotEmpty(entity.getMetadataElements())) {
				for(final MetadataElementPageTemplateXrefEntity xref : entity.getMetadataElements()) {
					if(xref != null) {
						final MetadataElementPageTemplateXrefIdEntity id = xref.getId();
						if(id != null && StringUtils.isNotBlank(id.getMetadataElementId())) {
							final String metaElementId = id.getMetadataElementId();
							final MetadataElementPageTemplateXrefEntity dbXref = xrefDAO.findById(id);
							if(dbXref != null) {
								renewedXrefs.add(dbXref);
							} else {
								xref.setTemplate(entity);
								xref.setMetadataElement(elementDAO.findById(metaElementId));
								renewedXrefs.add(xref);
							}
						}
					}
				}
			}
			entity.setMetadataElements(renewedXrefs);
			if(StringUtils.isBlank(entity.getId())) {
				pageTemplateDAO.save(entity);
			} else {
				pageTemplateDAO.merge(entity);
			}
		}
	}

	@Override
	@Transactional
	public void delete(final String id) {
		final MetadataElementPageTemplateEntity entity = pageTemplateDAO.findById(id);
		if(entity != null) {
			pageTemplateDAO.delete(entity);
		}
	}

}
