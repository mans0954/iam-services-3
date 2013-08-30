package org.openiam.idm.srvc.meta.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.usertype.UserVersionType;
import org.openiam.am.srvc.dao.URIPatternDao;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeFieldSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.service.LanguageDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.MetadataFieldTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.MetadataFieldTemplateXrefIDEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;
import org.openiam.idm.srvc.meta.domain.MetadataValidValueEntity;
import org.openiam.idm.srvc.meta.domain.pk.MetadataElementPageTemplateXrefIdEntity;
import org.openiam.idm.srvc.meta.dto.PageElement;
import org.openiam.idm.srvc.meta.dto.PageElementValidValue;
import org.openiam.idm.srvc.meta.dto.PageElementValue;
import org.openiam.idm.srvc.meta.dto.PageTempate;
import org.openiam.idm.srvc.meta.dto.PageTemplateAttributeToken;
import org.openiam.idm.srvc.meta.dto.TemplateRequest;
import org.openiam.idm.srvc.meta.dto.TemplateUIField;
import org.openiam.idm.srvc.meta.exception.PageTemplateException;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.searchbean.converter.MetadataElementTemplateSearchBeanConverter;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.idm.srvc.user.service.UserAttributeDAO;
import org.openiam.idm.srvc.user.service.UserDAO;
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
	private MetadataElementTemplateSearchBeanConverter templateSearchBeanConverter;
	
	@Value("${org.openiam.resource.type.ui.template}")
    private String uiTemplateResourceType;
	
	@Autowired
	private AuthorizationManagerService authorizationManagerService;
	
	private static Logger LOG = Logger.getLogger(MetadataElementTemplateServiceImpl.class);

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
				resource.setName(entity.getName() + "_" + System.currentTimeMillis());
	            resource.setResourceType(resourceTypeDAO.findById(uiTemplateResourceType));
	            resource.setIsPublic(true);
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
							final MetadataElementPageTemplateXrefEntity dbXref = xrefDAO.findById(id);
							if(dbXref != null) {
								dbXref.setDisplayOrder(xref.getDisplayOrder());
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
			
			final Set<MetadataFieldTemplateXrefEntity> fieldXrefs = new LinkedHashSet<MetadataFieldTemplateXrefEntity>();
			if(CollectionUtils.isNotEmpty(entity.getFieldXrefs())) {
				for(final MetadataFieldTemplateXrefEntity xref : entity.getFieldXrefs()) {
					if(xref != null) {
						final MetadataFieldTemplateXrefIDEntity id = xref.getId();
						final String fieldId = id.getFieldId();
						if(id != null && StringUtils.isNotBlank(fieldId) && templateType.getField(fieldId) != null) {
							final MetadataFieldTemplateXrefEntity dbXref = uiFieldXrefDAO.findById(id);
							boolean isRequired = templateType.getField(id.getFieldId()).isRequired() ? true : xref.isRequired();
							if(dbXref != null) {
								dbXref.setRequired(isRequired);
								dbXref.setEditable(xref.isEditable());
								dbXref.setDisplayOrder(xref.getDisplayOrder());
								fieldXrefs.add(dbXref);
							} else {
								xref.setRequired(isRequired);
								xref.setTemplate(entity);
								xref.setField(uiFieldDAO.findById(fieldId));
								fieldXrefs.add(xref);
							}
						}
					}
				}
			}
			entity.setFieldXrefs(fieldXrefs);
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

	@Override
	public PageTempate getTemplate(TemplateRequest request) {
		MetadataElementPageTemplateEntity entity = null;
		if(StringUtils.isNotEmpty(request.getTemplateId())) {
			entity = pageTemplateDAO.findById(request.getTemplateId());
		} else if(StringUtils.isNotEmpty(request.getPatternId())) {
			final MetadataElementPageTemplateSearchBean searchBean = new MetadataElementPageTemplateSearchBean();
			searchBean.setDeepCopy(true);
			final String patternId = request.getPatternId();
			searchBean.addPatternId(patternId);
			final List<MetadataElementPageTemplateEntity> entityList = findBeans(searchBean, 0, Integer.MAX_VALUE);
			if(CollectionUtils.isNotEmpty(entityList)) {
				if(entityList.size() > 1) {
					LOG.warn(String.format("Pattern %s mapped to multiple templates - can't fetch template", request.getPatternId()));
				} else {
					entity = entityList.get(0);
				}
			}
		}
		
		final LanguageEntity targetLanguage = getLanguage(request);
		boolean isAdminRequest = request.isAdminRequest();
		
		final String userId = StringUtils.trimToNull(request.getUserId());
		PageTempate template = null;
		if(entity != null) {
			final Map<String, List<UserAttributeEntity>> metadataElementId2UserAttributeMap = new HashMap<String, List<UserAttributeEntity>>();
			if(userId != null) {
				final List<UserAttributeEntity> attributeList = attributeDAO.findUserAttributes(userId);
				for(final UserAttributeEntity attribute : attributeList) {
					if(attribute.getElement() != null) {
						final String elementId = attribute.getElement().getId();
						if(!metadataElementId2UserAttributeMap.containsKey(elementId)) {
							metadataElementId2UserAttributeMap.put(elementId, new LinkedList<UserAttributeEntity>());
						}
						metadataElementId2UserAttributeMap.get(elementId).add(attribute);
					}
				}
			}
			
			/*
			 * If the user is unknown (self registration), the template is public, it's an admin request, or if the user is entitled to the template, create one
			 */
			if(entity.isPublic() || isAdminRequest || isEntitled(userId, entity.getResource().getResourceId())) {
				final String templateId = entity.getId();
				template = new PageTempate();
				template.setTemplateId(templateId);
				if(CollectionUtils.isNotEmpty(entity.getMetadataElements())) {
					for(final MetadataElementPageTemplateXrefEntity xref : entity.getMetadataElements()) {
						final String elementId = xref.getId().getMetadataElementId();
						final Integer order = xref.getDisplayOrder();
						
						final MetadataElementEntity elementEntity = elementDAO.findById(elementId);
						if(elementEntity != null) {
							if(elementEntity.isPublic() || isAdminRequest || isEntitled(userId, elementEntity.getResource().getResourceId())) {
								final PageElement pageElement = new PageElement(elementEntity, order);
								
								if(targetLanguage != null) {
									if(targetLanguage != null) {
										pageElement.setDisplayName(getLanguageValue(targetLanguage, elementEntity.getLanguageMap()));
										pageElement.setDefaultValue(elementEntity.getStaticDefaultValue());
										if(StringUtils.isBlank(pageElement.getDefaultValue())) {
											pageElement.setDefaultValue(getLanguageValue(targetLanguage, elementEntity.getDefaultValueLanguageMap()));
										}
										if(CollectionUtils.isNotEmpty(elementEntity.getValidValues())) {
											for(final MetadataValidValueEntity validValueEntity : elementEntity.getValidValues()) {
												final String validValueId = validValueEntity.getId();
												final String value = validValueEntity.getUiValue();
												final String displayName = getLanguageValue(targetLanguage, validValueEntity.getLanguageMap());
												final Integer displayOrder = validValueEntity.getDisplayOrder();
												pageElement.addValidValue(new PageElementValidValue(validValueId, value, displayName, displayOrder));
											}
										}
									}
								}
								
								
								template.addElement(pageElement);
								if(MapUtils.isNotEmpty(metadataElementId2UserAttributeMap)) {
									final List<UserAttributeEntity> attributeList = metadataElementId2UserAttributeMap.get(elementEntity.getId());
									if(CollectionUtils.isNotEmpty(attributeList)) {
										for(final UserAttributeEntity attribute : attributeList) {
											pageElement.addUserValue(new PageElementValue(attribute.getId(), attribute.getValue()));
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
						final TemplateUIField uiField = new TemplateUIField();
						uiField.setId(field.getId());
						uiField.setName(field.getName());
						uiField.setRequired(xref.isRequired());
						uiField.setEditable(xref.isEditable());
						uiField.setDisplayOrder(xref.getDisplayOrder());
						template.addUIField(uiField);
					}
				}
			}
		}
		return template;
	}
	
	private LanguageEntity getLanguage(final TemplateRequest request) {
		return getLanguage(request.getLanguageId(), request.getLanguageCode(), request.getLocaleName());
	}
	
	private LanguageEntity getLanguage(final UserProfileRequestModel request) {
		return getLanguage(request.getLanguageId(), request.getLanguageCode(), request.getLocale());
	}
	
	private LanguageEntity getLanguage(final String languageId, final String languageCode, final String localeName) {
		LanguageEntity entity = null;
		if(StringUtils.isNotBlank(languageId)) {
			entity = languageDAO.findById(languageId);
		} else {
			if(StringUtils.isNotBlank(languageCode)) {
				entity = languageDAO.getByCode(languageCode);
			}
			
			if(entity == null && StringUtils.isNotBlank(localeName)) {
				entity = languageDAO.getByLocale(localeName);
			}
				
		}
		if(entity == null) {
			entity = languageDAO.getDefaultLanguage();
		}
		return entity;
	}
	
	private String getLanguageValue(final LanguageEntity targetLanguage, final Map<String, LanguageMappingEntity> languageMap) {
		String retVal = null;
		if(targetLanguage != null) {
			final String targetLanguageId = targetLanguage.getLanguageId();
			if(MapUtils.isNotEmpty(languageMap)) {
				for(final String languageId : languageMap.keySet()) {
					if(StringUtils.equals(targetLanguageId, languageId)) {
						retVal = languageMap.get(languageId).getValue();
						break;
					}
				}
			}
		}
		return retVal;
	}

	private boolean isEntitled(final String userId, final String resourceId) {
		final AuthorizationResource authResource = new AuthorizationResource();
		authResource.setId(resourceId);
		return authorizationManagerService.isEntitled(userId, authResource);
	}
	
	private boolean isEntitled(final String userId, final ResourceEntity resource) {
		return resource == null || isEntitled(userId, resource.getResourceId());
	}
	
	private boolean isEntitled(final String userId, final MetadataElementPageTemplateEntity template) {
		return template.isPublic() || isEntitled(userId, template.getResource());
	}
	
	private boolean isEntitled(final String userId, final MetadataElementEntity element) {
		return element.isPublic() || isEntitled(userId, element.getResource());
	}
	
	@Override
	public void validate(UserProfileRequestModel request) throws PageTemplateException {
		final PageTempate pageTemplate = request.getPageTemplate();
		final String userId = (request.getUser() != null) ? request.getUser().getUserId() : null;
		final LanguageEntity targetLanguage = getLanguage(request);
	
		if(pageTemplate != null) {
			if(request.getUser() == null || targetLanguage == null) {
				throw new PageTemplateException(ResponseCode.INVALID_ARGUMENTS);
			}
		
			final MetadataElementPageTemplateEntity template = pageTemplateDAO.findById(pageTemplate.getTemplateId());
			if(template == null) {
				throw new PageTemplateException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			/* only allow access if the user is entitled to the template */
			if(!isEntitled(userId, template)) {
				throw new PageTemplateException(ResponseCode.UNAUTHORIZED);
			}
				
			/* get element map from template */
			final Map<String, MetadataElementEntity> elementMap = getMetadataElementMap(template);
			
			/* create user attribute maps for fast access */
			final List<UserAttributeEntity> attributes = attributeDAO.findUserAttributes(userId, elementMap.keySet());
			final Map<String, UserAttributeEntity> id2UserAttributeMap = new HashMap<String, UserAttributeEntity>();
			final Map<String, List<UserAttributeEntity>> metadataId2UserAttributeMap = new HashMap<String, List<UserAttributeEntity>>();
			if(CollectionUtils.isNotEmpty(attributes)) {
				for(final UserAttributeEntity attribute : attributes) {
					final String elementId = attribute.getElement().getId();
					id2UserAttributeMap.put(attribute.getId(), attribute);
					if(!metadataId2UserAttributeMap.containsKey(elementId)) {
						metadataId2UserAttributeMap.put(elementId, new LinkedList<UserAttributeEntity>());
					}
					metadataId2UserAttributeMap.get(elementId).add(attribute);
				}
			}
			
			/* loop through all elements sent in the request */
			if(CollectionUtils.isNotEmpty(pageTemplate.getPageElements())) {
				for(final PageElement pageElement : pageTemplate.getPageElements()) {
					final String elementId = pageElement.getElementId();
					if(elementId != null && elementMap.containsKey(elementId)) {
						MetadataElementEntity element = elementMap.get(elementId);
						/* if the user is entitled to the element, do CRUD logic on the attributes */
						if(isEntitled(userId, element)) {
							boolean isMultiSelect = element.getMetadataType() != null && StringUtils.equals(element.getMetadataType().getMetadataTypeId(), "MULTI_SELECT");
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
						}
					}
				}
			}
		}
	}
	
	private PageTemplateAttributeToken getAttributesFromTokenInternal(final UserProfileRequestModel request) throws PageTemplateException {
		final PageTemplateAttributeToken token = new PageTemplateAttributeToken();
		
		/* sets to hold persistent and new attributes */
		final List<UserAttributeEntity> deleteList = new LinkedList<UserAttributeEntity>();
		final List<UserAttributeEntity> updateList = new LinkedList<UserAttributeEntity>();
		final List<UserAttributeEntity> saveList = new LinkedList<UserAttributeEntity>();
		
		final PageTempate pageTemplate = request.getPageTemplate();
		final String userId = request.getUser().getUserId();
		final LanguageEntity targetLanguage = getLanguage(request);
		
		if(pageTemplate != null) {
			if(request.getUser() == null || targetLanguage == null) {
				throw new PageTemplateException(ResponseCode.INVALID_ARGUMENTS);
			}
		
			final MetadataElementPageTemplateEntity template = pageTemplateDAO.findById(pageTemplate.getTemplateId());
			if(template == null) {
				throw new PageTemplateException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			/* only allow access if the user is entitled to the template */
			if(!isEntitled(userId, template)) {
				throw new PageTemplateException(ResponseCode.UNAUTHORIZED);
			}
				
			/* get element map from template */
			final Map<String, MetadataElementEntity> elementMap = getMetadataElementMap(template);
			
			/* create user attribute maps for fast access */
			final List<UserAttributeEntity> attributes = attributeDAO.findUserAttributes(userId, elementMap.keySet());
			final Map<String, UserAttributeEntity> id2UserAttributeMap = new HashMap<String, UserAttributeEntity>();
			final Map<String, List<UserAttributeEntity>> metadataId2UserAttributeMap = new HashMap<String, List<UserAttributeEntity>>();
			if(CollectionUtils.isNotEmpty(attributes)) {
				for(final UserAttributeEntity attribute : attributes) {
					final String elementId = attribute.getElement().getId();
					id2UserAttributeMap.put(attribute.getId(), attribute);
					if(!metadataId2UserAttributeMap.containsKey(elementId)) {
						metadataId2UserAttributeMap.put(elementId, new LinkedList<UserAttributeEntity>());
					}
					metadataId2UserAttributeMap.get(elementId).add(attribute);
				}
			}
			
			final UserEntity user = (userId != null) ? userDAO.findById(userId) : new UserEntity();
			
			/* loop through all elements sent in the request */
			if(CollectionUtils.isNotEmpty(pageTemplate.getPageElements())) {
				for(final PageElement pageElement : pageTemplate.getPageElements()) {
					final String elementId = pageElement.getElementId();
					if(elementId != null && elementMap.containsKey(elementId)) {
						MetadataElementEntity element = elementMap.get(elementId);
						/* if the user is entitled to the element, do CRUD logic on the attributes */
						if(isEntitled(userId, element)) {
							if(CollectionUtils.isEmpty(pageElement.getUserValues())) { /* none sent - signals delete */
								if(metadataId2UserAttributeMap.containsKey(elementId)) {
									deleteList.addAll(metadataId2UserAttributeMap.get(elementId));
								}
							} else { /* attributes sent - figure out weather to save or update */
								
								boolean isMultiSelect = element.getMetadataType() != null && StringUtils.equals(element.getMetadataType().getMetadataTypeId(), "MULTI_SELECT");
								int numRequiredViolations = 0;
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
									
									/* new attribute */
									if(StringUtils.isBlank(elementValue.getUserAttributeId())) {
										
										/* only create if there's a value */
										if(StringUtils.isNotBlank(elementValue.getValue())) {
											final UserAttributeEntity userAttribute = new UserAttributeEntity();
											userAttribute.setElement(element);
											userAttribute.setName(String.format("%s_%s", element.getAttributeName(), System.nanoTime()));
											userAttribute.setUser(user);
											userAttribute.setValue(elementValue.getValue());
											saveList.add(userAttribute);
										}
									} else { /* update, if possible */
										final UserAttributeEntity attribute = id2UserAttributeMap.get(elementValue.getUserAttributeId());
										if(attribute != null) {
											/* if the value is not empty, it's a possible update */
											if(StringUtils.isNotBlank(elementValue.getValue())) {
												/* only update if the value changed - optimization */
												if(!StringUtils.equals(attribute.getValue(), elementValue.getValue())) {
													attribute.setValue(elementValue.getValue());
													updateList.add(attribute);
												}
											} else { /* the value is empty - signals delete */
												deleteList.add(attribute);
											}
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
		return token;
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageTemplateAttributeToken getAttributesFromTemplate(final UserProfileRequestModel request) {
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
				for(final UserAttributeEntity entity : token.getSaveList()) {
					attributeDAO.save(entity);
				}
			}
			
			if(CollectionUtils.isNotEmpty(token.getUpdateList())) {
				for(final UserAttributeEntity entity : token.getUpdateList()) {
					attributeDAO.update(entity);
				}
			}
			
			if(CollectionUtils.isNotEmpty(token.getDeleteList())) {
				for(final UserAttributeEntity entity : token.getDeleteList()) {
					attributeDAO.delete(entity);
				}
			}
		}
	}
	
	private String getElementName(final MetadataElementEntity entity, final LanguageEntity language) {
		String elementName = null;
		if(entity != null && language != null && MapUtils.isNotEmpty(entity.getLanguageMap())) {
			final LanguageMappingEntity mapping = entity.getLanguageMap().get(language.getLanguageId());
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
							final LanguageMappingEntity languageMapping = validValue.getLanguageMap().get(language.getLanguageId());
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
				final MetadataElementEntity element = xref.getMetadataElement();
				if(element != null) {
					elementMap.put(element.getId(), element);
				}
			}
		}
		return elementMap;
	}

	@Override
	public MetadataTemplateTypeEntity getTemplateType(String id) {
		return templateTypeDAO.findById(id);
	}

	@Override
	public List<MetadataTemplateTypeEntity> findTemplateTypes(
			MetadataTemplateTypeEntity entity, int from, int size) {
		return templateTypeDAO.getByExample(entity, from, size);
	}

	@Override
	public List<MetadataTemplateTypeFieldEntity> findUIFields(final MetadataTemplateTypeFieldSearchBean searchBean, final int from, final int size) {
		return uiFieldDAO.getByExample(searchBean, from, size);
	}
    @Override
    public Integer countUIFields(final MetadataTemplateTypeFieldSearchBean searchBean){
        return uiFieldDAO.count(searchBean);
    }
}
