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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.am.srvc.dao.URIPatternDao;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.service.LanguageDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.MetadataValidValueEntity;
import org.openiam.idm.srvc.meta.domain.pk.MetadataElementPageTemplateXrefIdEntity;
import org.openiam.idm.srvc.meta.dto.PageElement;
import org.openiam.idm.srvc.meta.dto.PageElementValidValue;
import org.openiam.idm.srvc.meta.dto.PageElementValue;
import org.openiam.idm.srvc.meta.dto.PageTempate;
import org.openiam.idm.srvc.meta.dto.TemplateRequest;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.searchbean.converter.MetadataElementTemplateSearchBeanConverter;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
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
				resource.setName(String.format("%s_%s", entity.getName(), "" + System.currentTimeMillis()));
	            resource.setResourceType(resourceTypeDAO.findById(uiTemplateResourceType));
	            resource.setIsPublic(true);
	            resourceDAO.save(resource);
	            entity.setResource(resource);
			}
			
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
		
		final String localeName = request.getLocaleName();
		boolean isSelfServiceRequest = request.isSelfserviceRequest();
		
		final String userId = StringUtils.trimToNull(request.getUserId());
		PageTempate template = null;
		if(entity != null) {
			final List<LanguageEntity> languageList = languageDAO.findAll();
			final Map<String, LanguageEntity> languageMap = new HashMap<String, LanguageEntity>();
			if(CollectionUtils.isNotEmpty(languageList)) {
				for(final LanguageEntity languageEntity : languageList) {
					languageMap.put(languageEntity.getLocale(), languageEntity);
				}
			}
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
			if(userId == null || entity.isPublic() || !isSelfServiceRequest || isEntitled(userId, entity.getResource().getResourceId())) {
				template = new PageTempate();
				template.setTemplateId(entity.getId());
				if(CollectionUtils.isNotEmpty(entity.getMetadataElements())) {
					for(final MetadataElementPageTemplateXrefEntity xref : entity.getMetadataElements()) {
						final String elementId = xref.getId().getMetadataElementId();
						final Integer order = xref.getDisplayOrder();
						
						final MetadataElementEntity elementEntity = elementDAO.findById(elementId);
						if(elementEntity != null) {
							if(userId == null || elementEntity.isPublic() || !isSelfServiceRequest || isEntitled(userId, elementEntity.getResource().getResourceId())) {
								final PageElement pageElement = new PageElement(elementEntity, order);
								
								if(StringUtils.isNotEmpty(localeName)) {
									final LanguageEntity targetLanguage = languageMap.get(localeName);
									if(targetLanguage != null) {
										pageElement.setDisplayName(getLanguageValue(targetLanguage, elementEntity.getLanguageMap()));
										pageElement.setDefaultValue(getLanguageValue(targetLanguage, elementEntity.getDefaultValueLanguageMap()));
										if(CollectionUtils.isNotEmpty(elementEntity.getValidValues())) {
											for(final MetadataValidValueEntity validValueEntity : elementEntity.getValidValues()) {
												final String validValueId = validValueEntity.getId();
												final String value = validValueEntity.getUiValue();
												final String displayName = getLanguageValue(targetLanguage, validValueEntity.getLanguageMap());
												pageElement.addValidValue(new PageElementValidValue(validValueId, value, displayName));
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
			}
		}
		return template;
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
}
