package org.openiam.srvc.am;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.OrganizationTypeResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.OrganizationDozerConverter;
import org.openiam.dozer.converter.OrganizationTypeDozerBeanConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.idm.srvc.org.dto.OrganizationType;
import org.openiam.idm.srvc.org.service.OrganizationTypeService;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.constants.OrganizationTypeAPI;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;

import java.util.List;

@Service("organizationTypeDataService")
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/org/service", name = "OrganizationTypeDataService")
public class OrganizationTypeDataServiceImpl extends AbstractApiService implements OrganizationTypeDataService {
	
	private static final Log LOG = LogFactory.getLog(OrganizationTypeDataServiceImpl.class);

	@Autowired
	private OrganizationTypeService organizationTypeService;
	
	@Autowired
	private OrganizationTypeDozerBeanConverter dozerConverter;
	
	@Autowired
	private OrganizationDozerConverter organizationDozerConverter;

	public OrganizationTypeDataServiceImpl() {
		super(OpenIAMQueue.OrganizationTypeQueue);
	}

	@Override
//	@LocalizedServiceGet
//    @Transactional(readOnly = true)
	public OrganizationType findById(final String id, final Language language) {
		IdServiceRequest request = new IdServiceRequest(id);
		request.setLanguage(language);
		return this.getValue(OrganizationTypeAPI.GetById, request, OrganizationTypeResponse.class);

//		final OrganizationTypeEntity entity = organizationTypeService.findById(id);
//		return (entity != null) ? dozerConverter.convertToDTO(entity, true) : null;
	}
	
	@Override
	@LocalizedServiceGet
	public List<OrganizationType> findAllowedChildrenByDelegationFilter(final String requesterId, final Language language) {
		final List<OrganizationTypeEntity> entityList =  organizationTypeService.findAllowedChildrenByDelegationFilter(requesterId);
		return dozerConverter.convertToDTOList(entityList, false);
	}

	@Override
	@LocalizedServiceGet
    @Transactional(readOnly = true)
	public List<OrganizationType> findBeans(final OrganizationTypeSearchBean searchBean, final int from, final int size, final Language language) {
		final List<OrganizationTypeEntity> entityList = organizationTypeService.findBeans(searchBean, from, size);
		return dozerConverter.convertToDTOList(entityList, (searchBean != null) ? searchBean.isDeepCopy() : false);
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
			
			organizationTypeService.validateOrgType2OrgTypeAddition(id, childId);
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

    @Override
    @LocalizedServiceGet
    public List<OrganizationType> getAllowedParents(final String organizationTypeId, final String requesterId, final Language language){
        final List<OrganizationTypeEntity> entityList = organizationTypeService.getAllowedParents(organizationTypeId, requesterId);
        return dozerConverter.convertToDTOList(entityList, false);
    }
}
