package org.openiam.idm.srvc.meta.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.MetaDataElementDozerConverter;
import org.openiam.dozer.converter.MetaDataTypeDozerConverter;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.searchbean.converter.MetadataElementSearchBeanConverter;
import org.openiam.idm.srvc.searchbean.converter.MetadataTypeSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private MetadataElementSearchBeanConverter metadataElementSearchBeanConverter;
    
    @Autowired
    private MetadataTypeSearchBeanConverter metadataTypeSearchBeanConverter;

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
		final MetadataElementEntity entity = metadataElementSearchBeanConverter.convert(searchBean);
		return metadataElementDao.getByExample(entity, from, size);
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
	public void save(MetadataElementEntity entity) {
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getId())) {
				final MetadataElementEntity dbEntity = metadataElementDao.findById(entity.getId());
				if(dbEntity != null) {
					entity.setDefaultValueLanguageMap(dbEntity.getDefaultValueLanguageMap());
					entity.setLanguageMap(dbEntity.getLanguageMap());
					entity.setMetadataType(dbEntity.getMetadataType());
					entity.setTemplateSet(dbEntity.getTemplateSet());
					entity.setValidValues(dbEntity.getValidValues());
					entity.setResource(dbEntity.getResource());
				}
			}
			metadataElementDao.merge(entity);
		}
	}

	@Override
	public void save(MetadataTypeEntity entity) {
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getMetadataTypeId())) {
				final MetadataTypeEntity dbEntity = metadataTypeDao.findById(entity.getMetadataTypeId());
				if(dbEntity != null) {
					entity.setCategories(dbEntity.getCategories());
					entity.setElementAttributes(dbEntity.getElementAttributes());
				}
			}
			metadataTypeDao.merge(entity);
		}
	}
	
	@Override
	public void deleteMetdataElement(String id) {
		final MetadataElementEntity entity = metadataElementDao.findById(id);
		if(entity != null) {
			metadataElementDao.delete(entity);
		}
	}

	@Override
	public void deleteMetdataType(String id) {
		final MetadataTypeEntity entity = metadataTypeDao.findById(id);
		if(entity != null) {
			metadataTypeDao.delete(entity);
		}
	}

	@Override
	public int count(final MetadataElementSearchBean searchBean) {
		final MetadataElementEntity entity = metadataElementSearchBeanConverter.convert(searchBean);
		return metadataElementDao.count(entity);
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
