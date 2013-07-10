package org.openiam.idm.srvc.org.service;

import java.util.List;

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
	private OrganizationTypeSearchBeanConverter searchBeanConverter;
	
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
		final OrganizationTypeEntity entity = searchBeanConverter.convert(searchBean);
		final List<OrganizationTypeEntity> entityList = organizationTypeService.findBeans(entity, from, size);
		return dozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy());
	}

	@Override
	public int count(final OrganizationTypeSearchBean searchBean) {
		final OrganizationTypeEntity entity = searchBeanConverter.convert(searchBean);
		return organizationTypeService.count(entity);
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
			
			final OrganizationTypeEntity entity = dozerConverter.convertToEntity(type, true);
			organizationTypeService.save(entity);
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
	public List<OrganizationType> getChildren(final String id, final int from, final int size) {
		final List<OrganizationTypeEntity> entityList = organizationTypeService.getChildren(id, from, size);
		return dozerConverter.convertToDTOList(entityList, false);
	}

	@Override
	public List<OrganizationType> getParents(final String id, final int from, final int size) {
		final List<OrganizationTypeEntity> entityList = organizationTypeService.getParents(id, from, size);
		return dozerConverter.convertToDTOList(entityList, false);
	}

	@Override
	public List<Organization> getOrganizations(final String id, final int from, final int size) {
		final List<OrganizationEntity> entityList = organizationTypeService.getOrganizations(id, from, size);
		return organizationDozerConverter.convertToDTOList(entityList, false);
	}

	@Override
	public Response addChild(final String id, final String childId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(StringUtils.isBlank(id) || StringUtils.isBlank(childId)) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			//TODO: validate
			
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

	@Override
	public Response removeChild(String id, String childId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(StringUtils.isBlank(id) || StringUtils.isBlank(childId)) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			//TODO: validate
			
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
