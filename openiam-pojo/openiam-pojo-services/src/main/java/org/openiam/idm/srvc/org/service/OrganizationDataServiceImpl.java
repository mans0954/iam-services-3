package org.openiam.idm.srvc.org.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.dozer.converter.OrganizationAttributeDozerConverter;
import org.openiam.dozer.converter.OrganizationDozerConverter;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.OrgClassificationEnum;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.idm.srvc.searchbean.converter.OrganizationSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import diamelle.common.continfo.*;
//import diamelle.base.prop.*;

/**
 * <code>OrganizationManager</code> provides a service level interface to the
 * Organization components and its dependant objects as well as search
 * capability.<br>
 * <p/>
 * Note: The spring configuration file defines MetadataTypes are used to identify Departments and Divisions in the org list.
 *
 * @author OpenIAm
 * @version 2
 */

@WebService(endpointInterface = "org.openiam.idm.srvc.org.service.OrganizationDataService",
        targetNamespace = "urn:idm.openiam.org/srvc/org/service",
        portName = "OrganizationDataWebServicePort",
        serviceName = "OrganizationDataWebService")
@Service("orgManager")
@Transactional
public class OrganizationDataServiceImpl implements OrganizationDataService {
	
	private static final Log log = LogFactory.getLog(OrganizationDataServiceImpl.class);
	
	@Autowired
	private OrganizationService organizationService;
	
    @Autowired
    private OrganizationSearchBeanConverter organizationSearchBeanConverter;
    
    @Autowired
    private OrganizationDozerConverter organizationDozerConverter;
    
    @Autowired
    private OrganizationAttributeDozerConverter organizationAttributeDozerConverter;

    @Override
    public List<Organization> getTopLevelOrganizations( String requesterId) {
        final List<OrganizationEntity> entityList = organizationService.getTopLevelOrganizations(requesterId);
        return organizationDozerConverter.convertToDTOList(entityList, false);
    }

    @Override
    public Organization getOrganization(final String orgId, String requesterId) {
    	final OrganizationEntity entity = organizationService.getOrganization(orgId, requesterId);
    	return organizationDozerConverter.convertToDTO(entity, false);
    }

    @Override
    public List<Organization> getOrganizationsForUser(String userId,  String requesterId) {
        final List<OrganizationEntity> ogranizationEntity = organizationService.getOrganizationsForUser(userId, requesterId);
        return organizationDozerConverter.convertToDTOList(ogranizationEntity, false);
    }

    @Override
    public List<Organization> getAllOrganizations( String requesterId) {
        final List<OrganizationEntity> entityList = organizationService.getAllOrganizations(requesterId);
        return organizationDozerConverter.convertToDTOList(entityList, false);
    }



