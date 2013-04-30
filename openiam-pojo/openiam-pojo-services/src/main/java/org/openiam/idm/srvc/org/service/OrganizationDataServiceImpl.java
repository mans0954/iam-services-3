package org.openiam.idm.srvc.org.service;

import javax.jws.*;

//import diamelle.common.continfo.*;
//import diamelle.base.prop.*;

import java.util.*;
import java.rmi.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.dozer.converter.OrganizationAttributeDozerConverter;
import org.openiam.dozer.converter.OrganizationDozerConverter;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.UserAffiliationEntity;
import org.openiam.idm.srvc.org.dto.*;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.searchbean.converter.OrganizationSearchBeanConverter;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.util.DozerMappingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Organization> getTopLevelOrganizations() {
        final List<OrganizationEntity> entityList = organizationService.getTopLevelOrganizations();
        return organizationDozerConverter.convertToDTOList(entityList, false);
    }

    @Override
    public Organization getOrganization(final String orgId) {
    	final OrganizationEntity entity = organizationService.getOrganization(orgId);
    	return organizationDozerConverter.convertToDTO(entity, false);
    }

    @Override
    public List<Organization> getOrganizationsForUser(String userId) {
        final List<OrganizationEntity> ogranizationEntity = organizationService.getOrganizationsForUser(userId);
        return organizationDozerConverter.convertToDTOList(ogranizationEntity, false);
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
    public List<Organization> getAllOrganizations() {
    	final List<OrganizationEntity> entityList = organizationService.getAllOrganizations();
        return organizationDozerConverter.convertToDTOList(entityList, false);
    }



    @Override
    public List<Organization> findBeans(final OrganizationSearchBean searchBean, final int from, final int size) {
    	final OrganizationEntity searchEntity = organizationSearchBeanConverter.convert(searchBean);
        final List<OrganizationEntity> entityList = organizationService.findBeans(searchEntity, from, size);
        final List<Organization> resultList = organizationDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy());
        return resultList;
    }

    @Override
    public int count(final OrganizationSearchBean searchBean) {
    	final OrganizationEntity searchEntity = organizationSearchBeanConverter.convert(searchBean);
    	return organizationService.count(searchEntity);
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
			
			if(organization.getClassification() == null) {
				organization.setClassification(OrgClassificationEnum.fromStringValue(organization.getClassificaitonAsString()));
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
			
			final OrganizationEntity parent = organizationService.getOrganization(organizationId);
			final OrganizationEntity child = organizationService.getOrganization(childOrganizationId);
			
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

	@Override
	public List<Organization> getParentOrganizations(final String organizationId, final int from, final int size) {
		final List<OrganizationEntity> entityList = organizationService.getParentOrganizations(organizationId, from, size);
		final List<Organization> organizationList = organizationDozerConverter.convertToDTOList(entityList, false);
		return organizationList;
	}

	@Override
	public List<Organization> getChildOrganizations(final String organizationId, final int from, final int size) {
		final List<OrganizationEntity> entityList = organizationService.getChildOrganizations(organizationId, from, size);
		final List<Organization> organizationList = organizationDozerConverter.convertToDTOList(entityList, false);
		return organizationList;
	}

	@Override
	public int getNumOfParentOrganizations(final String organizationId) {
		return organizationService.getNumOfParentOrganizations(organizationId);
	}

	@Override
	public int getNumOfChildOrganizations(final String organizationId) {
		return organizationService.getNumOfChildOrganizations(organizationId);
	}
}
