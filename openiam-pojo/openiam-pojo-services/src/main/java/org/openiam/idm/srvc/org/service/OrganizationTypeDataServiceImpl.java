package org.openiam.idm.srvc.org.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.dozer.converter.OrganizationDozerConverter;
import org.openiam.dozer.converter.OrganizationTypeDozerBeanConverter;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationType;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.searchbean.converter.OrganizationTypeSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("organizationTypeDataService")
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/org/service", name = "OrganizationTypeDataService")
public class OrganizationTypeDataServiceImpl implements OrganizationTypeDataService {
	
	private Logger LOG = Logger.getLogger(OrganizationTypeDataServiceImpl.class);

	@Autowired
	private OrganizationTypeService organizationTypeService;
	
	@Autowired
	private OrganizationTypeDozerBeanConverter dozerConverter;
	
	@Autowired
	private OrganizationDozerConverter organizationDozerConverter;
	
	@Override
	public OrganizationType findById(final String id) {
		final OrganizationTypeEntity entity = organizationTypeService.findById(id);
		return (entity != null) ? dozerConverter.convertToDTO(entity, true) : null;
	}

	@Override
	public List<OrganizationType> findBeans(final OrganizationTypeSearchBean searchBean, final int from, final int size) {
		final List<OrganizationTypeEntity> entityList = organizationTypeService.findBeans(searchBean, from, size);
		return dozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy());
	}

	@Override
	public int count(final OrganizationTypeSearchBean searchBean) {
		return organizationTypeService.count(searchBean);
	}

	@Override
	public Response save(final OrganizationType type) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(type == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			if(StringUtils.isBlank(type.getName())) {
				throw new BasicDataServiceException(ResponseCode.NO_NAME);
			}
			
			final OrganizationTypeEntity dbEntity = organizationTypeService.findByName(type.getName());
			if(dbEntity != null) {
				if(!StringUtils.equals(dbEntity.getId(), type.getId())) {
					throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
				}
			}
			
			final OrganizationTypeEntity entity = dozerConverter.convertToEntity(type, true);
			organizationTypeService.save(entity);
			response.setResponseValue(entity.getId());
		} catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            LOG.error("Can't save", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
	}

	@Override
	@Transactional
	public Response delete(final String id) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(StringUtils.isBlank(id)) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final OrganizationTypeEntity entity = organizationTypeService.findById(id);
			if(CollectionUtils.isNotEmpty(entity.getChildTypes())) {
				throw new BasicDataServiceException(ResponseCode.ORGANIZATION_TYPE_CHILDREN_EXIST);
			}
			
			if(CollectionUtils.isNotEmpty(entity.getParentTypes())) {
				throw new BasicDataServiceException(ResponseCode.ORGANIZATION_TYPE_PARENTS_EXIST);
			}
			
			if(CollectionUtils.isNotEmpty(entity.getOrganizations())) {
				throw new BasicDataServiceException(ResponseCode.ORGANIZATION_TYPE_TIED_TO_ORGANIZATION);
			}
			
			organizationTypeService.delete(id);
		} catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            LOG.error("Can't save", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
	}

	@Override
	@Transactional
	public Response addChild(final String id, final String childId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(StringUtils.isBlank(id) || StringUtils.isBlank(childId)) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final OrganizationTypeEntity parent = organizationTypeService.findById(id);
			final OrganizationTypeEntity child = organizationTypeService.findById(childId);
			if(parent == null || child == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			if(parent.containsChild(child.getId())) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}
			
			if(causesCircularDependency(parent, child, new HashSet<OrganizationTypeEntity>())) {
				throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
			}
			
			organizationTypeService.addChild(id, childId);
		} catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            LOG.error("Can't save", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
	}
	
	private boolean causesCircularDependency(final OrganizationTypeEntity parent,
											 final OrganizationTypeEntity child, 
											 final Set<OrganizationTypeEntity> visitedSet) {
		boolean retval = false;
		if (parent != null && child != null) {
			if (!visitedSet.contains(child)) {
				visitedSet.add(child);
				if (CollectionUtils.isNotEmpty(parent.getParentTypes())) {
					for (final OrganizationTypeEntity entity : parent.getParentTypes()) {
						retval = StringUtils.equals(entity.getId(), child.getId());//entity.getResourceId().equals(child.getResourceId());
						if (retval) {
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
	public Response removeChild(String id, String childId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(StringUtils.isBlank(id) || StringUtils.isBlank(childId)) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			organizationTypeService.removeChild(id, childId);
		} catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            LOG.error("Can't save", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
	}
}
