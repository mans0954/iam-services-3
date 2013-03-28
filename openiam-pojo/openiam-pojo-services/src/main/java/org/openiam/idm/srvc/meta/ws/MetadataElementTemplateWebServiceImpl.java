package org.openiam.idm.srvc.meta.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.dozer.converter.MetadataElementTemplateDozerConverter;
import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;
import org.openiam.idm.srvc.meta.service.MetadataElementTemplateService;
import org.openiam.idm.srvc.searchbean.converter.MetadataElementTemplateSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("metadataElementTemplateWebService")
@WebService(endpointInterface = "org.openiam.idm.srvc.meta.ws.MetadataElementTemplateWebService", 
			targetNamespace = "urn:idm.openiam.org/srvc/meta/ws", 
			portName = "MetadataElementTemplateWebServicePort", 
			serviceName = "MetadataElementTemplateWebService")
public class MetadataElementTemplateWebServiceImpl implements MetadataElementTemplateWebService {

	@Autowired
	private MetadataElementTemplateService templateService;
	
	@Autowired
	private MetadataElementTemplateDozerConverter templateDozerConverter;
	
	@Autowired
	private MetadataElementTemplateSearchBeanConverter templateSearchBeanConverter;
	
	private static Logger LOG = Logger.getLogger(MetadataElementTemplateWebServiceImpl.class);

	@Override
	public List<MetadataElementPageTemplate> findBeans(final MetadataElementPageTemplateSearchBean searchBean, final int from, final int size) {
		final MetadataElementPageTemplateEntity entity = templateSearchBeanConverter.convert(searchBean);
		final List<MetadataElementPageTemplateEntity> entityList = templateService.findBeans(entity, from, size);
		return (entityList != null) ? templateDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy()) : null;
	}

	@Override
	public int count(final MetadataElementPageTemplateSearchBean searchBean) {
		final MetadataElementPageTemplateEntity entity = templateSearchBeanConverter.convert(searchBean);
		return templateService.count(entity);
	}

	@Override
	public Response save(final MetadataElementPageTemplate template) {
		final Response response = new Response();
		try {
			if(template == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final MetadataElementPageTemplateEntity entity = templateDozerConverter.convertToEntity(template, true);
			templateService.save(entity);
			response.setStatus(ResponseStatus.SUCCESS);
			response.setResponseValue(entity.getId());
		} catch(BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
		} catch(Throwable e) {
			LOG.error("Unkonwn Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public Response delete(final String templateId) {
		final Response response = new Response();
		try {
			if(StringUtils.isNotBlank(templateId)) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			templateService.delete(templateId);
			response.setStatus(ResponseStatus.SUCCESS);
		} catch(BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
		} catch(Throwable e) {
			LOG.error("Unkonwn Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public MetadataElementPageTemplate findById(final String templateId) {
		final MetadataElementPageTemplateSearchBean searchBean = new MetadataElementPageTemplateSearchBean();
		searchBean.setKey(templateId);
		searchBean.setDeepCopy(true);
		final List<MetadataElementPageTemplate> foundList = findBeans(searchBean, 0, 1);
		return (CollectionUtils.isNotEmpty(foundList)) ? foundList.get(0) : null;
	}
}
