package org.openiam.authmanager.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.authmanager.common.model.AuthorizationMenu;
import org.openiam.authmanager.common.xref.ResourceResourceXref;
import org.openiam.authmanager.exception.AuthorizationMenuException;
import org.openiam.authmanager.service.AuthorizationManagerMenuService;
import org.openiam.authmanager.service.AuthorizationManagerMenuWebService;
import org.openiam.authmanager.util.AuthorizationConstants;
import org.openiam.authmanager.ws.request.MenuEntitlementsRequest;
import org.openiam.authmanager.ws.request.MenuRequest;
import org.openiam.authmanager.ws.response.MenuSaveResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.dto.ResourceRisk;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@WebService(endpointInterface = "org.openiam.authmanager.service.AuthorizationManagerMenuWebService", 
	targetNamespace = "urn:idm.openiam.org/srvc/authorizationmanager/menu/service", 
	portName = "AuthorizationManagerMenuWebServicePort",
	serviceName = "AuthorizationManagerMenuWebService")
@Service("authorizationManagerMenuWebService")
public class AuthorizationManagerMenuWebServiceImpl implements AuthorizationManagerMenuWebService {

	private static final Log log = LogFactory.getLog(AuthorizationManagerMenuWebServiceImpl.class);
	
	@Autowired
	private AuthorizationManagerMenuService menuService;
	
	@Autowired
	private ResourceService resourceService;
	
	@Override
	public AuthorizationMenu getMenuTreeForUserId(final MenuRequest request, final Language langauge) {
		final StopWatch sw = new StopWatch();
		sw.start();
		AuthorizationMenu retVal = null;
		if(request != null) {
			if(StringUtils.isNotEmpty(request.getUserId())) {
				if(StringUtils.isNotEmpty(request.getMenuRoot())) {
					retVal = menuService.getMenuTree(request.getMenuRoot(), request.getUserId());
				} else {
					retVal = menuService.getMenuTreeByName(request.getMenuName(), request.getUserId());
				}
			} else if(request.getLoginId() != null) {
				final AuthorizationManagerLoginId login = request.getLoginId();
				if(StringUtils.isNotEmpty(request.getMenuRoot())) {
					retVal = menuService.getMenuTree(request.getMenuRoot(), login.getLogin(), login.getManagedSysId());
				} else {
					retVal = menuService.getMenuTreeByName(request.getMenuName(), login.getLogin(), login.getManagedSysId());
				}
			}
		}
		sw.stop();
		if(log.isDebugEnabled()) {
			log.debug(String.format("getMenuTreeForUserId: request: %s, time: %s ms", request, sw.getTime()));
		}
		localize(retVal, langauge);
		return retVal;
		
	}

	@Override
	public AuthorizationMenu getMenuTree(final String menuId, final Language language) {
		final AuthorizationMenu menu = menuService.getMenuTree(menuId);
		localize(menu, language);
		return menu;
	}
	

	@Override
	public AuthorizationMenu getNonCachedMenuTree(final String menuId, final String principalId, final String principalType, final Language language) {
		final AuthorizationMenu menu = menuService.getNonCachedMenuTree(menuId, principalId, principalType);
		localize(menu, language);
		return menu;
	}
	
	private void localize(final AuthorizationMenu menu, final Language language) {
		if(menu != null && language != null) {
			menu.localize(language);
			
			AuthorizationMenu sibling = menu.getNextSibling();
			while(sibling != null) {
				localize(sibling, language);
				sibling = sibling.getNextSibling();
			}
			
			localize(menu.getFirstChild(), language);
		}
	}
	
