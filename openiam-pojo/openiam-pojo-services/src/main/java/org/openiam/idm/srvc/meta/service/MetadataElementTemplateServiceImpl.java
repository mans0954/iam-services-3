package org.openiam.idm.srvc.meta.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dao.URIPatternDao;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.BaseTemplateRequestModel;
import org.openiam.base.domain.AbstractAttributeEntity;
import org.openiam.base.service.AbstractLanguageService;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.MetadataElementTemplateDozerConverter;
import org.openiam.dozer.converter.MetadataTemplateTypeDozerConverter;
import org.openiam.dozer.converter.MetadataTemplateTypeFieldDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeFieldSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.service.LanguageDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.MetadataFieldTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;
import org.openiam.idm.srvc.meta.domain.MetadataValidValueEntity;
import org.openiam.idm.srvc.meta.domain.pk.MetadataElementPageTemplateXrefIdEntity;
import org.openiam.idm.srvc.meta.dto.*;
import org.openiam.exception.PageTemplateException;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.idm.srvc.user.service.UserAttributeDAO;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("metadataElementTemplateService")
public class MetadataElementTemplateServiceImpl extends AbstractLanguageService  implements MetadataElementTemplateService {
	
	@Autowired
	private MetadataElementPageTemplateDAO pageTemplateDAO;
	
	@Autowired
	private MetadataElementPageTemplateXrefDAO xrefDAO;
	
	@Autowired
	private ResourceDAO resourceDAO;
	
	@Autowired
	private MetadataElementDAO elementDAO;
	
	@Autowired
	private MetadataService elementService;
	
	@Autowired
	private ResourceTypeDAO resourceTypeDAO;
	
	@Autowired
	private URIPatternDao uriPatternDAO;
	
	@Autowired
	private LanguageDAO languageDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private UserAttributeDAO attributeDAO;
	
	@Autowired
	private MetadataTemplateTypeEntityDAO templateTypeDAO;
	
	@Autowired
	private MetadataTemplateTypeFieldEntityDAO uiFieldDAO;
	
	@Autowired
	private MetadataFieldTemplateXrefDAO uiFieldXrefDAO;
	
	@Autowired
	private MetadataService metadataService;

	@Value("${org.openiam.resource.type.ui.template}")
	private String uiTemplateResourceType;
	
	@Autowired
	private AuthorizationManagerService authorizationManagerService;

	@Autowired
	@Qualifier("configurableGroovyScriptEngine")
	protected ScriptIntegration scriptRunner;

	@Autowired
	private TemplateObjectHelper templateObjectHelper;


	@Autowired
	@Qualifier("customJacksonMapper")
	protected ObjectMapper jacksonMapper;

	@Autowired
	private MetadataElementTemplateDozerConverter templateDozerConverter;
	@Autowired
	private MetadataTemplateTypeDozerConverter templateTypeDozerConverter;
	@Autowired
	private MetadataTemplateTypeFieldDozerConverter uiFieldDozerConverter;

	
	private static final Log LOG = LogFactory.getLog(MetadataElementTemplateServiceImpl.class);

