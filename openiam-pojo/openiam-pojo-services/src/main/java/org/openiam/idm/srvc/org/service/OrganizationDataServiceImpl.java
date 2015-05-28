package org.openiam.idm.srvc.org.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.LanguageDozerConverter;
import org.openiam.dozer.converter.LocationDozerConverter;
import org.openiam.dozer.converter.OrganizationDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AddressSearchBean;
import org.openiam.idm.searchbeans.LocationSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.access.service.AccessRightProcessor;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.loc.domain.LocationEntity;
import org.openiam.idm.srvc.loc.dto.Location;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebParam;
import javax.jws.WebService;

import java.util.*;

//import diamelle.common.continfo.*;
//import diamelle.base.prop.*;

/**
 * <code>OrganizationManager</code> provides a service level interface to the
 * Organization components and its dependant objects as well as search
 * capability.<br>
 * <p/>
 * 
 * @author OpenIAm
 * @version 2
 */

@WebService(endpointInterface = "org.openiam.idm.srvc.org.service.OrganizationDataService",
            targetNamespace = "urn:idm.openiam.org/srvc/org/service",
            portName = "OrganizationDataWebServicePort",
            serviceName = "OrganizationDataWebService")
@Service("orgManager")
public class OrganizationDataServiceImpl implements OrganizationDataService {

    private static final Log log = LogFactory.getLog(OrganizationDataServiceImpl.class);

    @Autowired
    private LocationDozerConverter locationDozerConverter;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private OrganizationDozerConverter organizationDozerConverter;

    @Autowired
    private LanguageDozerConverter languageConverter;
    
    @Autowired
    private AccessRightProcessor accessRightProcessor;