	@Override
	//@Transactional
	public MenuSaveResponse deleteMenuTree(final String rootId) {
		final MenuSaveResponse response = new MenuSaveResponse();
		response.setStatus(ResponseStatus.SUCCESS);
		try {
			final ResourceEntity resource = resourceService.findResourceById(rootId);
			
			if(resource == null) {
				throw new AuthorizationMenuException(ResponseCode.MENU_DOES_NOT_EXIST, rootId);
			}
			
			if(CollectionUtils.isNotEmpty(resource.getChildResources())) {
				throw new AuthorizationMenuException(ResponseCode.HANGING_CHILDREN, resource.getName());
			}
			
			/*
			if(CollectionUtils.isNotEmpty(resource.getEntitlements())) {
				throw new AuthorizationMenuException(MenuError.HANGING_ENTITLEMENTS, resource.getName());
			}
			*/

			
			resourceService.deleteResource(rootId);
		} catch(AuthorizationMenuException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getResponseCode());
			response.setProblematicMenuName(e.getMenuName());
		} catch(Throwable e) {
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	//@Transactional
	public MenuSaveResponse saveMenuTree(final AuthorizationMenu root) {
		final MenuSaveResponse response = new MenuSaveResponse();
		response.setStatus(ResponseStatus.SUCCESS);
		
		try {
			setParents(null, root);
			final AuthorizationMenu currentRoot = menuService.getMenuTree(root.getId());
			
			final List<AuthorizationMenu> changedMenus = new LinkedList<AuthorizationMenu>();
			final List<AuthorizationMenu> newMenus = new LinkedList<AuthorizationMenu>();
			final List<AuthorizationMenu> deletedMenus = new LinkedList<AuthorizationMenu>();
			
			//final List<ResourceResourceXref> newXrefs = new LinkedList<ResourceResourceXref>();
			final Map<String, String> newResourceName2ParentIdMap = new HashMap<String, String>();
			final List<ResourceResourceXref> deletedXrefs = new LinkedList<ResourceResourceXref>();
			if(currentRoot != null) {
				/* put existing menus in a map */
				final List<AuthorizationMenu> currentMenus = getMenus(currentRoot);
				final Map<String, AuthorizationMenu> currentMenuMap = new HashMap<String, AuthorizationMenu>();
				for(final AuthorizationMenu menu : currentMenus) {
					currentMenuMap.put(menu.getId(), menu);
				}
				
				/* put incoming menus in a map */
				final List<AuthorizationMenu> incomingMenus = getMenus(root);
				final Map<String, AuthorizationMenu> incomingMenuMap = new HashMap<String, AuthorizationMenu>();
				for(final AuthorizationMenu menu : incomingMenus) {
					if(StringUtils.isNotBlank(menu.getId())) {
						incomingMenuMap.put(menu.getId(), menu);
					}
				}
				
				/* find new entries */
				for(final AuthorizationMenu menu : incomingMenus) {
					if(StringUtils.isEmpty(menu.getId())) {
						newMenus.add(menu);
						if(menu.getParent() != null) {
							newResourceName2ParentIdMap.put(menu.getName(), menu.getParent().getId());
						}
					}
				}
				
				/* find deleted entries */
				for(final String currentId : currentMenuMap.keySet()) {
					if(!incomingMenuMap.containsKey(currentId)) {
						final AuthorizationMenu currentMenu = currentMenuMap.get(currentId);
						deletedMenus.add(currentMenu);
						if(currentMenu.getParent() != null) {
							final ResourceResourceXref xref = new ResourceResourceXref();
							xref.setResourceId(currentMenu.getParent().getId());
							xref.setMemberResourceId(currentMenu.getId());
							deletedXrefs.add(xref);
						}
					}
				}
				
				/* find changed resources */
				for(final AuthorizationMenu menu : incomingMenus) {
					if(StringUtils.isNotEmpty(menu.getId())) {
						if(!menu.equals(currentMenuMap.get(menu.getId()))) {
							changedMenus.add(menu);
						}
					}
				}
				
				final List<ResourceEntity> resourcesToCreate = new LinkedList<ResourceEntity>();
				final List<ResourceEntity> resourcesToUpdate = new LinkedList<ResourceEntity>();
				final List<ResourceEntity> resourcesToDelete = new LinkedList<ResourceEntity>();
				
				/* mark menus for deletion */
				if(CollectionUtils.isNotEmpty(deletedMenus)) {
					final List<ResourceEntity> deletedResourceList = new LinkedList<ResourceEntity>();
					for(final AuthorizationMenu menu : deletedMenus) {
						final ResourceEntity resource = resourceService.findResourceById(menu.getId());
						if(resource != null) {
							deletedResourceList.add(resource);
						}
					}
					resourcesToDelete.addAll(deletedResourceList);
				}
				
				/* return error if Resource has Collections on it */
				if(CollectionUtils.isNotEmpty(resourcesToDelete)) {
					for(final ResourceEntity resource : resourcesToDelete) {
						if(CollectionUtils.isNotEmpty(resource.getChildResources())) {
							throw new AuthorizationMenuException(ResponseCode.HANGING_CHILDREN, resource.getName());
						}
						
						/*
						if(CollectionUtils.isNotEmpty(resource.getEntitlements())) {
							throw new AuthorizationMenuException(MenuError.HANGING_ENTITLEMENTS, resource.getName());
						}
						*/

					}
				}
				
				/* Find Menus to udpate */
				if(CollectionUtils.isNotEmpty(changedMenus)) {
					final Map<String, AuthorizationMenu> changedMenuMap = new HashMap<String, AuthorizationMenu>();
					for(final AuthorizationMenu menu : changedMenus) {
						changedMenuMap.put(menu.getId(), menu);
					}
					final List<ResourceEntity> resourceList = resourceService.findResourcesByIds(changedMenuMap.keySet());
					for(final ResourceEntity resource : resourceList) {
						final AuthorizationMenu menu = changedMenuMap.get(resource.getId());	
						
						final ResourceEntity existingResource = resourceService.findResourceByName(menu.getName());
						/* check that, if the user changed the name of the menu, it doesn't conflict with another resource with the same name */
						if(existingResource != null && !existingResource.getId().equals(resource.getId())) {
							throw new AuthorizationMenuException(ResponseCode.NAME_TAKEN, resource.getName());
						}
						
						merge(resource, menu);
					}
					resourcesToUpdate.addAll(resourceList);
				}
				
				/* find Menus to create */
				if(CollectionUtils.isNotEmpty(newMenus)) {
					final List<ResourceEntity> newResourceList = new LinkedList<ResourceEntity>();
					for(final AuthorizationMenu menu : newMenus) {
						final ResourceEntity resource = createResource(menu);
						newResourceList.add(resource);
						
						final ResourceEntity existingResource = resourceService.findResourceByName(resource.getName());
						/* check that, if the user changed the name of the menu, it doesn't conflict with another resource with the same name */
						if(existingResource != null) {
							throw new AuthorizationMenuException(ResponseCode.NAME_TAKEN, resource.getName());
						}
					}
					resourcesToCreate.addAll(newResourceList);
				}
				
				/* create the maps */
				final Map<String, ResourceEntity> resourcesToUpdateMap = new HashMap<String, ResourceEntity>();
				final Map<String, ResourceEntity> resourceToCreateMap = new HashMap<String, ResourceEntity>();
				final Map<String, ResourceEntity> resourceToDeleteMap = new HashMap<String, ResourceEntity>();
				for(final ResourceEntity resource : resourcesToUpdate) {
					resourcesToUpdateMap.put(resource.getId(), resource);
				}
				for(final ResourceEntity resource : resourcesToCreate) {
					resourceToCreateMap.put(resource.getId(), resource);
				}
				for(final ResourceEntity resource : resourcesToDelete) {
					resourceToDeleteMap.put(resource.getId(), resource);
				}
				
				/* set new xrefs, if any */
				if(CollectionUtils.isNotEmpty(resourcesToCreate)) {
					for(final ResourceEntity resource : resourcesToCreate) {
						final String parentId = newResourceName2ParentIdMap.get(resource.getName());
						if(!resourcesToUpdateMap.containsKey(parentId)) {
							final ResourceEntity parent = resourceService.findResourceById(parentId);
							resourcesToUpdateMap.put(parentId, parent);
						}
						final ResourceEntity parent = resourcesToUpdateMap.get(parentId);
						parent.addChildResource(resource);
					}
				}
				
				/* remove old xrefs, if any */
				if(CollectionUtils.isNotEmpty(deletedXrefs)) {
					for(final ResourceResourceXref xref : deletedXrefs) {
						if(!resourcesToUpdateMap.containsKey(xref.getResourceId())) {
							final ResourceEntity resource = resourceService.findResourceById(xref.getResourceId());
							resourcesToUpdateMap.put(resource.getId(), resource);
						}
						final ResourceEntity resource = resourcesToUpdateMap.get(xref.getResourceId());
						final ResourceEntity toDelete = resourceToDeleteMap.get(xref.getMemberResourceId());
						resource.removeChildResource(toDelete);
					}
				}
				
				menuService.processTreeUpdate(resourcesToCreate, resourcesToUpdate, resourcesToDelete);
			}
		} catch(AuthorizationMenuException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getResponseCode());
			response.setProblematicMenuName(e.getMenuName());
		} catch(Throwable e) {
			log.error("Can't save menu tree", e);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}
	
	private ResourceEntity createResource(final AuthorizationMenu menu) {
		final ResourceEntity resource = new ResourceEntity();
		resource.setURL(menu.getUrl());
		resource.setName(menu.getName());
		resource.setDisplayOrder(menu.getDisplayOrder());
        resource.setRisk(ResourceRisk.valueOf(menu.getRisk()));
		resource.setIsPublic(menu.getIsPublic());
		resource.setResourceType(resourceService.findResourceTypeById(AuthorizationConstants.MENU_ITEM_RESOURCE_TYPE));
		resource.setDisplayNameMap(convert(menu.getDisplayNameMap()));
		/*
		final ResourcePropEntity displayNameProp = new ResourcePropEntity();
		displayNameProp.setResource(resource);
		displayNameProp.setName(AuthorizationConstants.MENU_ITEM_DISPLAY_NAME_PROPERTY);
		displayNameProp.setPropValue(menu.getDisplayName());
		resource.addResourceProperty(displayNameProp);
		*/
		
		final ResourcePropEntity iconProp = new ResourcePropEntity();
		iconProp.setResource(resource);
		iconProp.setName(AuthorizationConstants.MENU_ITEM_ICON_PROPERTY);
		iconProp.setPropValue(menu.getIcon());
		resource.addResourceProperty(iconProp);
		
		final ResourcePropEntity visibleProp = new ResourcePropEntity();
		visibleProp.setResource(resource);
		visibleProp.setName(AuthorizationConstants.MENU_ITEM_IS_VISIBLE);
		visibleProp.setPropValue(Boolean.valueOf(menu.getIsVisible()).toString());
		resource.addResourceProperty(visibleProp);
		
		return resource;
	}
	
	private void merge(final ResourceEntity resource, final AuthorizationMenu menu) {
		resource.setURL(menu.getUrl());
		resource.setName(menu.getName());
		resource.setDisplayOrder(menu.getDisplayOrder());
        resource.setRisk(ResourceRisk.getByValue(menu.getRisk()));
		resource.setIsPublic(menu.getIsPublic());
		resource.setDisplayNameMap(convert(menu.getDisplayNameMap()));
		
		//ResourcePropEntity displayNameProp = resource.getResourceProperty(AuthorizationConstants.MENU_ITEM_DISPLAY_NAME_PROPERTY);
		ResourcePropEntity iconProp = resource.getResourceProperty(AuthorizationConstants.MENU_ITEM_ICON_PROPERTY);
		ResourcePropEntity visibleProp = resource.getResourceProperty(AuthorizationConstants.MENU_ITEM_IS_VISIBLE);
		
		if(visibleProp != null) {
			visibleProp.setPropValue(Boolean.valueOf(menu.getIsVisible()).toString());
		} else {
			visibleProp = new ResourcePropEntity();
			visibleProp.setResource(resource);
			visibleProp.setName(AuthorizationConstants.MENU_ITEM_IS_VISIBLE);
			visibleProp.setPropValue(Boolean.valueOf(menu.getIsVisible()).toString());
			resource.addResourceProperty(visibleProp);
		}
		
		/*
		if(displayNameProp != null) {
			displayNameProp.setPropValue(menu.getDisplayName());
		} else {
			displayNameProp = new ResourcePropEntity();
			displayNameProp.setResource(resource);
			displayNameProp.setName(AuthorizationConstants.MENU_ITEM_DISPLAY_NAME_PROPERTY);
			displayNameProp.setPropValue(menu.getDisplayName());
			resource.addResourceProperty(displayNameProp);
		}
		*/
		
		if(iconProp != null) {
			iconProp.setPropValue(menu.getIcon());
		} else {
			iconProp = new ResourcePropEntity();
			iconProp.setResource(resource);
			iconProp.setName(AuthorizationConstants.MENU_ITEM_ICON_PROPERTY);
			iconProp.setPropValue(menu.getIcon());
			resource.addResourceProperty(iconProp);
		}
	}
	
	private List<AuthorizationMenu> getMenus(final AuthorizationMenu menu) {
		final List<AuthorizationMenu> authMenus = new LinkedList<AuthorizationMenu>();
		if(menu != null) {
			authMenus.addAll(getMenus(menu.getFirstChild()));
			authMenus.addAll(getMenus(menu.getNextSibling()));
			authMenus.add(menu);
		}
		return authMenus;
	}
	
	private void setParents(final AuthorizationMenu parent, final AuthorizationMenu menu) {
		if(menu != null) {
			menu.setParent(parent);
			
			if(menu.getFirstChild() != null) {
				setParents(menu, menu.getFirstChild());
			}
			
			if(menu.getNextSibling() != null) {
				setParents(parent, menu.getNextSibling());
			}
		}
	}

	@Override
	//@Transactional
	public Response entitle(final MenuEntitlementsRequest menuEntitlementsRequest) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(menuEntitlementsRequest == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final String principalType = menuEntitlementsRequest.getPrincipalType();
			final String principalId = menuEntitlementsRequest.getPrincipalId();
			if(StringUtils.isBlank(principalType) || StringUtils.isBlank(principalId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			menuService.entitle(menuEntitlementsRequest);
			
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Can't save menu entitlements", e);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}
	
	private Map<String, LanguageMappingEntity> convert(final Map<String, LanguageMapping> transientMap) {
		final Map<String, LanguageMappingEntity> convertedMap = new HashMap<>();
		if(MapUtils.isNotEmpty(transientMap)) {
			for(final String key : transientMap.keySet()) {
				final LanguageMapping mapping = transientMap.get(key);
				final LanguageMappingEntity entity = new LanguageMappingEntity();
				entity.setId(mapping.getId());
				entity.setLanguageId(mapping.getLanguageId());
				entity.setReferenceId(mapping.getReferenceId());
				entity.setReferenceType(mapping.getReferenceType());
				entity.setValue(mapping.getValue());
				convertedMap.put(key, entity);
			}
		}
		return convertedMap;
	}

	@Override
	public boolean isUserAuthenticatedToMenuWithURL(final String userId, final String url, final String menuId, final boolean defaultResult) {
		return menuService.isUserAuthenticatedToMenuWithURL(userId, url, menuId, defaultResult);
	}
}