	@Override
	@Transactional(readOnly=true)
	public List<MetadataElementPageTemplate> findBeans(final MetadataElementPageTemplateSearchBean searchBean, final int from, final int size) {
		List<MetadataElementPageTemplateEntity> entityList = findEntityBeans(searchBean, from, size);
		return (entityList != null) ? templateDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy()) : null;
	}
	@Transactional(readOnly=true)
	private List<MetadataElementPageTemplateEntity> findEntityBeans(final MetadataElementPageTemplateSearchBean searchBean, final int from, final int size) {
		List<MetadataElementPageTemplateEntity> entityList = null;
		if(CollectionUtils.isNotEmpty(searchBean.getKeySet())) {
			entityList = pageTemplateDAO.findByIds(searchBean.getKeySet());
		} else {
			entityList = pageTemplateDAO.getByExample(searchBean, from, size);
		}
		return entityList;
	}

	@Override
	@Transactional(readOnly=true)
	public int count(final MetadataElementPageTemplateSearchBean searchBean) {
		int count = 0;
		if(CollectionUtils.isNotEmpty(searchBean.getKeySet())) {
			count = pageTemplateDAO.findByIds(searchBean.getKeySet()).size();
		} else {
			count = pageTemplateDAO.count(searchBean);
		}
		return count;
	}

	@Override
	@Transactional
	public String save(final MetadataElementPageTemplate template) throws BasicDataServiceException {
		if(template == null) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
		}
		if(StringUtils.isBlank(template.getMetadataTemplateTypeId())) {
			throw new BasicDataServiceException(ResponseCode.TEMPLATE_TYPE_REQUIRED);
		}
		MetadataElementPageTemplateEntity entity = templateDozerConverter.convertToEntity(template, true);

		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getId())) {
				final MetadataElementPageTemplateEntity dbEntity = pageTemplateDAO.findById(entity.getId());
				if(dbEntity != null) {
					entity.setResource(dbEntity.getResource());
					if(entity.getResource() != null) {
						entity.getResource().setCoorelatedName(entity.getName());
					}
				}
			} else {
				final ResourceEntity resource = new ResourceEntity();
				resource.setName(entity.getName() + "_" + System.currentTimeMillis());
	            resource.setResourceType(resourceTypeDAO.findById(uiTemplateResourceType));
	            resource.setIsPublic(true);
	            resource.setCoorelatedName(entity.getName());
	            resourceDAO.save(resource);
	            entity.setResource(resource);
			}
			
			final MetadataTemplateTypeEntity templateType = templateTypeDAO.findById(entity.getTemplateType().getId());
			entity.setTemplateType(templateType);
			
			final Set<URIPatternEntity> transietSet = entity.getUriPatterns();
			if(CollectionUtils.isNotEmpty(transietSet)) {
				final Set<URIPatternEntity> persistentSet = new HashSet<URIPatternEntity>();
				for(final URIPatternEntity transientEntity : transietSet) {
					if(transientEntity != null && StringUtils.isNotBlank(transientEntity.getId())) {
						final URIPatternEntity pattern = uriPatternDAO.findById(transientEntity.getId());
						if(pattern != null) {
							persistentSet.add(pattern);
						}
					}
				}
				entity.setUriPatterns(persistentSet);
			}
			
			final Set<MetadataElementPageTemplateXrefEntity> renewedXrefs = new LinkedHashSet<MetadataElementPageTemplateXrefEntity>();
			if(CollectionUtils.isNotEmpty(entity.getMetadataElements())) {
				for(final MetadataElementPageTemplateXrefEntity xref : entity.getMetadataElements()) {
					if(xref != null) {
						final MetadataElementPageTemplateXrefIdEntity id = xref.getId();
						if(id != null && StringUtils.isNotBlank(id.getMetadataElementId())) {
							final String metaElementId = id.getMetadataElementId();
							//final MetadataElementPageTemplateXrefEntity dbXref = xrefDAO.findById(id);
							//if(dbXref != null) {
							//	dbXref.setDisplayOrder(xref.getDisplayOrder());
							//	renewedXrefs.add(dbXref);
							//} else {
								xref.setTemplate(entity);
								xref.setMetadataElement(elementDAO.findById(metaElementId));
								xref.setId(new MetadataElementPageTemplateXrefIdEntity(entity.getId(), xref.getMetadataElement().getId()));
								renewedXrefs.add(xref);
							//}
						}
					}
				}
			}
			
			final Set<MetadataFieldTemplateXrefEntity> fieldXrefs = new LinkedHashSet<MetadataFieldTemplateXrefEntity>();
			if(CollectionUtils.isNotEmpty(entity.getFieldXrefs())) {
				for(final MetadataFieldTemplateXrefEntity xref : entity.getFieldXrefs()) {
					if(xref != null) {
						final String fieldId = xref.getField().getId();
						if(StringUtils.isNotBlank(fieldId) && templateType.getField(fieldId) != null) {
							//final MetadataFieldTemplateXrefEntity dbXref = uiFieldXrefDAO.findById(xref.getId());
							boolean isRequired = templateType.getField(fieldId).isRequired() ? true : xref.isRequired();
							//if(dbXref != null) {
							//	dbXref.setRequired(isRequired);
							//	dbXref.setEditable(xref.isEditable());
							//	dbXref.setDisplayOrder(xref.getDisplayOrder());
							//	dbXref.setLanguageMap(xref.getLanguageMap());
							//	fieldXrefs.add(dbXref);
							//} else {
								//xref.setId(null);
								xref.setRequired(isRequired);
								xref.setTemplate(entity);
								xref.setField(uiFieldDAO.findById(fieldId));
								//xref.setLanguageMap(null);
								if (xref.getId() == null) {
                                    uiFieldXrefDAO.save(xref);
                                }
								fieldXrefs.add(xref);
							//}
						}
					}
				}
			}
			if(entity.getFieldXrefs() == null) {
				entity.setFieldXrefs(new HashSet<MetadataFieldTemplateXrefEntity>());
			}
			entity.getFieldXrefs().clear();
			entity.getFieldXrefs().addAll(fieldXrefs);
			//entity.setFieldXrefs(fieldXrefs);
			if(entity.getMetadataElements() == null) {
				entity.setMetadataElements(new HashSet<MetadataElementPageTemplateXrefEntity>());
			}
			entity.getMetadataElements().clear();
			entity.getMetadataElements().addAll(renewedXrefs);
			//entity.setMetadataElements(renewedXrefs);
			if(StringUtils.isBlank(entity.getId())) {
				pageTemplateDAO.save(entity);
			} else {
				pageTemplateDAO.merge(entity);
			}
		}
		pageTemplateDAO.evictFromSecondLevelCache(entity);
		template.setId(entity.getId());

		return entity.getId();
	}

	@Override
	@Transactional
	public void delete(final String id) {
		final MetadataElementPageTemplateEntity entity = pageTemplateDAO.findById(id);
		if(entity != null) {
			pageTemplateDAO.delete(entity);
		}
	}

	@Override
    @Transactional(readOnly = true)
	public PageTempate getTemplate(TemplateRequest request) {
		MetadataElementPageTemplateEntity entity = null;
		if(StringUtils.isNotEmpty(request.getTemplateId())) {
			entity = pageTemplateDAO.findById(request.getTemplateId());
		} else if(StringUtils.isNotEmpty(request.getPatternId())) {
			final MetadataElementPageTemplateSearchBean searchBean = new MetadataElementPageTemplateSearchBean();
			searchBean.setDeepCopy(true);
			final String patternId = request.getPatternId();
			searchBean.addPatternId(patternId);
			final List<MetadataElementPageTemplateEntity> entityList = findEntityBeans(searchBean, 0, Integer.MAX_VALUE);
			if(CollectionUtils.isNotEmpty(entityList)) {
				if(entityList.size() > 1) {
					LOG.warn(String.format("Pattern %s mapped to multiple templates - can't fetch template", request.getPatternId()));
				} else {
					entity = entityList.get(0);
				}
			}
		}
		
		final LanguageEntity targetLanguage = getLanguage(request.getLanguageId());
		boolean isAdminRequest = request.isAdminRequest();
		
		final String objectId = StringUtils.trimToNull(request.getTargetObjectId());
		PageTempate template = null;
		if(entity != null) {
			Map<String, AbstractAttributeEntity> attributeName2AttributeMap = new HashMap<>();
			TemplateObjectProvider templateProvider = templateObjectHelper.getProvider(entity.getTemplateType().getId());
			if(templateProvider!=null){
				attributeName2AttributeMap = templateProvider.getAttributeName2ObjectAttributeMap(objectId);
			}
			
			/*
			 * If the user is unknown (self registration), the template is public, it's an admin request, or if the user is entitled to the template, create one
			 */
			if(entity.isPublic() || isAdminRequest || isEntitled(request.getRequesterId(), entity.getResource().getId())) {
				final String templateId = entity.getId();
				template = new PageTempate();
				template.setTemplateId(templateId);

				if(StringUtils.isNotBlank(entity.getDataModelUrl())){
					// set dynamic data model for this template
					TemplateGlobalDataModel templateDataModelIntf = null;
					final Map<String, Object> bindingMap = new HashMap<String, Object>();
					try {
						templateDataModelIntf = (TemplateGlobalDataModel)
								scriptRunner.instantiateClass(bindingMap, entity.getDataModelUrl());
					} catch (Throwable e) {
						LOG.error(String.format("Can't instantiate '%s' - skip", entity.getDataModelUrl()), e);
					}
					StopWatch w = new StopWatch();
					w.start();
					if(templateDataModelIntf!=null){
						String model=null;
						try {
							model=jacksonMapper.writeValueAsString(templateDataModelIntf.getDataModel(request.getRequesterId()));
						} catch (IOException e) {
							LOG.warn("Cannot get data model. Set it to null by default");
							model=null;
						}
						template.setJsonDataModel(model);
					}
					w.stop();
					LOG.info(String.format("=== Get data model for template took: %f sec", w.getTime()/1000.0));
				}

				if(StringUtils.isNotBlank(entity.getCustomJS())){
					template.setCustomJS(entity.getCustomJS());
				}

				if(CollectionUtils.isNotEmpty(entity.getMetadataElements())) {
					for(final MetadataElementPageTemplateXrefEntity xref : entity.getMetadataElements()) {
						final String elementId = xref.getId().getMetadataElementId();
						final Integer order = xref.getDisplayOrder();
						
						final MetadataElementEntity elementEntity = getElement(elementId, targetLanguage);//elementDAO.findById(elementId);
						if(elementEntity != null) {
							if(elementEntity.getIsPublic() || isAdminRequest || isEntitled(request.getRequesterId(), elementEntity.getResource().getId())) {
								final PageElement pageElement = new PageElement(elementEntity, order);
								
								if(targetLanguage != null) {
									//pageElement.setDisplayName(getLanguageValue(targetLanguage, elementEntity.getLanguageMap()));
									if(MapUtils.isNotEmpty(elementEntity.getLanguageMap())) {
										final LanguageMappingEntity displayNameXref = elementEntity.getLanguageMap().get(targetLanguage.getId());
										final String displayName = (displayNameXref != null) ? displayNameXref.getValue() : null;
										pageElement.setDisplayName(displayName);
									}
									pageElement.setDefaultValue(elementEntity.getStaticDefaultValue());
									if(StringUtils.isBlank(pageElement.getDefaultValue())) {
										//pageElement.setDefaultValue(getLanguageValue(targetLanguage, elementEntity.getDefaultValueLanguageMap()));
										final Map<String, LanguageMappingEntity> defValLanguageMap = elementEntity.getDefaultValueLanguageMap();
										final LanguageMappingEntity displayNameXref = (defValLanguageMap != null) ? defValLanguageMap.get(targetLanguage.getId()) : null;
										final String defaultValue = (displayNameXref != null) ? displayNameXref.getValue() : null;
										pageElement.setDefaultValue(defaultValue);
									}
									if(CollectionUtils.isNotEmpty(elementEntity.getValidValues())) {
										for(final MetadataValidValueEntity validValueEntity : elementEntity.getValidValues()) {
											final String validValueId = validValueEntity.getId();
											final String value = validValueEntity.getUiValue();
											//final String displayName = getLanguageValue(targetLanguage, validValueEntity.getLanguageMap());
											//final String displayName =  validValueEntity.getDisplayName();
											final LanguageMappingEntity displayNameXref = validValueEntity.getLanguageMap().get(targetLanguage.getId());
											final String displayName = (displayNameXref != null) ? displayNameXref.getValue() : null;
											final Integer displayOrder = validValueEntity.getDisplayOrder();
											if(displayName != null && value != null) {
												pageElement.addValidValue(new PageElementValidValue(validValueId, value, displayName, displayOrder));
											}
										}
									}
								}

								
								template.addElement(pageElement);
								final AbstractAttributeEntity attribute = attributeName2AttributeMap.get(elementEntity.getAttributeName());
								if(attribute != null) {
									if (!attribute.getIsMultivalued()) {
											pageElement.addUserValue(new PageElementValue(attribute.getId(), attribute.getValue()));
									} else {
										if (CollectionUtils.isNotEmpty(attribute.getValues())) {
											for (String value : attribute.getValues()) {
												pageElement.addUserValue(new PageElementValue(attribute.getId(), value));
											}
										}
									}
								}
							}
						}
					}
				}
				
				if(CollectionUtils.isNotEmpty(entity.getFieldXrefs())) {
					for(final MetadataFieldTemplateXrefEntity xref : entity.getFieldXrefs()) {
						final MetadataTemplateTypeFieldEntity field = xref.getField();
						if(targetLanguage != null) {
							final String displayName = xref.getDisplayName(targetLanguage);
							if(displayName != null) {
								final TemplateUIField uiField = new TemplateUIField();
								uiField.setId(field.getId());
								uiField.setName(displayName);
								uiField.setRequired(xref.isRequired());
								uiField.setEditable(xref.isEditable());
								uiField.setDisplayOrder(xref.getDisplayOrder());
								template.addUIField(uiField);
							} else {
								LOG.warn(String.format("UI Xref %s has a null display name for lang %s.  This field will not be used in the UI", xref, targetLanguage));
							}
						}
					}
				}
			}
		}
		return template;
	}
	
	private MetadataElementEntity getElement(final String id, final LanguageEntity language) {
       return elementDAO.findById(id);
	}
	
	private LanguageEntity getLanguage(final String languageId) {
		LanguageEntity entity = null;
		if(StringUtils.isNotBlank(languageId)) {
			entity = languageDAO.findById(languageId);
		}
			
		if(entity == null) {
			entity = languageDAO.getDefaultLanguage();
		}
		return entity;
	}

	private boolean isEntitled(final String userId, final String resourceId) {
		return authorizationManagerService.isEntitled(userId, resourceId);
	}
	
	private boolean isEntitled(final String userId, final ResourceEntity resource) {
		return resource == null || isEntitled(userId, resource.getId());
	}
	
	private boolean isEntitled(final String userId, final MetadataElementPageTemplateEntity template) {
		return template.isPublic() || isEntitled(userId, template.getResource());
	}
	
	private boolean isEntitled(final String userId, final MetadataElementEntity element) {
		return element.getIsPublic() || isEntitled(userId, element.getResource());
	}
	
	@Override
	@Transactional(readOnly=true)
	public void validate(BaseTemplateRequestModel request) throws Exception {
		final PageTempate pageTemplate = request.getPageTemplate();
		final String objectId = (request.getTargetObject() != null) ? request.getTargetObject().getId() : null;
		final LanguageEntity targetLanguage = getLanguage(request.getLanguageId());
	
		if(pageTemplate != null) {
			if(request.getTargetObject() == null || targetLanguage == null) {
				throw new PageTemplateException(ResponseCode.INVALID_ARGUMENTS);
			}
		
			final MetadataElementPageTemplateEntity template = pageTemplateDAO.findById(pageTemplate.getTemplateId());
			if(template == null) {
				throw new PageTemplateException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			/* only allow access if the user is entitled to the template */
			// TODO: confirm with LEV. not sure if we use it
//			if(!isEntitled(userId, template)) {
//				throw new PageTemplateException(ResponseCode.UNAUTHORIZED);
//			}
				
			/* get element map from template */
			//final Map<String, MetadataElementEntity> elementMap = getMetadataElementMap(template);
			
			/* create user attribute maps for fast access */
			TemplateObjectProvider templateProvider = templateObjectHelper.getProvider(template.getTemplateType().getId());
			if(templateProvider==null){
				throw new PageTemplateException(ResponseCode.INVALID_ARGUMENTS);
			}
			final Map<String, MetadataElementEntity> elementMap = getMetadataElementMap(template);

			templateProvider.isValid(request.getTargetObject());

			/* loop through all elements sent in the request */
			if(CollectionUtils.isNotEmpty(pageTemplate.getPageElements())) {
				for(final PageElement pageElement : pageTemplate.getPageElements()) {
					final String elementId = pageElement.getElementId();
					if(elementId != null && elementMap.containsKey(elementId)) {
						MetadataElementEntity element = elementMap.get(elementId);
						/* if the user is entitled to the element, do CRUD logic on the attributes */
//						if(isEntitled(userId, element)) {
							boolean isMultiSelect = element.getMetadataType() != null && StringUtils.equals(element.getMetadataType().getId(), "MULTI_SELECT");
							int numRequiredViolations = 0;
							
							if(isMultiSelect && pageElement.isEditable() && pageElement.isRequired() && CollectionUtils.isEmpty(pageElement.getUserValues())) {
								final PageTemplateException exception =  new PageTemplateException(ResponseCode.REQUIRED);
								exception.setElementName(getElementName(element, targetLanguage));
								throw exception;
							}
							
							if(CollectionUtils.isNotEmpty(pageElement.getUserValues())) {
								for(final PageElementValue elementValue : pageElement.getUserValues()) {
									boolean indexViolatesRequiredFlag = pageElement.isEditable() && pageElement.isRequired() && StringUtils.isBlank(elementValue.getValue());
									if(indexViolatesRequiredFlag) {
										numRequiredViolations++;
									}
									
									if(isMultiSelect) {
										if(numRequiredViolations == pageElement.getUserValues().size()) {
											final PageTemplateException exception =  new PageTemplateException(ResponseCode.REQUIRED);
											exception.setElementName(getElementName(element, targetLanguage));
											throw exception;
										}
									} else {
										if(indexViolatesRequiredFlag) {
											final PageTemplateException exception =  new PageTemplateException(ResponseCode.REQUIRED);
											exception.setElementName(getElementName(element, targetLanguage));
											throw exception;
										}
									}
									
									if(!isValid(elementValue, element, targetLanguage)) {
										final PageTemplateException exception =  new PageTemplateException(ResponseCode.INVALID_VALUE);
										exception.setCurrentValue(elementValue.getValue());
										exception.setElementName(getElementName(element, targetLanguage));
										throw exception;
									}
								}
							}
//						}
					}
				}
			}
		}
	}
	
	private PageTemplateAttributeToken getAttributesFromTokenInternal(final BaseTemplateRequestModel request) throws PageTemplateException {
		PageTemplateAttributeToken token = new PageTemplateAttributeToken();

		/* sets to hold persistent and new attributes */
		final List<AbstractAttributeEntity> deleteList = new LinkedList<AbstractAttributeEntity>();
		final List<AbstractAttributeEntity> updateList = new LinkedList<AbstractAttributeEntity>();
		final List<AbstractAttributeEntity> saveList = new LinkedList<AbstractAttributeEntity>();
		final List<AbstractAttributeEntity> nonChangedList = new LinkedList<AbstractAttributeEntity>();
		
		final PageTempate pageTemplate = request.getPageTemplate();
		final String objectId = request.getTargetObject().getId();
		final LanguageEntity targetLanguage = getLanguage(request.getLanguageId());
		
		if(pageTemplate != null) {
			if(request.getTargetObject() == null || targetLanguage == null) {
				throw new PageTemplateException(ResponseCode.INVALID_ARGUMENTS);
			}
		
			final MetadataElementPageTemplateEntity template = pageTemplateDAO.findById(pageTemplate.getTemplateId());
			if(template == null) {
				throw new PageTemplateException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			/* only allow access if the user is entitled to the template */
//			if(!isEntitled(userId, template)) {
//				throw new PageTemplateException(ResponseCode.UNAUTHORIZED);
//			}
				
			/* get element map from template */
			final Map<String, MetadataElementEntity> elementMap = getMetadataElementMap(template);

			Map<String, AbstractAttributeEntity> attributeName2AttributeMap = new HashMap<>();
			TemplateObjectProvider templateProvider = templateObjectHelper.getProvider(template.getTemplateType().getId());
			if(templateProvider!=null){
//				return templateProvider.getAttributesFromToken(pageTemplate, objectId, elementMap, targetLanguage);

				attributeName2AttributeMap = templateProvider.getAttributeName2ObjectAttributeMap(objectId);


				final Object entity = templateProvider.getEntity(objectId);
				final String userTypeId = templateProvider.getObjectMetadataTypeId(entity);

				/* loop through all elements sent in the request */
				if(CollectionUtils.isNotEmpty(pageTemplate.getPageElements())) {
					for(final PageElement pageElement : pageTemplate.getPageElements()) {
						final String elementId = pageElement.getElementId();
						if(elementId != null && elementMap.containsKey(elementId)) {
							MetadataElementEntity element = elementMap.get(elementId);
							/* if the user is entitled to the element, do CRUD logic on the attributes */
//							if(isEntitled(userId, element)) {
								if(CollectionUtils.isEmpty(pageElement.getUserValues())) { /* none sent - signals delete */
									if(attributeName2AttributeMap.containsKey(pageElement.getAttributeName())) {
										deleteList.add(attributeName2AttributeMap.get(pageElement.getAttributeName()));
									}
								} else { /* attributes sent - figure out weather to save or update */

									List<String> values = new ArrayList<>();
									boolean isMultiSelect = element.getMetadataType() != null && StringUtils.equals(element.getMetadataType().getId(), "MULTI_SELECT");
									int numRequiredViolations = 0;
									for(final PageElementValue elementValue : pageElement.getUserValues()) {
										if(StringUtils.isNotBlank(elementValue.getValue())) {
											values.add(elementValue.getValue());
										}
									}

									AbstractAttributeEntity attribute = attributeName2AttributeMap.get(pageElement.getAttributeName());
									if(CollectionUtils.isEmpty(values)) {
										if (attribute != null) {
											/* the value is empty - signals delete */
											deleteList.add(attribute);
										}
									} else { /* if the value is not empty, it's a possible update */
										if (attribute == null) { /* add new attribute */
											attribute = templateProvider.getNewAttributeInstance(element.getAttributeName(), entity);
											final MetadataElementEntity metadataElement = getMetadataElement(userTypeId, element);
											attribute.setMetadataElementId(metadataElement.getId());
											attribute.setIsMultivalued(isMultiSelect);
											if (isMultiSelect) {
												attribute.setValues(values);
											} else {
												attribute.setValue(values.get(0));
											}
												saveList.add(attribute);
										} else { /* update, if possible */
											final MetadataElementEntity metadataElement = getMetadataElement(userTypeId, element);
											boolean isChanged = (metadataElement == null) ? attribute.getMetadataElementId() != null
													: metadataElement.equals(attribute.getMetadataElementId());
											attribute.setMetadataElementId(attribute.getMetadataElementId());
											if (isMultiSelect) {
												/* only update if the values changed - optimization */
												if(!attribute.getIsMultivalued() || !values.equals(attribute.getValues())) {
													attribute.setIsMultivalued(true);
													attribute.setValues(values);
													isChanged = true;
												}
											} else {
													/* only update if the value changed - optimization */
												if(attribute.getIsMultivalued() || !StringUtils.equals(attribute.getValue(), values.get(0))) {
													attribute.setIsMultivalued(false);
													attribute.setValue(values.get(0));
													isChanged = true;
												}
											}
											if (isChanged) {
												updateList.add(attribute);
											} else {
												nonChangedList.add(attribute);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		token.setSaveList(saveList);
		token.setUpdateList(updateList);
		token.setDeleteList(deleteList);
		token.setNonChangedList(nonChangedList);
		return token;
		}
/*
    private MetadataElementEntity getMetadataElement(String typeId, MetadataElementEntity element) {
        if (typeId != null) {
            MetadataElementSearchBean searchBean = new MetadataElementSearchBean();
            searchBean.setAttributeName(element.getAttributeName());

            Set<String> ids = new HashSet<String>();
            ids.add(typeId);
            searchBean.setTypeIdSet(ids);

            List<MetadataElementEntity> list = elementDAO.getByExample(searchBean, 0, Integer.MAX_VALUE);
            if (list.size() > 0) {
                return list.get(0);
            }
        }
        return element;
    }*/

	@Override
	@Transactional(readOnly = true)
	public PageTemplateAttributeToken getAttributesFromTemplate(final BaseTemplateRequestModel request) {
		PageTemplateAttributeToken token = null;
		try {
			token = getAttributesFromTokenInternal(request);
		} catch(PageTemplateException e) {
			LOG.warn("Can't get attributes from template", e);
		} catch(Throwable e) {
			LOG.error("Can't get attributes from template", e);
		}
		return token;
	}

	@Override
	@Transactional
	public void saveTemplate(final UserProfileRequestModel request) throws PageTemplateException {
		final PageTemplateAttributeToken token = getAttributesFromTokenInternal(request);
		if(token != null) {
			if(CollectionUtils.isNotEmpty(token.getSaveList())) {
				for(final UserAttributeEntity entity : (List<UserAttributeEntity>)token.getSaveList()) {
					attributeDAO.save(entity);
				}
			}
			
			if(CollectionUtils.isNotEmpty(token.getUpdateList())) {
				for(final UserAttributeEntity entity : (List<UserAttributeEntity>)token.getUpdateList()) {
					attributeDAO.update(entity);
				}
			}
			
			if(CollectionUtils.isNotEmpty(token.getDeleteList())) {
				for(final UserAttributeEntity entity : (List<UserAttributeEntity>)token.getDeleteList()) {
					attributeDAO.delete(entity);
				}
			}
		}
	}

	@Override
	public void validate(UserProfileRequestModel request) throws PageTemplateException {

	}

	private String getElementName(final MetadataElementEntity entity, final LanguageEntity language) {
		String elementName = null;
		if(entity != null && language != null && MapUtils.isNotEmpty(entity.getLanguageMap())) {
			final LanguageMappingEntity mapping = entity.getLanguageMap().get(language.getId());
			if(mapping != null) {
				elementName = mapping.getValue();
			}
		}
		return elementName;
	}
	
	private boolean isValid(final PageElementValue elementValue, final MetadataElementEntity entity, final LanguageEntity language) {
		boolean retVal = false;
		if(StringUtils.isBlank(elementValue.getValue())) {
			retVal = true;
		} else if(elementValue != null && entity != null) {
			if(CollectionUtils.isEmpty(entity.getValidValues())) {
				retVal = true;
			} else {
				final Set<String> validValueSet = getValidValuesFromMetadataEntity(entity, language);
				if(validValueSet.contains(elementValue.getValue())) {
					retVal = true;
				}
			}
		}
		return retVal;
	}
	
	private Set<String> getValidValuesFromMetadataEntity(final MetadataElementEntity entity, final LanguageEntity language) {
		final Set<String> validValues = new HashSet<String>();
		if(entity != null && language != null) {
			if(CollectionUtils.isNotEmpty(entity.getValidValues())) {
				for(final MetadataValidValueEntity validValue : entity.getValidValues()) {
					if(validValue != null) {
						if(StringUtils.isNotBlank(validValue.getUiValue())) {
							validValues.add(validValue.getUiValue());
						} else if(MapUtils.isNotEmpty(validValue.getLanguageMap())) {
							final LanguageMappingEntity languageMapping = validValue.getLanguageMap().get(language.getId());
							if(languageMapping != null) {
								validValues.add(languageMapping.getValue());
							}
						}
					}
				}
			}
		}
		return validValues;
	}
	
	private Map<String, MetadataElementEntity> getMetadataElementMap(final MetadataElementPageTemplateEntity template) {
		final Map<String, MetadataElementEntity> elementMap = new HashMap<String, MetadataElementEntity>();
		if(CollectionUtils.isNotEmpty(template.getMetadataElements())) {
			for(final MetadataElementPageTemplateXrefEntity xref : template.getMetadataElements()) {
				MetadataElementEntity element = xref.getMetadataElement();
				if(element != null && element.getId() != null) {
					element = elementDAO.findById(element.getId());
					elementMap.put(element.getId(), element);
				}
			}
		}
		return elementMap;
	}

	@Override
	@Transactional(readOnly=true)
	public MetadataTemplateType getTemplateType(String id) {
		MetadataTemplateTypeEntity entity = templateTypeDAO.findById(id);
		return (entity != null) ? templateTypeDozerConverter.convertToDTO(entity, true) : null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<MetadataTemplateType> findTemplateTypes(MetadataTemplateTypeSearchBean searchBean, int from, int size) {
		List<MetadataTemplateTypeEntity> entityList = templateTypeDAO.getByExample(searchBean, from, size);
		return (entityList != null) ? templateTypeDozerConverter.convertToDTOList(entityList, (searchBean != null) ? searchBean.isDeepCopy() : false) : null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<MetadataTemplateTypeField> findUIFields(final MetadataTemplateTypeFieldSearchBean searchBean, final int from, final int size) {
		List<MetadataTemplateTypeFieldEntity> entityList = uiFieldDAO.getByExample(searchBean, from, size);
		return (entityList != null) ? uiFieldDozerConverter.convertToDTOList(entityList, (searchBean != null) ? searchBean.isDeepCopy() : false) : null;
	}
	
    @Override
    @Transactional(readOnly=true)
    public Integer countUIFields(final MetadataTemplateTypeFieldSearchBean searchBean){
        return uiFieldDAO.count(searchBean);
    }

	protected MetadataElementEntity getMetadataElement(String typeId, MetadataElementEntity element) {
		if (typeId != null) {
			MetadataElementSearchBean searchBean = new MetadataElementSearchBean();
			searchBean.setAttributeName(element.getAttributeName());

			Set<String> ids = new HashSet<String>();
			ids.add(typeId);
			searchBean.setTypeIdSet(ids);
			List<MetadataElementEntity> list = elementDAO.getByExample(searchBean, 0, Integer.MAX_VALUE);
			if (list.size() > 0) {
				return list.get(0);
			}
		}
		return element;
	}

	@Override
	@Transactional(readOnly=true)
	public MetadataElementPageTemplate findById(String id) {
		MetadataElementPageTemplateEntity entity = pageTemplateDAO.findById(id);
		return (entity!=null) ? templateDozerConverter.convertToDTO(entity, true) : null;
	}
}
