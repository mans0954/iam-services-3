package org.openiam.idm.srvc.meta.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.MetaDataElementDozerConverter;
import org.openiam.dozer.converter.MetaDataTypeDozerConverter;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.service.LanguageMappingDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataValidValueEntity;
import org.openiam.idm.srvc.meta.domain.WhereClauseConstants;
import org.openiam.idm.srvc.meta.domain.pk.MetadataElementPageTemplateXrefIdEntity;
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
    
    @Autowired
    private MetadataValidValueDAO validValueDAO;
    
    @Autowired
    private LanguageMappingDAO languageMappingDAO;
    
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
			final Map<String, LanguageMappingEntity> languageMap = entity.getLanguageMap();
			final Map<String, LanguageMappingEntity> defaultValueLanguageMap = entity.getDefaultValueLanguageMap();
			final Set<MetadataValidValueEntity> validValues = entity.getValidValues();
			
			if(StringUtils.isBlank(entity.getId())) {
				final ResourceEntity resource = new ResourceEntity();
				resource.setName(String.format("%s_%s", entity.getAttributeName(), "" + System.currentTimeMillis()));
	            resource.setResourceType(resourceTypeDAO.findById(uiWidgetResourceType));
	            resource.setIsPublic(true); /* make public by default */
	            resourceDAO.save(resource);
	            entity.setResource(resource);
				entity.setMetadataType(metadataTypeDao.findById(entity.getMetadataType().getMetadataTypeId()));
				
				entity.setLanguageMap(null);
				entity.setDefaultValueLanguageMap(null);
				entity.setValidValues(null);
				entity.setTemplateSet(null);
				
				metadataElementDao.save(entity);
				entity.setLanguageMap(mergeLanguageMaps(entity.getLanguageMap(), languageMap));
				entity.setDefaultValueLanguageMap(mergeLanguageMaps(entity.getDefaultValueLanguageMap(), defaultValueLanguageMap));
				entity.setValidValues(mergeValidValues(entity.getValidValues(), validValues));
				doCollectionsArithmetic(entity);
			} else {
				final MetadataElementEntity dbEntity = metadataElementDao.findById(entity.getId());
				entity.setValidValues(dbEntity.getValidValues());
				entity.setLanguageMap(dbEntity.getLanguageMap());
				entity.setDefaultValueLanguageMap(dbEntity.getDefaultValueLanguageMap());
				entity.setUserAttributes(dbEntity.getUserAttributes());
				
				entity.setLanguageMap(mergeLanguageMaps(entity.getLanguageMap(), languageMap));
				entity.setDefaultValueLanguageMap(mergeLanguageMaps(entity.getDefaultValueLanguageMap(), defaultValueLanguageMap));
				entity.setValidValues(mergeValidValues(dbEntity.getValidValues(), validValues));
				doCollectionsArithmetic(entity);
			
				/* don't let the caller update these */
				entity.setMetadataType(dbEntity.getMetadataType());
				entity.setTemplateSet(dbEntity.getTemplateSet());
				entity.setResource(dbEntity.getResource());
			}
			
			if(CollectionUtils.isNotEmpty(entity.getValidValues())) {
				for(final MetadataValidValueEntity validValue : entity.getValidValues()) {
					validValue.setEntity(entity);
					save(validValue);
				}
			}
			
			metadataElementDao.merge(entity);
		}
	}
	
	/* assumes same referenceId and referenceType */
	private Map<String, LanguageMappingEntity> mergeLanguageMaps(final Map<String, LanguageMappingEntity> persistentMap, Map<String, LanguageMappingEntity> transientMap) {
		//final Map<String, Set<String>> deleteMap = new HashMap<String, Set<String>>();
		
		transientMap = (transientMap != null) ? transientMap : new HashMap<String, LanguageMappingEntity>();
		final Map<String, LanguageMappingEntity> retVal = (persistentMap != null) ? persistentMap : new HashMap<String, LanguageMappingEntity>();
		
		/* remove empty strings */
		for(final Iterator<Entry<String, LanguageMappingEntity>> it = transientMap.entrySet().iterator(); it.hasNext();) {
			final Entry<String, LanguageMappingEntity> entry = it.next();
			final LanguageMappingEntity entity = entry.getValue();
			if(StringUtils.isBlank(entity.getValue())) {
				it.remove();
			}
		}
		
		/* update existing entries */
		for(final LanguageMappingEntity transientEntry : transientMap.values()) {
			for(final LanguageMappingEntity persistentEntry : retVal.values()) {
				if(StringUtils.equals(transientEntry.getLanguageId(), persistentEntry.getLanguageId())) {
					persistentEntry.setValue(transientEntry.getValue());
				}
			}
		}
		
		/* remove old entries */
		for(final Iterator<Entry<String, LanguageMappingEntity>> it = retVal.entrySet().iterator(); it.hasNext();) {
			final Entry<String, LanguageMappingEntity> entry = it.next();
			final LanguageMappingEntity persistentEntry = entry.getValue();
			boolean contains = false;
			for(final LanguageMappingEntity transientEntry : transientMap.values()) {
				if(StringUtils.equals(transientEntry.getLanguageId(), persistentEntry.getLanguageId())) {
					contains = true;
				}
			}
			
			if(!contains) {
				it.remove();
			}
		}
		
		/* add new entries */
		for(final LanguageMappingEntity transientEntry : transientMap.values()) {
			boolean found = false;
			for(final LanguageMappingEntity persistentEntry : retVal.values()) {
				if(StringUtils.isNotEmpty(transientEntry.getValue())) {
					if(StringUtils.equals(transientEntry.getLanguageId(), persistentEntry.getLanguageId())) {
						found = true;
						break;
					}
				}
			}
			if(!found) {
				retVal.put(transientEntry.getLanguageId(), transientEntry);
			}
		}
		
		/*
		for(final String referenceType : deleteMap.keySet()) {
			languageMappingDAO.deleteByReferenceTypeAndIds(deleteMap.get(referenceType), referenceType);
		}
		*/
		
		return retVal;
	}
	
	private Set<MetadataValidValueEntity> mergeValidValues(final Set<MetadataValidValueEntity> persistentSet, Set<MetadataValidValueEntity> transientSet) {
		final Set<MetadataValidValueEntity> retval = (persistentSet != null) ? persistentSet : new HashSet<MetadataValidValueEntity>();
		transientSet = (transientSet != null) ? transientSet : new HashSet<MetadataValidValueEntity>();
		
		/* add new entities */
		for(final MetadataValidValueEntity transientEntity : transientSet) {
			boolean exists = false;
			if(StringUtils.isNotBlank(transientEntity.getId())) {
				for(final MetadataValidValueEntity persistentEntity : retval) {
					if(StringUtils.equals(persistentEntity.getId(), transientEntity.getId())) {
						//transientEntity.setUiValue(persistentEntity.getUiValue());
						exists = true;
					}
				}
			}
			
			if(!exists) {
				retval.add(transientEntity);
			}
		}
		
		/* purge old entities */
		for(final Iterator<MetadataValidValueEntity> it = retval.iterator(); it.hasNext();) {
			boolean exists = false;
			final MetadataValidValueEntity persistentEntity = it.next();
			if(StringUtils.isNotBlank(persistentEntity.getId())) {
				for(final MetadataValidValueEntity transientEntity : transientSet) {
					if(StringUtils.equals(persistentEntity.getId(), transientEntity.getId())) {
						//transientEntity.setUiValue(persistentEntity.getUiValue());
						exists = true;
					}
				}
			
				if(!exists) {
					it.remove();
				}
			}
		}
		
		/* now that you have the valid values to persist, update the underlying collections */
		for(final Iterator<MetadataValidValueEntity> it = retval.iterator(); it.hasNext();) {
			final MetadataValidValueEntity persistentEntity = it.next();
			for(final MetadataValidValueEntity transientEntity : transientSet) {
				if(persistentEntity.getId() != null && transientEntity.getId() != null) {
					if(StringUtils.equals(persistentEntity.getId(), transientEntity.getId())) {
						persistentEntity.setUiValue(transientEntity.getUiValue());
						persistentEntity.setDisplayOrder(transientEntity.getDisplayOrder());
						mergeLanguageMaps(persistentEntity.getLanguageMap(), transientEntity.getLanguageMap());
					}
				}
			}
		}
		
		return retval;
	}
	
	private void doCollectionsArithmetic(final MetadataElementEntity entity) {
		if(MapUtils.isNotEmpty(entity.getLanguageMap())) {
			for(final LanguageMappingEntity mapValue : entity.getLanguageMap().values()) {
				setReferenceType(mapValue, WhereClauseConstants.META_ELEMENT_REFERENCE_TYPE, entity.getId());
			}
		}
		if(MapUtils.isNotEmpty(entity.getDefaultValueLanguageMap())) {
			for(final LanguageMappingEntity mapValue : entity.getDefaultValueLanguageMap().values()) {
				setReferenceType(mapValue, WhereClauseConstants.META_ELEMENT_DEFAULT_VALUE_REFERENCE_TYPE, entity.getId());
			}
		}
		if(CollectionUtils.isNotEmpty(entity.getValidValues())) {
			for(final MetadataValidValueEntity validValue : entity.getValidValues()) {
				if(MapUtils.isNotEmpty(validValue.getLanguageMap())) {
					for(final LanguageMappingEntity mapValue : validValue.getLanguageMap().values()) {
						setReferenceType(mapValue, WhereClauseConstants.VALID_VALUES_REFERENCE_TYPE, validValue.getId());
					}
				}
			}
		}
	}
	
	private void setReferenceType(final LanguageMappingEntity entity, final String referenceType, final String referenceId) {
		if(entity != null) {
			entity.setReferenceType(referenceType);
			entity.setReferenceId(referenceId);
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
			/*
			final Map<String, Set<String>> languageDeleteMap = new HashMap<String, Set<String>>();
			if(CollectionUtils.isNotEmpty(entity.getValidValues())) {
				for(final MetadataValidValueEntity validValue : entity.getValidValues()) {
					populateLanguageDeleteMap(validValue.getLanguageMap(), languageDeleteMap);
				}
			}
			populateLanguageDeleteMap(entity.getDefaultValueLanguageMap(), languageDeleteMap);
			populateLanguageDeleteMap(entity.getLanguageMap(), languageDeleteMap);
			
			for(final String referenceType : languageDeleteMap.keySet()) {
				languageMappingDAO.deleteByReferenceTypeAndIds(languageDeleteMap.get(referenceType), referenceType);
			}
			*/
			metadataElementDao.delete(entity);
		}
	}
	
	private void populateLanguageDeleteMap(final Map<String, LanguageMappingEntity> languageMap, final Map<String, Set<String>> languageDeleteMap) {
		if(MapUtils.isNotEmpty(languageMap)) {
			for(final LanguageMappingEntity languageEntity : languageMap.values()) {
				if(!languageDeleteMap.containsKey(languageEntity.getReferenceType())) {
					languageDeleteMap.put(languageEntity.getReferenceType(), new HashSet<String>());
				}
				languageDeleteMap.get(languageEntity.getReferenceType()).add(languageEntity.getReferenceId());
			}
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

	//@Override
	//@Transactional
	private void save(MetadataValidValueEntity entity) {
		final Map<String, LanguageMappingEntity> languageMap = entity.getLanguageMap();
		if(StringUtils.isEmpty(entity.getId())) {
			entity.setLanguageMap(null);
			//entity.setEntity(metadataElementDao.findById(entity.getEntity().getId()));
			validValueDAO.save(entity);
			entity.setLanguageMap(mergeLanguageMaps(entity.getLanguageMap(), languageMap));
		} else {
			//final MetadataValidValueEntity dbEntity = validValueDAO.findById(entity.getId());
			//entity.setEntity(dbEntity.getEntity());
			//entity.setLanguageMap(mergeLanguageMaps(dbEntity.getLanguageMap(), languageMap));
		}
		setLanguageMetadata(entity);
		validValueDAO.merge(entity);
	}
	
	private void setLanguageMetadata(final MetadataValidValueEntity entity) {
		if(entity != null) {
			if(MapUtils.isNotEmpty(entity.getLanguageMap())) {
				for(final LanguageMappingEntity languageEntity : entity.getLanguageMap().values()) {
					setReferenceType(languageEntity, WhereClauseConstants.VALID_VALUES_REFERENCE_TYPE, entity.getId());
				}
			}
		}
	}

	@Override
	@Transactional
	public void delteMetaValidValue(String validValueId) {
		final MetadataValidValueEntity entity = validValueDAO.findById(validValueId);
		if(entity != null) {
			final Map<String, Set<String>> languageDeleteMap = new HashMap<String, Set<String>>();
			populateLanguageDeleteMap(entity.getLanguageMap(), languageDeleteMap);
			for(final String referenceType : languageDeleteMap.keySet()) {
				languageMappingDAO.deleteByReferenceTypeAndIds(languageDeleteMap.get(referenceType), referenceType);
			}
			validValueDAO.delete(entity);
		}
	}

	@Override
	public List<MetadataTypeEntity> getAllMetadataTypes() {
		return metadataTypeDao.findAll();
	}
}
