package org.openiam.idm.srvc.access.ws;

import java.util.Collection;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.AccessRightDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AccessRightSearchBean;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.access.service.AccessRightService;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.internationalization.LocalizedServiceGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("accessRightWS")
@WebService(endpointInterface = "org.openiam.idm.srvc.access.ws.AccessRightDataService", targetNamespace = "urn:idm.openiam.org/srvc/access/service", portName = "AccessRightDataServicePort", serviceName = "AccessRightDataService")
public class AccessRightDataServiceImpl extends AbstractBaseService implements AccessRightDataService {

	private static final Log log = LogFactory.getLog(AccessRightDataServiceImpl.class);
	
	@Autowired
	private AccessRightService service;
	
	@Autowired
	private AccessRightDozerConverter converter;
	
	@Override
	public Response save(final AccessRight dto) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        try {
        	if(dto == null) {
        		throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        	}
        	
        	final AccessRightEntity entity = converter.convertToEntity(dto, true);
        	service.save(entity);
            response.setResponseValue(entity.getId());
        } catch (BasicDataServiceException e) {
            response.fail();
            response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
        } catch (Throwable e) {
            log.error("Can't save or update object", e);
            response.setErrorText(e.getMessage());
            response.fail();
        }
        return response;
	}

	@Override
	public Response delete(String id) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        try {
        	if(StringUtils.isBlank(id)) {
        		throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        	}
        	
        	service.delete(id);
        } catch (BasicDataServiceException e) {
            response.fail();
            response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
        } catch (Throwable e) {
            log.error("Can't save or delete object", e);
            response.setErrorText(e.getMessage());
            response.fail();
        }
        return response;
	}

	@Override
	public AccessRight get(String id) {
		final AccessRightEntity entity = service.get(id);
		final AccessRight dto = converter.convertToDTO(entity, true);
		return dto;
	}

	@Override
	@LocalizedServiceGet
	public List<AccessRight> findBeans(final AccessRightSearchBean searchBean, final int from, final int size, final Language language) {
		final List<AccessRightEntity> entities = service.findBeans(searchBean, from, size);
		final List<AccessRight> dtos = converter.convertToDTOList(entities, true);
		return dtos;
	}

	@Override
	public int count(AccessRightSearchBean searchBean) {
		return service.count(searchBean);
	}

	@Override
	public List<AccessRight> getByIds(final Collection<String> ids) {
		final List<AccessRightEntity> entities = service.findByIds(ids);
		final List<AccessRight> dtos = converter.convertToDTOList(entities, true);
		return dtos;
	}

}
