package org.openiam.idm.srvc.meta.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.UpdateAttributeByMetadataRequest;
import org.openiam.base.service.AbstractLanguageService;
import org.openiam.base.ws.ResponseCode;
import org.openiam.cache.CacheKeyEvict;
import org.openiam.cache.CacheKeyEviction;
import org.openiam.dozer.converter.MetaDataElementDozerConverter;
import org.openiam.dozer.converter.MetaDataTypeDozerConverter;
import org.openiam.elasticsearch.dao.MetadataTypeElasticSearchRepository;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.service.LanguageMappingDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.domain.MetadataValidValueEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.mq.constants.*;
import org.openiam.mq.constants.queue.MqQueue;
import org.openiam.mq.constants.queue.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.gateway.RequestServiceGateway;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
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
    private MetadataTypeElasticSearchRepository metadataTypeRepo;
    
    @Autowired
    private MetadataElementDAO metadataElementDao;
    
    @Autowired
    private ResourceTypeDAO resourceTypeDAO;
    
    @Autowired
    private ResourceDAO resourceDAO;
    
    @Autowired
    private MetadataValidValueDAO validValueDAO;
    
    @Autowired
    private LanguageMappingDAO languageMappingDAO;

    @Autowired
    private MetaDataTypeDozerConverter metaDataTypeDozerConverter;

    @Autowired
    private MetaDataElementDozerConverter metaDataElementDozerConverter;

    @Value("${org.openiam.resource.type.ui.widget}")
    private String uiWidgetResourceType;

//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private RequestServiceGateway requestServiceGateway;

