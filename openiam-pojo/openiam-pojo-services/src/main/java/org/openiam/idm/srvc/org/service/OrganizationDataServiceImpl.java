package org.openiam.idm.srvc.org.service;

import javax.jws.*;

//import diamelle.common.continfo.*;
//import diamelle.base.prop.*;

import java.util.*;
import java.rmi.*;

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
    private OrganizationDAO orgDao;
    
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private OrganizationAttributeDAO orgAttrDao;
    
    @Autowired
    private UserAffiliationDAO userAffiliationDAO;
    
    @Autowired
    private OrganizationSearchBeanConverter organizationSearchBeanConverter;
    
    @Autowired
    private OrganizationDozerConverter organizationDozerConverter;
    
    @Autowired
    private OrganizationAttributeDozerConverter organizationAttributeDozerConverter;

    @Override
    public List<Organization> subOrganizations(final String orgId) {
        final List<OrganizationEntity> entityList = orgDao.findChildOrganization(orgId);
        final List<Organization> organizationList = organizationDozerConverter.convertToDTOList(entityList, false);
        return organizationList;
    }

    
    @Override
    public List<Organization> getTopLevelOrganizations() {
        final List<OrganizationEntity> entityList = orgDao.findRootOrganizations();
        return organizationDozerConverter.convertToDTOList(entityList, false);
    }


    private List<Organization> getOrganizationByClassification(final String parentId, final OrgClassificationEnum classification) {
        if (classification == null)
            throw new NullPointerException("classification is null");

        final List<OrganizationEntity> entityList = orgDao.findOrganizationByClassification(parentId, classification);
        return organizationDozerConverter.convertToDTOList(entityList, false);
    }

    @Override
    public List<Organization> allDepartments(String parentId) {
        return getOrganizationByClassification(parentId, OrgClassificationEnum.DEPARTMENT);
    }

    @Override
    public List<Organization> allDivisions(String parentId) {
        return getOrganizationByClassification(parentId, OrgClassificationEnum.DIVISION);
    }

    @Override
    public Organization getOrganization(final String orgId) {
    	final OrganizationEntity entity = orgDao.findById(orgId);
    	return organizationDozerConverter.convertToDTO(entity, false);
    }

    @Override
    public List<Organization> getOrganizationsForUser(String userId) {
        final List<OrganizationEntity> ogranizationEntity = userAffiliationDAO.findOrgAffiliationsByUser(userId);
        return organizationDozerConverter.convertToDTOList(ogranizationEntity, false);
    }

    @Override
    public Response addUserToOrg(final String orgId, final String userId) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(orgId != null && userId != null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final OrganizationEntity organization = orgDao.findById(orgId);
			final UserEntity user = userDAO.findById(userId);
			
			final UserAffiliationEntity entity = new UserAffiliationEntity();
			entity.setOrganization(organization);
			entity.setUser(user);
			
			userAffiliationDAO.save(entity);
			
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
			
			final UserAffiliationEntity entity = userAffiliationDAO.getRecord(userId, orgId);
			if(entity == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			userAffiliationDAO.delete(entity);
			
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
    	final List<OrganizationEntity> entityList = orgDao.findAllOrganization();
        return organizationDozerConverter.convertToDTOList(entityList, false);
    }



    @Override
    public List<Organization> findBeans(final OrganizationSearchBean searchBean, final int from, final int size) {
        final List<OrganizationEntity> entityList = orgDao.getByExample(organizationSearchBeanConverter.convert(searchBean), from, size);
        final List<Organization> resultList = organizationDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy());
        return resultList;
    }

    @Override
    public int count(final OrganizationSearchBean searchBean) {
        return orgDao.count(organizationSearchBeanConverter.convert(searchBean));
    }


	@Override
	public Response removeAttribute(final String attributeId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(attributeId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final OrganizationAttributeEntity entity = orgAttrDao.findById(attributeId);
			if(entity == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			orgAttrDao.delete(entity);
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
			if(StringUtils.isNotBlank(entity.getOrgId())) {
				final OrganizationEntity dbOrg = orgDao.findById(entity.getOrgId());
				if(dbOrg == null) {
					throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
				}
				dbOrg.setAbbreviation(entity.getAbbreviation());
				dbOrg.setAlias(entity.getAlias());
				dbOrg.setClassification(entity.getClassification());
				dbOrg.setDescription(entity.getDescription());
				dbOrg.setDomainName(entity.getDomainName());
				dbOrg.setInternalOrgId(entity.getInternalOrgId());
				dbOrg.setLdapStr(entity.getLdapStr());
				dbOrg.setMetadataTypeId(entity.getMetadataTypeId());
				dbOrg.setOrganizationName(entity.getOrganizationName());
				dbOrg.setParentId(entity.getParentId());
				dbOrg.setStatus(entity.getStatus());
				dbOrg.setSymbol(entity.getSymbol());
				entity = dbOrg;
			}
			
			if(StringUtils.isNotBlank(entity.getOrgId())) {
				orgDao.update(entity);
			} else {
				orgDao.save(entity);
			}
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
			
			if(orgDao.findById(attribute.getOrganizationId()) == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final OrganizationAttributeEntity entity = organizationAttributeDozerConverter.convertToEntity(attribute, false);
			if(StringUtils.isBlank(entity.getName())) {
				throw new BasicDataServiceException(ResponseCode.NO_NAME);
			}
			
			orgAttrDao.save(entity);
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
	public Response deleteOrganization(final String orgId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(orgId != null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final OrganizationEntity entity = orgDao.findById(orgId);
			if(entity == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			orgDao.delete(entity);
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
