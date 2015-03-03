package org.openiam.idm.srvc.meta.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.service.AbstractLanguageService;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.service.LanguageMappingDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataValidValueEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.searchbean.converter.MetadataTypeSearchBeanConverter;
import org.openiam.internationalization.LocalizedServiceGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier(value = "metaElementQueue")
    private Queue queue;

    private static final Log log = LogFactory.getLog(MetadataServiceImpl.class);

	@Override
	@LocalizedServiceGet
	@Transactional(readOnly=true)
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
	@Transactional(readOnly=true)
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
    @Transactional(readOnly=true)
    public MetadataTypeEntity findById(String id) {
        return metadataTypeDao.findById(id);
    }

    @Override
	@Transactional
	public void save(MetadataElementEntity entity) {
		if(entity != null) {
			if(StringUtils.isBlank(entity.getId())) {
				final ResourceEntity resource = new ResourceEntity();
				resource.setName(String.format("%s_%s", entity.getAttributeName(), "" + System.currentTimeMillis()));
	            resource.setResourceType(resourceTypeDAO.findById(uiWidgetResourceType));
	            resource.setIsPublic(true); /* make public by default */
	            resource.setCoorelatedName(entity.getAttributeName());
	            resourceDAO.save(resource);
	            entity.setResource(resource);
				entity.setMetadataType(metadataTypeDao.findById(entity.getMetadataType().getId()));
				
				entity.setTemplateSet(null);
				if(CollectionUtils.isNotEmpty(entity.getValidValues())) {
					for(final MetadataValidValueEntity validValue : entity.getValidValues()) {
						validValue.setEntity(entity);
					}
				}
				metadataElementDao.save(entity);
			} else {
				final MetadataElementEntity dbEntity = metadataElementDao.findById(entity.getId());
				//entity.setValidValues(dbEntity.getValidValues());
				entity.setUserAttributes(dbEntity.getUserAttributes());
				
				mergeValidValues(entity, dbEntity);

				/* don't let the caller update these */
				entity.setMetadataType(dbEntity.getMetadataType());
				entity.setTemplateSet(dbEntity.getTemplateSet());
				entity.setResource(dbEntity.getResource());
				if(entity.getResource() != null) {
					entity.getResource().setCoorelatedName(entity.getAttributeName());
				}
				entity.setOrganizationAttributes(dbEntity.getOrganizationAttributes());
                entity.setGroupAttributes(dbEntity.getGroupAttributes());
                entity.setUserAttributes(dbEntity.getUserAttributes());
                entity.setResourceAttributes(dbEntity.getResourceAttributes());
    			if(CollectionUtils.isNotEmpty(entity.getValidValues())) {
    				for(final MetadataValidValueEntity validValue : entity.getValidValues()) {
    					validValue.setEntity(entity);
    					if(StringUtils.isEmpty(validValue.getId())) {
    						validValueDAO.save(validValue);
    					}
    				}
    			}
			}
			metadataElementDao.merge(entity);
            this.send(entity);
		}
	}

    private void send(final MetadataElementEntity entity) {
        jmsTemplate.send(queue, new MessageCreator() {
            public javax.jms.Message createMessage(Session session) throws JMSException {
                javax.jms.Message message = session.createObjectMessage(entity);
                return message;
            }
        });
    }
	
	private void mergeValidValues(final MetadataElementEntity bean, final MetadataElementEntity dbObject) {
		 Set<MetadataValidValueEntity> beanProps = (bean.getValidValues() != null) ? bean.getValidValues() : new HashSet<MetadataValidValueEntity>();
		 Set<MetadataValidValueEntity> dbProps = (dbObject.getValidValues() != null) ? new HashSet<MetadataValidValueEntity>(dbObject.getValidValues()) : new HashSet<MetadataValidValueEntity>();

        /* update */
        Iterator<MetadataValidValueEntity> dbIteroator = dbProps.iterator();
        while(dbIteroator.hasNext()) {
        	final MetadataValidValueEntity dbProp = dbIteroator.next();
        	
        	boolean contains = false;
            for (final MetadataValidValueEntity beanProp : beanProps) {
                if (StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                	dbProp.setDisplayName(beanProp.getDisplayName());
                	dbProp.setDisplayOrder(beanProp.getDisplayOrder());
                	dbProp.setLanguageMap(beanProp.getLanguageMap());
                	dbProp.setUiValue(beanProp.getUiValue());
                    contains = true;
                    break;
                }
            }
            
            /* remove */
            if(!contains) {
            	dbIteroator.remove();
            }
        }

        /* add */
        final Set<MetadataValidValueEntity> toAdd = new HashSet<>();
        for (final MetadataValidValueEntity beanProp : beanProps) {
            boolean contains = false;
            dbIteroator = dbProps.iterator();
            while(dbIteroator.hasNext()) {
            	final MetadataValidValueEntity dbProp = dbIteroator.next();
                if (StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                    contains = true;
                }
            }

            if (!contains) {
            	beanProp.setEntity(bean);
                toAdd.add(beanProp);
            }
        }
        dbProps.addAll(toAdd);
        
        bean.setValidValues(dbProps);
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
			log.debug("METADATA_TYPE SAVE : " + entity.toString());

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
	@Transactional(readOnly=true)
	public int count(final MetadataElementSearchBean searchBean) {
		return metadataElementDao.count(searchBean);
	}

	@Override
	@Transactional(readOnly=true)
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

	@Override
	@Transactional
	public void delteMetaValidValue(String validValueId) {
		final MetadataValidValueEntity entity = validValueDAO.findById(validValueId);
		if(entity != null) {
			validValueDAO.delete(entity);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<MetadataElementEntity> findElementByName(String name) {
		final MetadataElementSearchBean searchBean = new MetadataElementSearchBean();
		searchBean.setAttributeName(name);
		return findBeans(searchBean, 0, Integer.MAX_VALUE, null);
	}
}
