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
import org.openiam.dozer.converter.OrganizationUserDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AddressSearchBean;
import org.openiam.idm.searchbeans.LocationSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.loc.domain.LocationEntity;
import org.openiam.idm.srvc.loc.dto.Location;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationUserEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationUserDTO;
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
    private
    OrganizationUserDozerConverter organizationUserDozerConverter;

    @Override
    /**
     * Only for internal system use, without @LocalizedServiceGet
     */
    public Organization getOrganization(final String orgId, String requesterId) {
        return this.getOrganizationLocalized(orgId, requesterId, getDefaultLanguage());
    }

    @Override
    //@LocalizedServiceGet
    //@Transactional(readOnly = true)
    public Organization getOrganizationLocalized(final String orgId, String requesterId, final Language language) {
        return organizationService.getOrganizationLocalizedDto(orgId, requesterId, languageConverter.convertToEntity(language, false));
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
    //@LocalizedServiceGet
    public List<Organization> getOrganizationsForUserLocalized(final String userId, final String requesterId, final int from, final int size, final Language language) {
        return organizationService.getOrganizationsDtoForUser(userId, requesterId, from, size, languageConverter.convertToEntity(language, false));
    }

    @Override
    @Deprecated
    public List<Organization> findBeans(final OrganizationSearchBean searchBean, final String requesterId, final int from, final int size) {
        return this.findBeansLocalized(searchBean, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    //@LocalizedServiceGet
    public List<Organization> findBeansLocalized(final OrganizationSearchBean searchBean, final String requesterId, final int from, final int size, final Language language) {
        /*final List<OrganizationEntity> entityList = organizationService.findBeans(searchBean, requesterId, from, size, languageConverter.convertToEntity(language, false));
        final List<Organization> resultList = new LinkedList<Organization>();
        for (OrganizationEntity organizationEntity : entityList) {
            Organization newOrg = organizationDozerConverter.convertToDTO(organizationEntity, false);
            newOrg.setOrganizationUserDTOs(new HashSet<OrganizationUserDTO>());
            for (OrganizationUserEntity e : organizationEntity.getOrganizationUser()) {
                OrganizationUserDTO dto = new OrganizationUserDTO(e.getUser().getId(), e.getOrganization().getId(), e.getMetadataTypeEntity().getId(), null);
                newOrg.getOrganizationUserDTOs().add(dto);
            }
            resultList.add(newOrg);
        }*/

        return organizationService.findBeansDto(searchBean, requesterId, from, size, languageConverter.convertToEntity(language, false));
    }

    @Override
    @Deprecated
    public List<Organization> getParentOrganizations(String orgId, String requesterId, final int from, final int size) {
        return this.getParentOrganizationsLocalized(orgId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    //@LocalizedServiceGet
    public List<Organization> getParentOrganizationsLocalized(String orgId, String requesterId, final int from, final int size, final Language language) {
        return organizationService.getParentOrganizationsDto(orgId, requesterId, from, size, languageConverter.convertToEntity(language, false));
    }

    @Override
    @Deprecated
    public List<Organization> getChildOrganizations(String orgId, String requesterId, final int from, final int size) {
        return this.getChildOrganizationsLocalized(orgId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    //@LocalizedServiceGet
    public List<Organization> getChildOrganizationsLocalized(String orgId, String requesterId, final int from, final int size, final Language language) {
        return organizationService.getChildOrganizationsDto(orgId, requesterId, from, size, languageConverter.convertToEntity(language, false));
    }

    @Override
    public int getNumOfParentOrganizations(String orgId, String requesterId) {
        return organizationService.getNumOfParentOrganizations(orgId, requesterId);
    }

    @Override
    public int getNumOfChildOrganizations(String orgId, String requesterId) {
        return organizationService.getNumOfChildOrganizations(orgId, requesterId);
    }

    @Override
    public int count(final OrganizationSearchBean searchBean, String requesterId) {
        return organizationService.count(searchBean, requesterId);
    }

    @Override
    @Deprecated
    public List<Organization> getAllowedParentOrganizationsForType(final String orgTypeId, String requesterId) {
        return this.getAllowedParentOrganizationsForTypeLocalized(orgTypeId, requesterId, getDefaultLanguage());
    }

    @Override
    //@LocalizedServiceGet
    public List<Organization> getAllowedParentOrganizationsForTypeLocalized(final String orgTypeId, String requesterId, final Language language) {
        return organizationService.getAllowedParentOrganizationsDtoForType(orgTypeId, requesterId, languageConverter.convertToEntity(language, false));
    }

    @Override
    @Deprecated
    public List<Organization> findOrganizationsByAttributeValue(String attrName, String attrValue) {
        return this.findOrganizationsByAttributeValueLocalized(attrName, attrValue, getDefaultLanguage());
    }

    @Override
    //@LocalizedServiceGet
    //@Transactional(readOnly = true)
    public List<Organization> findOrganizationsByAttributeValueLocalized(String attrName, String attrValue, final Language language) {
        return organizationService.findOrganizationsDtoByAttributeValue(attrName, attrValue, languageConverter.convertToEntity(language, false));
    }


    @Override
    @Deprecated
    public List<Organization> getOrganizationsForUserByType(final String userId, final String requesterId, final String organizationTypeId) {
        return this.getOrganizationsForUserByTypeLocalized(userId, requesterId, organizationTypeId, getDefaultLanguage());
    }

    @Override
    //@LocalizedServiceGet
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

    private Language getDefaultLanguage() {
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
    public Response addChildOrganization(final String organizationId, final String childOrganizationId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (organizationId == null || childOrganizationId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            organizationService.validateOrg2OrgAddition(organizationId, childOrganizationId);
            organizationService.addChildOrganization(organizationId, childOrganizationId);
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
    //@Transactional(readOnly = true)
    public Location getLocationById(String locationId) {
        return organizationService.getLocationDtoById(locationId);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<Location> getLocationList(String organizationId) {

        return this.getLocationListByPage(organizationId, 0, Integer.MAX_VALUE);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<Location> getLocationListByPage(String organizationId, Integer from, Integer size) {
        return organizationService.getLocationDtoList(organizationId, from, size);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<Location> findLocationBeans(final LocationSearchBean searchBean, final int from, final int size) {
        return organizationService.getLocationDtoList(searchBean, from, size);
    }

    @Override
    public int getNumOfLocations(LocationSearchBean searchBean) {
        return organizationService.getNumOfLocations(searchBean);
    }

    @Override
    //@Transactional(readOnly = true)
    public int getNumOfLocationsForOrganization(String organizationId) {
        return organizationService.getNumOfLocationsForOrganization(organizationId);
    }

    @Override
    //@Transactional(readOnly = true)
    public int getNumOfLocationsForUser(String userId) {
        return organizationService.getNumOfLocationsForUser(userId);
    }

    @Override
    public List<Location> getLocationListByPageForUser(String userId, Integer from, Integer size) {

        return organizationService.getLocationListByPageForUser(userId, from, size);
    }

    @Override
    public List<Organization> getUserAffiliationsByType(final String userId, final String typeId, int from, int size, final String requesterId, final Language language) {
        return organizationService.getUserAffiliationsByType(userId, typeId, requesterId, from, size, languageConverter.convertToEntity(language, false));
    }

}
