package org.openiam.idm.srvc.msg.ws;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.MailTemplateDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.msg.domain.MailTemplateEntity;
import org.openiam.idm.srvc.msg.dto.MailTemplateDto;
import org.openiam.idm.srvc.msg.dto.MailTemplateSearchBean;
import org.openiam.idm.srvc.msg.service.MailTemplateService;
import org.openiam.idm.srvc.msg.service.MailTemplateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(endpointInterface = "org.openiam.idm.srvc.msg.ws.MailTemplateWebService",
        targetNamespace = "urn:idm.openiam.org/srvc/msg/service",
        portName = "MailTemplatePort",
        serviceName = "MailTemplateWebService")
@Service("mailTemplateWebService")
public class MailTemplateWebServiceImpl implements MailTemplateWebService {
	
	private static final Log log = LogFactory.getLog(MailTemplateServiceImpl.class);

	@Autowired
    private MailTemplateService tmplService;
    
    @Autowired
    private MailTemplateDozerConverter mailTemplateDozerConverter;

    @Override
	public Response save(MailTemplateDto dto) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        try {
        	if(dto == null) {
        		throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        	}
        	
        	if(StringUtils.isBlank(dto.getName())) {
        		throw new BasicDataServiceException(ResponseCode.NAME_MISSING);
        	}
        	
            final MailTemplateEntity entity = mailTemplateDozerConverter.convertToEntity(dto, true);
            tmplService.save(entity);
            response.setResponseValue(entity.getId());
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
        } catch (Throwable e) {
            log.error("Can't validate resource", e);
            response.setErrorText(e.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
	}

	@Override
	public Response removeTemplate(String id) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        try {
        	if(id == null) {
        		throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        	}
        	
        	tmplService.delete(id);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
        } catch (Throwable e) {
            log.error("Can't validate resource", e);
            response.setErrorText(e.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
	}

	@Override
	public MailTemplateDto getTemplateById(String id) {
		final MailTemplateEntity entity = tmplService.get(id);
		return mailTemplateDozerConverter.convertToDTO(entity, true);
	}

	@Override
	public List<MailTemplateDto> findBeans(MailTemplateSearchBean searchBean,
			int from, int size) {
		final List<MailTemplateEntity> entities = tmplService.findBeans(searchBean, from, size);
		final List<MailTemplateDto> dtos = mailTemplateDozerConverter.convertToDTOList(entities, (searchBean != null) ? searchBean.isDeepCopy() : false);
		return dtos;
	}
}
