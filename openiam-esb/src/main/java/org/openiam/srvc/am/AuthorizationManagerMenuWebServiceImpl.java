package org.openiam.srvc.am;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dto.jdbc.AuthorizationMenu;
import org.openiam.authmanager.service.AuthorizationManagerMenuService;
import org.openiam.base.request.*;
import org.openiam.base.response.AuthorizationMenuResponse;
import org.openiam.base.response.BooleanResponse;
import org.openiam.mq.constants.AMMenuAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.srvc.AbstractApiService;
import org.openiam.util.AuthorizationConstants;
import org.openiam.base.response.MenuSaveResponse;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.dto.ResourceRisk;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@WebService(endpointInterface = "org.openiam.srvc.am.AuthorizationManagerMenuWebService",
	targetNamespace = "urn:idm.openiam.org/srvc/authorizationmanager/menu/service", 
	portName = "AuthorizationManagerMenuWebServicePort",
	serviceName = "AuthorizationManagerMenuWebService")
@Service("authorizationManagerMenuWebService")
public class AuthorizationManagerMenuWebServiceImpl  extends AbstractApiService implements AuthorizationManagerMenuWebService {

	private static final Log log = LogFactory.getLog(AuthorizationManagerMenuWebServiceImpl.class);
	
	public AuthorizationManagerMenuWebServiceImpl() {
		super(OpenIAMQueue.AMMenuQueue);
	}

	@Override
	public AuthorizationMenu getMenuTreeForUserId(final MenuRequest request, final Language language) {
		request.setLanguage(language);
		AuthorizationMenuResponse response= this.manageApiRequest(AMMenuAPI.MenuTreeForUser, request, AuthorizationMenuResponse.class);
		if(response.isFailure()){
			return null;
		}
		return response.getMenu();
	}

	@Override
	public AuthorizationMenu getMenuTree(final String menuId, final Language language) {
		MenuRequest request = new MenuRequest();
		request.setMenuRoot(menuId);
		request.setLanguage(language);

		AuthorizationMenuResponse response= this.manageApiRequest(AMMenuAPI.MenuTree, request, AuthorizationMenuResponse.class);
		if(response.isFailure()){
			return null;
		}
		return response.getMenu();
	}
	

	@Override
	public AuthorizationMenu getNonCachedMenuTree(final String menuId, final String principalId, final String principalType, final Language language) {
		MenuRequest request = new MenuRequest();
		request.setMenuRoot(menuId);
		request.setPrincipalId(principalId);
		request.setPrincipalType(principalType);
		request.setLanguage(language);

		AuthorizationMenuResponse response= this.manageApiRequest(AMMenuAPI.NonCachedMenuTree, request, AuthorizationMenuResponse.class);
		if(response.isFailure()){
			return null;
		}
		return response.getMenu();
	}
	
	@Override
	public MenuSaveResponse deleteMenuTree(final String rootId) {
		IdServiceRequest request = new IdServiceRequest();
		request.setId(rootId);
		return this.manageApiRequest(AMMenuAPI.DeleteMenuTree, request, MenuSaveResponse.class);
	}

	@Override
	public boolean isUserAuthenticatedToMenuWithURL(final String userId, final String url, final String menuId, final boolean defaultResult) {
		MenuRequest request = new MenuRequest();
		request.setMenuRoot(menuId);
		request.setUserId(userId);
		request.setUrl(url);
		request.setDefaultResult(defaultResult);

		BooleanResponse response= this.manageApiRequest(AMMenuAPI.IsUserAuthenticatedToMenuWithURL, request, BooleanResponse.class);
		if(response.isFailure()){
			return false;
		}
		return response.getValue();
	}

	@Override
	public void sweep() {
		this.sendAsync(AMMenuAPI.Sweep, new BaseServiceRequest());
	}