/*    @Autowired
    @Qualifier(value = "metaElementQueue")
    private Queue queue;*/

    private static final Log log = LogFactory.getLog(MetadataServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public String findElementIdByAttrNameAndTypeId(String attrName, String typeId) {
        return metadataElementDao.findIdByAttrNameTypeId(attrName, typeId);
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public MetadataType findMetadataTypeByNameAndGrouping(String name, MetadataTypeGrouping grouping, final Language language) {
        MetadataTypeEntity metadataTypeEntity = metadataTypeDao.findByNameGrouping(name, grouping);
        return metaDataTypeDozerConverter.convertToDTO(metadataTypeEntity, true);
    }

    @Override
    @Transactional(readOnly=true)
    @LocalizedServiceGet
    public MetadataElement findElementById(String id, Language language) {
        MetadataElementEntity metadataElementEntity = metadataElementDao.findById(id);
        return metadataElementEntity != null ? metaDataElementDozerConverter.convertToDTO(metadataElementEntity,true) : null;
    }

    @Override
    @Transactional(readOnly=true)
    @LocalizedServiceGet
    public MetadataElement findElementByAttrNameAndTypeId(String attrName, String typeId, final Language language) {
        MetadataElementEntity metadataElementEntity = metadataElementDao.findByAttrNameTypeId(attrName, typeId);
        return metadataElementEntity != null ? metaDataElementDozerConverter.convertToDTO(metadataElementEntity, true) : null;
    }

    @Override
	@LocalizedServiceGet
	@Transactional(readOnly=true)
    @Cacheable(value="metadataElements",  key="{ #searchBean, #from, #size, #language}", condition="{#searchBean != null and #searchBean.findInCache}")
	public List<MetadataElement> findBeans(final MetadataElementSearchBean searchBean, final int from, final int size, final Language language) {
		List<MetadataElementEntity> retVal = getProxyService().findEntityBeans(searchBean, from,size);
        return (retVal != null) ? metaDataElementDozerConverter.convertToDTOList(retVal,true) : null;
	}

    @Override
    @Transactional(readOnly=true)
    /**
     * Without localization loop
     */
    @Cacheable(value="metadataElements",  key="{ #searchBean, #from, #size }", condition="{#searchBean != null and #searchBean.findInCache}")
    public List<MetadataElement> findBeans(MetadataElementSearchBean searchBean, int from, int size) {
       return this.findBeans(searchBean, from, size, null);
    }
    
    @Override
    @Transactional(readOnly=true)
    @LocalizedServiceGet
    /*AM-851 */
	//@Cacheable(value="metadataElementEntities",  key="{ #searchBean, #from, #size }", condition="{#searchBean != null and #searchBean.findInCache}")
	public List<MetadataElementEntity> findEntityBeans(final MetadataElementSearchBean searchBean, final int from, final int size){
		List<MetadataElementEntity> retVal = null;
		if(searchBean != null  && searchBean.hasMultipleKeys()) {
			retVal = metadataElementDao.findByIds(searchBean.getKeys());
		} else {
			retVal = metadataElementDao.getByExample(searchBean, from, size);
		}

        return retVal;
	}

    @Override
    @LocalizedServiceGet
	@Transactional(readOnly=true)
    @Cacheable(value="metadataTypes", key="{ #searchBean, #from, #size, #language}", condition="{#searchBean != null and #searchBean.findInCache}")
	public List<MetadataType> findBeans(final MetadataTypeSearchBean searchBean, final int from, final int size, final Language language) {
		List<MetadataTypeEntity> retVal = null;
		if(searchBean != null) {
			if(CollectionUtils.isNotEmpty(searchBean.getKeys())) {
				retVal = metadataTypeDao.findByIds(searchBean.getKeys());
			} else {
				if(searchBean.isUseElasticSearch()) {
					if(metadataTypeRepo.isValidSearchBean(searchBean)) {
						retVal = metadataTypeRepo.findBeans(searchBean, from, size);
					} else {
						retVal = metadataTypeRepo.findAll(metadataTypeRepo.getPageable(searchBean, from, size)).getContent();
					}
				} else {
					retVal = metadataTypeDao.getByExample(searchBean, from, size);
				}
			}
		} else {
			retVal = metadataTypeRepo.findAll(metadataTypeRepo.getPageable(searchBean, from, size)).getContent();
		}
        return (retVal != null) ? metaDataTypeDozerConverter.convertToDTOList(retVal,true) : null;
	}

	@Override
	@LocalizedServiceGet
	@Transactional(readOnly=true)
	/* AM-851 */
	//@Cacheable(value="metadataTypeEntities", key="{ #searchBean, #from, #size,#lang}", condition="{#searchBean != null and #searchBean.findInCache}")
	public List<MetadataTypeEntity> findEntityBeans(final MetadataTypeSearchBean searchBean, final int from, final int size, final Language language){
		List<MetadataTypeEntity> retVal = null;
		if(searchBean.hasMultipleKeys()) {
			retVal = metadataTypeDao.findByIds(searchBean.getKeys());
		} else {
			retVal = metadataTypeDao.getByExample(searchBean, from, size);
		}
		return retVal;
	}

    @Override
    @Cacheable(value="metadataTypes", key="{ #searchBean, #from, #size }", condition="{#searchBean != null and #searchBean.findInCache}")
    public List<MetadataType> findBeansNoLocalize(MetadataTypeSearchBean searchBean, int from, int size) {
        return this.findBeans(searchBean, from, size, null);
    }

    @Override
    @Transactional(readOnly=true)
    public MetadataType findById(String id) {
        MetadataTypeEntity metadataTypeEntity = metadataTypeDao.findById(id);
        return metadataTypeEntity != null ? metaDataTypeDozerConverter.convertToDTO(metadataTypeEntity, true) : null;
    }

    @Override
	@Transactional
    @CacheKeyEviction(
    	evictions={
            @CacheKeyEvict("metadataElements"),
            @CacheKeyEvict("metadataElementEntities")
        }
    )
	public void save(final MetadataElement element) {
		if(element != null) {
            MetadataElementEntity entity = metaDataElementDozerConverter.convertToEntity(element,true);
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
//				entity.setUserAttributes(dbEntity.getUserAttributes());
				
				mergeValidValues(entity, dbEntity);

				/* don't let the caller update these */
				entity.setMetadataType(dbEntity.getMetadataType());
				entity.setTemplateSet(dbEntity.getTemplateSet());
				entity.setResource(dbEntity.getResource());
				if(entity.getResource() != null) {
					entity.getResource().setCoorelatedName(entity.getAttributeName());
				}
//				  entity.setOrganizationAttributes(dbEntity.getOrganizationAttributes());
//                entity.setGroupAttributes(dbEntity.getGroupAttributes());
//                entity.setUserAttributes(dbEntity.getUserAttributes());
//                entity.setResourceAttributes(dbEntity.getResourceAttributes());
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
			element.setId(entity.getId());
		}
	}

    private void send(final MetadataElementEntity entity) {
		if(entity.getMetadataType()!=null && entity.getMetadataType().getGrouping()!=null) {
			UpdateAttributeByMetadataRequest request = new UpdateAttributeByMetadataRequest();
			request.setMetadataElementId(entity.getId());
			request.setDefaultValue(entity.getStaticDefaultValue());
			request.setName(entity.getAttributeName());
			request.setRequired(entity.isRequired());
			request.setMetadataTypeId(entity.getMetadataType().getId());
			request.setMetadataTypeGrouping(entity.getMetadataType().getGrouping());

			MQRequest<UpdateAttributeByMetadataRequest, OpenIAMAPICommon> mqRequest = new MQRequest<>();
			mqRequest.setRequestBody(request);
			mqRequest.setRequestApi(OpenIAMAPICommon.UpdateAttributesByMetadata);

			MqQueue queue = null;
			switch (entity.getMetadataType().getGrouping()) {
				case USER_OBJECT_TYPE:
					queue = OpenIAMQueue.UserAttributeQueue;
					break;
				case ROLE_TYPE:
					queue = OpenIAMQueue.RoleAttributeQueue;
					break;
				case GROUP_TYPE:
					queue = OpenIAMQueue.GroupAttributeQueue;
					break;
				case ORG_TYPE:
					queue = OpenIAMQueue.OrganizationAttributeQueue;
					break;
				case RESOURCE_TYPE:
					queue = OpenIAMQueue.ResourceAttributeQueue;
					break;
				default:
					return;
			}
			try {
				requestServiceGateway.send(queue, OpenIAMAPICommon.UpdateAttributesByMetadata, request);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
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
	@CacheKeyEviction(
    	evictions={
            @CacheKeyEvict("metadataTypes"),
            @CacheKeyEvict("metadataTypeEntities")
        }
    )
	public void save(final MetadataType dto) throws BasicDataServiceException {
		if(dto!=null) {
			final MetadataTypeEntity entity = metaDataTypeDozerConverter.convertToEntity(dto, true);

			if (StringUtils.isNotBlank(entity.getId())) {
				final MetadataTypeEntity dbEntity = metadataTypeDao.findById(entity.getId());
				if (dbEntity != null) {
					entity.setCategories(dbEntity.getCategories());
					entity.setElementAttributes(dbEntity.getElementAttributes());
				}
			}
			if (StringUtils.isBlank(entity.getId())) {
				metadataTypeDao.save(entity);
			} else {
				if (entity.isUsedForSMSOTP()) {
					final List<MetadataType> phoneTypesWithOTP = getPhonesWithSMSOTPEnabled();
					if (CollectionUtils.isNotEmpty(phoneTypesWithOTP)) {
						for (final MetadataType phoneTypeWithOTP : phoneTypesWithOTP) {
							if (!StringUtils.equals(phoneTypeWithOTP.getId(), entity.getId())) {
								throw new BasicDataServiceException(ResponseCode.PHONE_MARKED_FOR_SMS_OTP, phoneTypeWithOTP.getName());
							}
						}
					}
				}

				metadataTypeDao.merge(entity);
			}
			dto.setId(entity.getId());
		}
	}
	
	@Override
	@Transactional
	@CacheKeyEviction(
    	evictions={
            @CacheKeyEvict("metadataElements"),
            @CacheKeyEvict("metadataElementEntities")
        }
    )
	public void deleteMetdataElement(final String id) {
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
	@CacheKeyEviction(
    	evictions={
            @CacheKeyEvict("metadataTypes"),
            @CacheKeyEvict("metadataTypeEntities")
        }
    )
	public void deleteMetdataType(final String id) {
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
			retVal = metadataTypeDao.count(searchBean);
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
	public List<MetadataElement> findElementByName(String name) {
		final MetadataElementSearchBean searchBean = new MetadataElementSearchBean();
		searchBean.setAttributeName(name);
        return findBeans(searchBean, 0, Integer.MAX_VALUE, null);
	}

	@Override
	@Transactional(readOnly=true)
	public List<MetadataType> getPhonesWithSMSOTPEnabled() {
		final MetadataTypeSearchBean searchBean = new MetadataTypeSearchBean();
		searchBean.setUsedForSMSOTP(true);
		searchBean.setGrouping(MetadataTypeGrouping.PHONE);
		final List<MetadataTypeEntity> entitiesMarkedForSMSOTP = metadataTypeDao.getByExample(searchBean);
		return  (entitiesMarkedForSMSOTP != null) ? metaDataTypeDozerConverter.convertToDTOList(entitiesMarkedForSMSOTP,true) : null;
	}
	
	private MetadataService getProxyService() {
		MetadataService service = (MetadataService) SpringContextProvider.getBean("metadataService");
        return service;
    }
}
