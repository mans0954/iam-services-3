package org.openiam.idm.srvc.org.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.dozer.converter.OrganizationAttributeDozerConverter;
import org.openiam.dozer.converter.OrganizationDozerConverter;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.idm.srvc.searchbean.converter.OrganizationSearchBeanConverter;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private OrganizationService organizationService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private OrganizationSearchBeanConverter organizationSearchBeanConverter;

    @Autowired
    private OrganizationDozerConverter organizationDozerConverter;

    @Autowired
    private OrganizationAttributeDozerConverter organizationAttributeDozerConverter;

    @Override
    public Organization getOrganization(final String orgId, String requesterId) {
        final OrganizationEntity entity = organizationService.getOrganization(orgId, requesterId);
        return organizationDozerConverter.convertToDTO(entity, true);
    }
    
    @Override
	public int getNumOfOrganizationsForUser(final String userId, final String requesterId) {
    	return organizationService.getNumOfOrganizationsForUser(userId, requesterId);
	}
    
    @Override
	public List<Organization> getOrganizationsForUser(final String userId, final String requesterId, final int from, final int size) {
    	final List<OrganizationEntity> ogranizationEntity = organizationService.getOrganizationsForUser(userId, requesterId, from, size);
        return organizationDozerConverter.convertToDTOList(ogranizationEntity, false);
	}

    @Override
    public List<Organization> getAllOrganizations(String requesterId) {
        final List<OrganizationEntity> entityList = organizationService.getAllOrganizations(requesterId);
        return organizationDozerConverter.convertToDTOList(entityList, false);
    }

    @Override
    public List<Organization> findBeans(final OrganizationSearchBean searchBean, String requesterId, final int from, final int size) {
        final List<OrganizationEntity> entityList = organizationService.findBeans(searchBean, requesterId, from, size);
        final List<Organization> resultList = organizationDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy());
        return resultList;
    }

    @Override
    public List<Organization> getParentOrganizations(String orgId, String requesterId, final int from, final int size) {
        final List<OrganizationEntity> entityList = organizationService.getParentOrganizations(orgId, requesterId, from, size);
        final List<Organization> organizationList = organizationDozerConverter.convertToDTOList(entityList, false);
        return organizationList;
    }

    @Override
    public List<Organization> getChildOrganizations(String orgId, String requesterId, final int from, final int size) {
        final List<OrganizationEntity> entityList = organizationService.getChildOrganizations(orgId, requesterId, from, size);
        final List<Organization> organizationList = organizationDozerConverter.convertToDTOList(entityList, false);
        return organizationList;
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

    @Override
    public Response removeAttribute(final String attributeId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (attributeId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            organizationService.removeAttribute(attributeId);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Can't delete occupation attribute", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response saveOrganization(final Organization organization) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (organization == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            if (StringUtils.isBlank(organization.getName())) {
                throw new BasicDataServiceException(ResponseCode.ORGANIZATION_NAME_NOT_SET);
            }

            final OrganizationEntity found = organizationService.getOrganizationByName(organization.getName(), null);
            if (found != null) {
                if (StringUtils.isBlank(organization.getId()) && found != null) {
                    throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
                }

                if (StringUtils.isNotBlank(organization.getId()) && !organization.getId().equals(found.getId())) {
                    throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
                }
            }

            // if(StringUtils.isBlank(organization.getMetadataTypeId())) {
            // throw new
            // BasicDataServiceException(ResponseCode.ORGANIZATION_TYPE_NOT_SET);
            // }
            if(StringUtils.isBlank(organization.getOrganizationTypeId())) {
                throw new BasicDataServiceException(ResponseCode.CLASSIFICATION_NOT_SET);
            }

            final OrganizationEntity entity = organizationDozerConverter.convertToEntity(organization, true);
            organizationService.save(entity);
            response.setResponseValue(entity.getId());
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Can't save organization", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response saveAttribute(final OrganizationAttribute attribute) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (attribute == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            if (StringUtils.isBlank(attribute.getOrganizationId())) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }

            final OrganizationAttributeEntity entity = organizationAttributeDozerConverter.convertToEntity(attribute, false);
            if (StringUtils.isBlank(entity.getName())) {
                throw new BasicDataServiceException(ResponseCode.NO_NAME);
            }

            organizationService.save(entity);
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
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (orgId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            organizationService.deleteOrganization(orgId);
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
	public List<Organization> getOrganizationsForUserByType(final String userId, final String requesterId, final String organizationTypeId) {
		final OrganizationSearchBean searchBean = new OrganizationSearchBean();
		searchBean.setUserId(userId);
		searchBean.setOrganizationTypeId(organizationTypeId);
		searchBean.setDeepCopy(false);
		return findBeans(searchBean, requesterId, 0, Integer.MAX_VALUE);
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
}