    @Override
    /**
     * Only for internal system use, without @LocalizedServiceGet
     */
    public Organization getOrganization(final String orgId, String requesterId) {
        return this.getOrganizationLocalized(orgId, requesterId, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public Organization getOrganizationLocalized(final String orgId, String requesterId, final Language language) {
        final OrganizationEntity entity = organizationService.getOrganizationLocalized(orgId, requesterId, languageConverter.convertToEntity(language, false));
        return organizationDozerConverter.convertToDTO(entity, true);
    }
    
    @Override
	public int getNumOfOrganizationsForUser(final String userId, final String requesterId) {
    	return organizationService.getNumOfOrganizationsForUser(userId, requesterId);
	}

    @Override
    /**
     * for internal use only, without  @LocalizedServiceGet
     */
    public List<Organization> getOrganizationsForUser(final String userId, final String requesterId, final int from, final int size) {
        return this.getOrganizationsForUserLocalized(userId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
	public List<Organization> getOrganizationsForUserLocalized(final String userId, final String requesterId, final int from, final int size, final Language language) {
    	final List<OrganizationEntity> ogranizationEntity = organizationService.getOrganizationsForUser(userId, requesterId, from, size, languageConverter.convertToEntity(language, false));
        return organizationDozerConverter.convertToDTOList(ogranizationEntity, false);
	}

    @Override
    @Deprecated
    public List<Organization> findBeans(final OrganizationSearchBean searchBean, final String requesterId, final int from, final int size) {
        return this.findBeansLocalized(searchBean, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    public List<Organization> findBeansLocalized(final OrganizationSearchBean searchBean, final String requesterId, final int from, final int size, final Language language) {
        final List<OrganizationEntity> entityList = organizationService.findBeans(searchBean, requesterId, from, size, languageConverter.convertToEntity(language, false));
        final List<Organization> dtoList = organizationDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy());
        accessRightProcessor.process(searchBean, dtoList, entityList);
        return dtoList;
    }

    @Override
    @Deprecated
    public List<Organization> getParentOrganizations(String orgId, String requesterId, final int from, final int size) {
        return this.getParentOrganizationsLocalized(orgId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Deprecated
    public List<Organization> getParentOrganizationsLocalized(String orgId, String requesterId, final int from, final int size, final Language language) {
    	final OrganizationSearchBean sb = new OrganizationSearchBean();
    	sb.addChildId(orgId);
    	return findBeansLocalized(sb, requesterId, from, size, language);
    }

    @Override
    @Deprecated
    public List<Organization> getChildOrganizations(String orgId, String requesterId, final int from, final int size) {
        return this.getChildOrganizationsLocalized(orgId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Deprecated
    public List<Organization> getChildOrganizationsLocalized(String orgId, String requesterId, final int from, final int size, final Language language) {
    	final OrganizationSearchBean sb = new OrganizationSearchBean();
    	sb.addParentId(orgId);
    	return findBeansLocalized(sb, requesterId, from, size, language);
    }

    @Override
    @Deprecated
    public int getNumOfParentOrganizations(String orgId, String requesterId) {
        final OrganizationSearchBean sb = new OrganizationSearchBean();
        sb.addChildId(orgId);
        return count(sb, requesterId);
    }

    @Override
    @Deprecated
    public int getNumOfChildOrganizations(String orgId, String requesterId) {
        final OrganizationSearchBean sb = new OrganizationSearchBean();
        sb.addParentId(orgId);
        return count(sb, requesterId);
    }

    @Override
    public int count(final OrganizationSearchBean searchBean, String requesterId) {
        return organizationService.count(searchBean, requesterId);
    }

    @Override
    @Deprecated
    public List<Organization> getAllowedParentOrganizationsForType(final String orgTypeId, String requesterId){
        return this.getAllowedParentOrganizationsForTypeLocalized(orgTypeId, requesterId, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    public List<Organization> getAllowedParentOrganizationsForTypeLocalized(final String orgTypeId, String requesterId, final Language language){
        final List<OrganizationEntity> entityList = organizationService.getAllowedParentOrganizationsForType(orgTypeId, requesterId, languageConverter.convertToEntity(language, false));
        final List<Organization> resultList = organizationDozerConverter.convertToDTOList(entityList, false);
        return resultList;
    }

    @Override
    @Deprecated
    public List<Organization> findOrganizationsByAttributeValue(String attrName, String attrValue) {
        return this.findOrganizationsByAttributeValueLocalized(attrName, attrValue, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Organization> findOrganizationsByAttributeValueLocalized(String attrName, String attrValue, final Language language) {
        return organizationDozerConverter.convertToDTOList(
                organizationService.findOrganizationsByAttributeValue(attrName, attrValue, languageConverter.convertToEntity(language, false)), true);
    }


    @Override
    @Deprecated
    public List<Organization> getOrganizationsForUserByType(final String userId, final String requesterId, final String organizationTypeId) {
        return this.getOrganizationsForUserByTypeLocalized(userId, requesterId, organizationTypeId, getDefaultLanguage());
    }

	@Override
	@LocalizedServiceGet
	public List<Organization> getOrganizationsForUserByTypeLocalized(final String userId, final String requesterId, final String organizationTypeId, final Language language) {
		final OrganizationSearchBean searchBean = new OrganizationSearchBean();
		searchBean.addUserId(userId);
		searchBean.setOrganizationTypeId(organizationTypeId);
		searchBean.setDeepCopy(false);
		return findBeansLocalized(searchBean, requesterId, 0, Integer.MAX_VALUE, language);
	}

    @Override
    public Response addUserToOrg(final String orgId, final String userId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (orgId == null || userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            organizationService.addUserToOrg(orgId, userId);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Can't save resource type", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response removeUserFromOrg(String orgId, String userId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (orgId == null || userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            organizationService.removeUserFromOrg(orgId, userId);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Can't save resource type", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    private Language getDefaultLanguage(){
        Language lang = new Language();
        lang.setId("1");
        return lang;
    }

    @Override
    public Response saveOrganization(final Organization organization, final String requesterId) {
        return saveOrganizationWithSkipPrePostProcessors(organization, requesterId, false);
    }

    @Override
    public Response saveOrganizationWithSkipPrePostProcessors(final Organization organization, final String requestorId, final boolean skipPrePostProcessors) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            Organization org = organizationService.save(organization, requestorId, skipPrePostProcessors);
            response.setResponseValue(org.getId());

        } catch (BasicDataServiceException e) {
        	response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
        } catch (Throwable e) {
            log.error("Can't save organization", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response removeChildOrganization(final String organizationId, final String childOrganizationId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (organizationId == null || childOrganizationId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            organizationService.removeChildOrganization(organizationId, childOrganizationId);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Can't save resource type", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response addChildOrganization(final String organizationId, final String childOrganizationId, final Set<String> rightIds) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (organizationId == null || childOrganizationId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            organizationService.validateOrg2OrgAddition(organizationId, childOrganizationId, rightIds);
            organizationService.addChildOrganization(organizationId, childOrganizationId, rightIds);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Can't save resource type", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }
    
	@Override
	public Response addRoleToOrganization(final String organizationId, final String roleId, final Set<String> rightIds) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (organizationId == null || roleId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            organizationService.addRoleToOrganization(organizationId, roleId, rightIds);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Can't perform operation", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
	}

	@Override
	public Response removeRoleFromOrganization(final String organizationId, final String roleId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (organizationId == null || roleId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            organizationService.removeRoleFromOrganization(organizationId, roleId);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Can't perform operation", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
	}
    
    @Override
	public Response addGroupToOrganization(final String organizationId, final String groupId, final Set<String> rightIds) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (organizationId == null || groupId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            organizationService.addGroupToOrganization(organizationId, groupId, rightIds);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Can't perform operation", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
	}

	@Override
	public Response removeGroupFromOrganization(final String organizationId, final String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (organizationId == null || groupId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            organizationService.removeGroupFromOrganization(organizationId, groupId);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Can't perform operation", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
	}

    @Override
    public Response deleteOrganization(final String orgId) {
        return deleteOrganizationWithSkipPrePostProcessors(orgId, false);
    }

    @Override
    public Response deleteOrganizationWithSkipPrePostProcessors(final String orgId, final boolean skipPrePostProcessors) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            organizationService.deleteOrganization(orgId, skipPrePostProcessors);

        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());

        } catch (Throwable e) {
            log.error("Can't save resource type", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }
    
    private void validateDeleteInternal(final String id) throws BasicDataServiceException {
    	if (id == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        }
    }

    @Override
	public Response validateEdit(Organization organization) {
    	 final Response response = new Response(ResponseStatus.SUCCESS);
         try {
        	 organizationService.validate(organization);

         } catch (BasicDataServiceException e) {
        	response.setStatus(ResponseStatus.FAILURE);
 			response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());

         } catch (Throwable e) {
             log.error("Exception", e);
             response.setStatus(ResponseStatus.FAILURE);
             response.setErrorText(e.getMessage());
         }
         return response;
	}

	@Override
	public Response validateDelete(String id) {
		 final Response response = new Response(ResponseStatus.SUCCESS);
         try {
        	 validateDeleteInternal(id);
         } catch (BasicDataServiceException e) {
             response.setStatus(ResponseStatus.FAILURE);
             response.setErrorCode(e.getCode());
         } catch (Throwable e) {
             log.error("Exception", e);
             response.setStatus(ResponseStatus.FAILURE);
             response.setErrorText(e.getMessage());
         }
         return response;
	}

	@Override
	public Response canAddUserToOrganization(final String organizationId, final String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (organizationId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}


			if (userDataService.isHasOrganization(userId, organizationId)) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}

		} catch (BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch (Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}
	
	@Override
	public Response canRemoveUserToOrganization(final String organizationId, final String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (organizationId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}

			if (!userDataService.isHasOrganization(userId, organizationId)) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}


		} catch (BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch (Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}


    @Override
    public Response addLocation(Location val) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (val == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }


            final LocationEntity entity = locationDozerConverter.convertToEntity(val, true);

            organizationService.addLocation(entity);
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            log.error("Can't perform operation", e);
            response.setErrorText(e.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    @Override
    public Response updateLocation(Location location) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (location == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final LocationEntity entity = locationDozerConverter.convertToEntity(location, false);
            OrganizationEntity org = new OrganizationEntity();
            org.setId(location.getOrganizationId());
            entity.setOrganization(org);
            organizationService.updateLocation(entity);
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            log.error("Can't perform operation", e);
            response.setErrorText(e.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    @Override
    public Response removeLocation(String locationId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (locationId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            organizationService.removeLocation(locationId);
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            log.error("Can't perform operation", e);
            response.setErrorText(e.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Location getLocationById(String locationId) {

        final LocationEntity loc = organizationService.getLocationById(locationId);
        return locationDozerConverter.convertToDTO(loc, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> getLocationList(String organizationId) {

        return this.getLocationListByPage(organizationId, 0, Integer.MAX_VALUE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> getLocationListByPage(String organizationId, Integer from, Integer size) {

        final List<LocationEntity> adrList = organizationService.getLocationList(organizationId, from, size);
        return locationDozerConverter.convertToDTOList(adrList, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> findLocationBeans(final LocationSearchBean searchBean, final int from, final int size) {
        final List<LocationEntity> locList = organizationService.getLocationList(searchBean, from, size);
        return locationDozerConverter.convertToDTOList(locList, false);
    }

    @Override
    public int getNumOfLocations(LocationSearchBean searchBean) {
        return organizationService.getNumOfLocations(searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfLocationsForOrganization(String organizationId) {
        return organizationService.getNumOfLocationsForOrganization(organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfLocationsForUser(String userId) {
            return organizationService.getNumOfLocationsForUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> getLocationListByPageForUser(String userId, Integer from, Integer size) {

        Set<String> orgsId = new HashSet<String>();
        List<OrganizationEntity> orgList = organizationService.getOrganizationsForUser(userId, null, from, size, languageConverter.convertToEntity(getDefaultLanguage(), false));
        for (OrganizationEntity org : orgList) {
            orgsId.add(org.getId());
        }

        if (orgsId == null) {
            return null;
        }
        List<LocationEntity> listOrgEntity = organizationService.getLocationListByOrganizationId(orgsId, from, size);
        if (listOrgEntity == null) {
            return null;
        }

        List<Location> result = new ArrayList<Location>();
        for (LocationEntity org : listOrgEntity) {
            result.add(locationDozerConverter.convertToDTO(org, false));
        }

        return result;
    }
}
