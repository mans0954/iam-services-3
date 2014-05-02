package org.openiam.idm.srvc.meta.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.openiam.base.service.AbstractLanguageService;
import org.openiam.dozer.converter.MetaDataElementDozerConverter;
import org.openiam.dozer.converter.MetaDataTypeDozerConverter;
import org.openiam.hibernate.HibernateUtils;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
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
import org.openiam.internationalization.LocalizedServiceGet;
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
public class MetadataServiceImpl extends AbstractLanguageService implements MetadataService {

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
	@LocalizedServiceGet
	@Transactional
	public List<MetadataElementEntity> findBeans(final MetadataElementSearchBean searchBean, final int from, final int size, final LanguageEntity language) {
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
			final Set<MetadataValidValueEntity> validValues = entity.getValidValues();
			
			if(StringUtils.isBlank(entity.getId())) {
				final ResourceEntity resource = new ResourceEntity();
				resource.setName(String.format("%s_%s", entity.getAttributeName(), "" + System.currentTimeMillis()));
	            resource.setResourceType(resourceTypeDAO.findById(uiWidgetResourceType));
	            resource.setIsPublic(true); /* make public by default */
	            resourceDAO.save(resource);
	            entity.setResource(resource);
				entity.setMetadataType(metadataTypeDao.findById(entity.getMetadataType().getId()));
				
				entity.setValidValues(null);
				entity.setTemplateSet(null);
				metadataElementDao.save(entity);
				entity.setValidValues(mergeValidValues(entity.getValidValues(), validValues));
			} else {
				final MetadataElementEntity dbEntity = metadataElementDao.findById(entity.getId());
				entity.setValidValues(dbEntity.getValidValues());
				entity.setUserAttributes(dbEntity.getUserAttributes());
				
				entity.setValidValues(mergeValidValues(dbEntity.getValidValues(), validValues));

				/* don't let the caller update these */
				entity.setMetadataType(dbEntity.getMetadataType());
				entity.setTemplateSet(dbEntity.getTemplateSet());
				entity.setResource(dbEntity.getResource());
				entity.setOrganizationAttributes(dbEntity.getOrganizationAttributes());
                entity.setGroupAttributes(dbEntity.getGroupAttributes());
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
						persistentEntity.setLanguageMap(transientEntity.getLanguageMap());
					}
				}
			}
		}
		
		return retval;
	}
	
	@Override
	@Transactional
	public void save(MetadataTypeEntity entity) {
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getId())) {
				final MetadataTypeEntity dbEntity = metadataTypeDao.findById(entity.getId());
				if(dbEntity != null) {
					entity.setCategories(dbEntity.getCategories());
					entity.setElementAttributes(dbEntity.getElementAttributes());
				}
			}
			if(StringUtils.isBlank(entity.getId())) {
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
		if(StringUtils.isEmpty(entity.getId())) {
			//entity.setLanguageMap(null);
			//entity.setEntity(metadataElementDao.findById(entity.getEntity().getId()));
			validValueDAO.save(entity);
		} else {
			//final MetadataValidValueEntity dbEntity = validValueDAO.findById(entity.getId());
			//entity.setEntity(dbEntity.getEntity());
			//entity.setLanguageMap(mergeLanguageMaps(dbEntity.getLanguageMap(), languageMap));
			validValueDAO.merge(entity);
		}
		//validValueDAO.merge(entity);
	}
	
	@Override
	@Transactional
	public void delteMetaValidValue(String validValueId) {
		final MetadataValidValueEntity entity = validValueDAO.findById(validValueId);
		if(entity != null) {
			validValueDAO.delete(entity);
		}
	}

	@Override
	public List<MetadataElementEntity> findElementByName(String name) {
		final MetadataElementSearchBean searchBean = new MetadataElementSearchBean();
		searchBean.setAttributeName(name);
		return findBeans(searchBean, 0, Integer.MAX_VALUE, null);
	}
}
