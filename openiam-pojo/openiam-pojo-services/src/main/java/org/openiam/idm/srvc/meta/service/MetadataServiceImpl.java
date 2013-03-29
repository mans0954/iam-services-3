package org.openiam.idm.srvc.meta.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.MetaDataElementDozerConverter;
import org.openiam.dozer.converter.MetaDataTypeDozerConverter;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.searchbean.converter.MetadataTypeSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data service implementation for Metadata.
 * @author suneet
 * @version 1
 */
@Service("metadataService")
public class MetadataServiceImpl implements MetadataService {

    @Autowired
    private MetadataTypeDAO metadataTypeDao;
    
    @Autowired
    private MetadataElementDAO metadataElementDao;
    
    @Autowired
    private MetadataTypeSearchBeanConverter metadataTypeSearchBeanConverter;
    
    @Autowired
    private ResourceTypeDAO resourceTypeDAO;
    
    @Autowired
    private ResourceDAO resourceDAO;
    
    @Value("${org.openiam.resource.type.ui.widget}")
    private String uiWidgetResourceType;

    private static final Log log = LogFactory.getLog(MetadataServiceImpl.class);

    @Override
    public List<MetadataElementEntity> getMetadataElementByType(String typeId) {
        final MetadataTypeEntity result = metadataTypeDao.findById(typeId);
        final Map<String, MetadataElementEntity> elementMap = result.getElementAttributes();
        return (elementMap != null) ? new ArrayList<MetadataElementEntity>(elementMap.values()) : null;
    }

    @Override
    public List<MetadataTypeEntity> getTypesInCategory(String categoryId) {
        return metadataTypeDao.findTypesInCategory(categoryId);
    }

    @Override
    public List<MetadataElementEntity> getAllElementsForCategoryType(final String categoryType) {
        return metadataElementDao.findbyCategoryType(categoryType);
    }

	@Override
	public List<MetadataElementEntity> findBeans(final MetadataElementSearchBean searchBean, final int from, final int size) {
		List<MetadataElementEntity> retVal = null;
		if(searchBean.hasMultipleKeys()) {
			retVal = metadataElementDao.findByIds(searchBean.getKeys());
		} else {
			retVal = metadataElementDao.getByExample(searchBean, from, size);
		}
		return retVal;
	}
	
	@Override
	public List<MetadataTypeEntity> findBeans(final MetadataTypeSearchBean searchBean, final int from, final int size) {
		List<MetadataTypeEntity> retVal = null;
		if(searchBean.hasMultipleKeys()) {
			retVal = metadataTypeDao.findByIds(searchBean.getKeys());
		} else {
			final MetadataTypeEntity entity = metadataTypeSearchBeanConverter.convert(searchBean);
			retVal = metadataTypeDao.getByExample(entity, from, size);
		}
		return retVal;
	}

	@Override
	@Transactional
	public void save(MetadataElementEntity entity) {
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getId())) {
				final MetadataElementEntity dbEntity = metadataElementDao.findById(entity.getId());
				if(dbEntity != null) {
					//entity.setDefaultValueLanguageMap(dbEntity.getDefaultValueLanguageMap());
					//entity.setLanguageMap(dbEntity.getLanguageMap());
					entity.setMetadataType(dbEntity.getMetadataType());
					entity.setTemplateSet(dbEntity.getTemplateSet());
					//entity.setValidValues(dbEntity.getValidValues());
					entity.setResource(dbEntity.getResource());
				}
			} else {
				final ResourceEntity resource = new ResourceEntity();
				resource.setName(String.format("%s_%s", entity.getAttributeName(), "" + System.currentTimeMillis()));
	            resource.setResourceType(resourceTypeDAO.findById(uiWidgetResourceType));
	            resourceDAO.save(resource);
	            entity.setResource(resource);
				entity.setMetadataType(metadataTypeDao.findById(entity.getMetadataType().getMetadataTypeId()));
			}
			
			if(StringUtils.isBlank(entity.getId())) {
				metadataElementDao.save(entity);
			} else {
				metadataElementDao.merge(entity);
			}
		}
	}

	@Override
	@Transactional
	public void save(MetadataTypeEntity entity) {
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getMetadataTypeId())) {
				final MetadataTypeEntity dbEntity = metadataTypeDao.findById(entity.getMetadataTypeId());
				if(dbEntity != null) {
					entity.setCategories(dbEntity.getCategories());
					entity.setElementAttributes(dbEntity.getElementAttributes());
				}
			}
			if(StringUtils.isBlank(entity.getMetadataTypeId())) {
				metadataTypeDao.save(entity);
			} else {
				metadataTypeDao.merge(entity);
			}
		}
	}
	
	@Override
	@Transactional
	public void deleteMetdataElement(String id) {
		final MetadataElementEntity entity = metadataElementDao.findById(id);
		if(entity != null) {
			metadataElementDao.delete(entity);
		}
	}

	@Override
	@Transactional
	public void deleteMetdataType(String id) {
		final MetadataTypeEntity entity = metadataTypeDao.findById(id);
		if(entity != null) {
			metadataTypeDao.delete(entity);
		}
	}

	@Override
	public int count(final MetadataElementSearchBean searchBean) {
		return metadataElementDao.count(searchBean);
	}

	@Override
	public int count(final MetadataTypeSearchBean searchBean) {
		int retVal = 0;
		if(searchBean.hasMultipleKeys()) {
			final List<MetadataTypeEntity> entityList = metadataTypeDao.findByIds(searchBean.getKeys());
			retVal = (entityList != null) ? entityList.size() : 0;
		} else {
			final MetadataTypeEntity entity = metadataTypeSearchBeanConverter.convert(searchBean);
			retVal = metadataTypeDao.count(entity);
		}
		return retVal;
	}
}