	@Override
	//@Transactional
	public MenuSaveResponse saveMenuTree(final AuthorizationMenu root) {
		AuthorizationMenuRequest request = new AuthorizationMenuRequest();
		request.setMenu(root);
		return this.manageApiRequest(AMMenuAPI.SaveMenuTree, request, MenuSaveResponse.class);

//		final MenuSaveResponse response = new MenuSaveResponse();
//		response.setStatus(ResponseStatus.SUCCESS);
//
//		try {
//			setParents(null, root);
//			final AuthorizationMenu currentRoot = menuService.getMenuTree(root.getId());
//
//			final List<AuthorizationMenu> changedMenus = new LinkedList<AuthorizationMenu>();
//			final List<AuthorizationMenu> newMenus = new LinkedList<AuthorizationMenu>();
//			final List<AuthorizationMenu> deletedMenus = new LinkedList<AuthorizationMenu>();
//
//			//final List<ResourceResourceXref> newXrefs = new LinkedList<ResourceResourceXref>();
//			final Map<String, String> newResourceName2ParentIdMap = new HashMap<String, String>();
//			final List<ResourceResourceXref> deletedXrefs = new LinkedList<ResourceResourceXref>();
//			if(currentRoot != null) {
//				/* put existing menus in a map */
//				final List<AuthorizationMenu> currentMenus = getMenus(currentRoot);
//				final Map<String, AuthorizationMenu> currentMenuMap = new HashMap<String, AuthorizationMenu>();
//				for(final AuthorizationMenu menu : currentMenus) {
//					currentMenuMap.put(menu.getId(), menu);
//				}
//
//				/* put incoming menus in a map */
//				final List<AuthorizationMenu> incomingMenus = getMenus(root);
//				final Map<String, AuthorizationMenu> incomingMenuMap = new HashMap<String, AuthorizationMenu>();
//				for(final AuthorizationMenu menu : incomingMenus) {
//					if(StringUtils.isNotBlank(menu.getId())) {
//						incomingMenuMap.put(menu.getId(), menu);
//					}
//				}
//
//				/* find new entries */
//				for(final AuthorizationMenu menu : incomingMenus) {
//					if(StringUtils.isEmpty(menu.getId())) {
//						newMenus.add(menu);
//						if(menu.getParent() != null) {
//							newResourceName2ParentIdMap.put(menu.getName(), menu.getParent().getId());
//						}
//					}
//				}
//
//				/* find deleted entries */
//				for(final String currentId : currentMenuMap.keySet()) {
//					if(!incomingMenuMap.containsKey(currentId)) {
//						final AuthorizationMenu currentMenu = currentMenuMap.get(currentId);
//						deletedMenus.add(currentMenu);
//						if(currentMenu.getParent() != null) {
//							final ResourceResourceXref xref = new ResourceResourceXref();
//							xref.setResource(new AuthorizationResource(currentMenu.getParent()));
//							xref.setMemberResource(new AuthorizationResource(currentMenu));
//							deletedXrefs.add(xref);
//						}
//					}
//				}
//
//				/* find changed resources */
//				for(final AuthorizationMenu menu : incomingMenus) {
//					if(StringUtils.isNotEmpty(menu.getId())) {
//						if(!menu.equals(currentMenuMap.get(menu.getId()))) {
//							changedMenus.add(menu);
//						}
//					}
//				}
//
//				final List<ResourceEntity> resourcesToCreate = new LinkedList<ResourceEntity>();
//				final List<ResourceEntity> resourcesToUpdate = new LinkedList<ResourceEntity>();
//				final List<ResourceEntity> resourcesToDelete = new LinkedList<ResourceEntity>();
//
//				/* mark menus for deletion */
//				if(CollectionUtils.isNotEmpty(deletedMenus)) {
//					final List<ResourceEntity> deletedResourceList = new LinkedList<ResourceEntity>();
//					for(final AuthorizationMenu menu : deletedMenus) {
//						final ResourceEntity resource = resourceService.findResourceById(menu.getId());
//						if(resource != null) {
//							deletedResourceList.add(resource);
//						}
//					}
//					resourcesToDelete.addAll(deletedResourceList);
//				}
//
//				/* return error if Resource has Collections on it */
//				if(CollectionUtils.isNotEmpty(resourcesToDelete)) {
//					for(final ResourceEntity resource : resourcesToDelete) {
//						if(CollectionUtils.isNotEmpty(resource.getChildResources())) {
//							throw new AuthorizationMenuException(ResponseCode.HANGING_CHILDREN, resource.getName());
//						}
//
//						/*
//						if(CollectionUtils.isNotEmpty(resource.getEntitlements())) {
//							throw new AuthorizationMenuException(MenuError.HANGING_ENTITLEMENTS, resource.getName());
//						}
//						*/
//
//					}
//				}
//
//				/* Find Menus to udpate */
//				if(CollectionUtils.isNotEmpty(changedMenus)) {
//					final Map<String, AuthorizationMenu> changedMenuMap = new HashMap<String, AuthorizationMenu>();
//					for(final AuthorizationMenu menu : changedMenus) {
//						changedMenuMap.put(menu.getId(), menu);
//					}
//					final List<ResourceEntity> resourceList = resourceService.findResourcesByIds(changedMenuMap.keySet());
//					for(final ResourceEntity resource : resourceList) {
//						final AuthorizationMenu menu = changedMenuMap.get(resource.getId());
//
//						final ResourceEntity existingResource = resourceService.findResourceByName(menu.getName());
//						/* check that, if the user changed the name of the menu, it doesn't conflict with another resource with the same name */
//						if(existingResource != null && !existingResource.getId().equals(resource.getId())) {
//							throw new AuthorizationMenuException(ResponseCode.NAME_TAKEN, resource.getName());
//						}
//
//						merge(resource, menu);
//					}
//					resourcesToUpdate.addAll(resourceList);
//				}
//
//				/* find Menus to create */
//				if(CollectionUtils.isNotEmpty(newMenus)) {
//					final List<ResourceEntity> newResourceList = new LinkedList<ResourceEntity>();
//					for(final AuthorizationMenu menu : newMenus) {
//						final ResourceEntity resource = createResource(menu);
//						newResourceList.add(resource);
//
//						final ResourceEntity existingResource = resourceService.findResourceByName(resource.getName());
//						/* check that, if the user changed the name of the menu, it doesn't conflict with another resource with the same name */
//						if(existingResource != null) {
//							throw new AuthorizationMenuException(ResponseCode.NAME_TAKEN, resource.getName());
//						}
//					}
//					resourcesToCreate.addAll(newResourceList);
//				}
//
//				/* create the maps */
//				final Map<String, ResourceEntity> resourcesToUpdateMap = new HashMap<String, ResourceEntity>();
//				final Map<String, ResourceEntity> resourceToCreateMap = new HashMap<String, ResourceEntity>();
//				final Map<String, ResourceEntity> resourceToDeleteMap = new HashMap<String, ResourceEntity>();
//				for(final ResourceEntity resource : resourcesToUpdate) {
//					resourcesToUpdateMap.put(resource.getId(), resource);
//				}
//				for(final ResourceEntity resource : resourcesToCreate) {
//					resourceToCreateMap.put(resource.getId(), resource);
//				}
//				for(final ResourceEntity resource : resourcesToDelete) {
//					resourceToDeleteMap.put(resource.getId(), resource);
//				}
//
//				/* set new xrefs, if any */
//				if(CollectionUtils.isNotEmpty(resourcesToCreate)) {
//					for(final ResourceEntity resource : resourcesToCreate) {
//						final String parentId = newResourceName2ParentIdMap.get(resource.getName());
//						if(!resourcesToUpdateMap.containsKey(parentId)) {
//							final ResourceEntity parent = resourceService.findResourceById(parentId);
//							resourcesToUpdateMap.put(parentId, parent);
//						}
//						final ResourceEntity parent = resourcesToUpdateMap.get(parentId);
//						parent.addChildResource(resource, null, null, null);
//					}
//				}
//
//				/* remove old xrefs, if any */
//				if(CollectionUtils.isNotEmpty(deletedXrefs)) {
//					for(final ResourceResourceXref xref : deletedXrefs) {
//						if(!resourcesToUpdateMap.containsKey(xref.getResource().getId())) {
//							final ResourceEntity resource = resourceService.findResourceById(xref.getResource().getId());
//							resourcesToUpdateMap.put(resource.getId(), resource);
//						}
//						final ResourceEntity resource = resourcesToUpdateMap.get(xref.getResource().getId());
//						final ResourceEntity toDelete = resourceToDeleteMap.get(xref.getMemberResource().getId());
//						resource.removeChildResource(toDelete);
//					}
//				}
//
//				menuService.processTreeUpdate(resourcesToCreate, resourcesToUpdate, resourcesToDelete);
//			}
//		} catch(AuthorizationMenuException e) {
//			response.setStatus(ResponseStatus.FAILURE);
//			response.setErrorCode(e.getResponseCode());
//			response.setProblematicMenuName(e.getMenuName());
//		} catch(Throwable e) {
//			log.error("Can't save menu tree", e);
//			response.setStatus(ResponseStatus.FAILURE);
//		}
//		return response;
	}

	@Override
	//@Transactional
	public Response entitle(final MenuEntitlementsRequest menuEntitlementsRequest) {
		return this.manageApiRequest(AMMenuAPI.Entitle, menuEntitlementsRequest, Response.class);
	}

}