    @Override
    public List<Organization> findBeans(final OrganizationSearchBean searchBean, String requesterId,  final int from, final int size) {
        final List<OrganizationEntity> entityList = organizationService.findBeans(searchBean,requesterId, from, size);
        final List<Organization> resultList = organizationDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy());
        return resultList;
    }

    @Override
    public List<Organization> getParentOrganizations(String orgId, String parentClassification, String requesterId, final int from, final int size) {
        final List<OrganizationEntity> entityList = organizationService.getParentOrganizations(orgId, parentClassification, requesterId, from, size);
        final List<Organization> organizationList = organizationDozerConverter.convertToDTOList(entityList, false);
        return organizationList;
    }

    @Override
    public List<Organization> getChildOrganizations(String orgId, String childClassification, String requesterId, final int from, final int size) {
        final List<OrganizationEntity> entityList = organizationService.getChildOrganizations(orgId, childClassification, requesterId, from, size);
        final List<Organization> organizationList = organizationDozerConverter.convertToDTOList(entityList, false);
        return organizationList;
    }

    @Override
    public int getNumOfParentOrganizations(String orgId, String parentClassification, String requesterId) {
        return organizationService.getNumOfParentOrganizations(orgId, parentClassification, requesterId);
    }

    @Override
    public int getNumOfChildOrganizations(String orgId, String childClassification, String requesterId) {
        return organizationService.getNumOfChildOrganizations(orgId, childClassification, requesterId);
    }

    @Override
    public int count(final OrganizationSearchBean searchBean, String requesterId) {
        return organizationService.count(searchBean, requesterId);
    }

    @Override
    public Response addUserToOrg(final String orgId, final String userId) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(orgId != null && userId != null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			if(organizationService.getAffiliation(userId, orgId) != null) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}
			
			organizationService.addUserToOrg(orgId, userId);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
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
			if(orgId != null && userId != null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			organizationService.removeUserFromOrg(orgId, userId);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
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
			if(attributeId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			organizationService.removeAttribute(attributeId);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
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
			if(organization == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
            if(StringUtils.isBlank(organization.getOrganizationName())) {
                throw new BasicDataServiceException(ResponseCode.ORGANIZATION_NAME_NOT_SET);
            }

            final OrganizationEntity found = organizationService.getOrganizationByName(organization.getOrganizationName(), null);
            if(found != null) {
                if(StringUtils.isBlank(organization.getOrgId()) && found != null) {
                    throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
                }

                if(StringUtils.isNotBlank(organization.getOrgId()) && !organization.getOrgId().equals(found.getOrgId())) {
                    throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
                }
            }

            if(StringUtils.isBlank(organization.getMetadataTypeId())) {
                throw new BasicDataServiceException(ResponseCode.ORGANIZATION_TYPE_NOT_SET);
            }
			if(organization.getClassification() == null) {
				organization.setClassification(OrgClassificationEnum.fromStringValue(organization.getClassificaitonAsString()));
			}
            if(organization.getClassification() == null) {
                throw new BasicDataServiceException(ResponseCode.CLASSIFICATION_NOT_SET);
            }
			
			OrganizationEntity entity = organizationDozerConverter.convertToEntity(organization, false);
			organizationService.save(entity);
			response.setResponseValue(entity.getOrgId());
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Can't save resource type", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
    	return response;
	}


	@Override
	public Response saveAttribute(final OrganizationAttribute attribute) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(attribute == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			if(StringUtils.isBlank(attribute.getOrganizationId())) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final OrganizationAttributeEntity entity = organizationAttributeDozerConverter.convertToEntity(attribute, false);
			if(StringUtils.isBlank(entity.getName())) {
				throw new BasicDataServiceException(ResponseCode.NO_NAME);
			}
			
			organizationService.save(entity);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
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
			if(organizationId == null || childOrganizationId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			organizationService.removeChildOrganization(organizationId, childOrganizationId);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
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
			if(organizationId == null || childOrganizationId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final OrganizationEntity parent = organizationService.getOrganization(organizationId, null);
			final OrganizationEntity child = organizationService.getOrganization(childOrganizationId, null);
			
			if(parent == null || child == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			if(parent.hasChildOrganization(childOrganizationId)) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}
			
			if(causesCircularDependency(parent, child, new HashSet<OrganizationEntity>())) {
				throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
			}
			
			if(organizationId.equals(childOrganizationId)) {
				throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
			}
			
			organizationService.addChildOrganization(organizationId, childOrganizationId);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Can't save resource type", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
    	return response;
	}
	
	private boolean causesCircularDependency(final OrganizationEntity parent, final OrganizationEntity child, final Set<OrganizationEntity> visitedSet) {
		boolean retval = false;
		if(parent != null && child != null) {
			if(!visitedSet.contains(child)) {
				visitedSet.add(child);
				if(CollectionUtils.isNotEmpty(parent.getParentOrganizations())) {
					for(final OrganizationEntity entity : parent.getParentOrganizations()) {
						retval = entity.getOrgId().equals(child.getOrgId());
						if(retval) {
							break;
						}
						causesCircularDependency(parent, entity, visitedSet);
					}
				}
			}
		}
		return retval;
	}

	@Override
	public Response deleteOrganization(final String orgId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(orgId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			organizationService.deleteOrganization(orgId);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Can't save resource type", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
    	return response;
	}
}
