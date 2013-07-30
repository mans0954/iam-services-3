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
import org.openiam.dozer.converter.MetadataTemplateTypeDozerConverter;
import org.openiam.dozer.converter.MetadataTemplateTypeFieldDozerConverter;
import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeFieldSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateType;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateTypeField;
import org.openiam.idm.srvc.meta.dto.PageTempate;
import org.openiam.idm.srvc.meta.dto.TemplateRequest;
import org.openiam.idm.srvc.meta.service.MetadataElementTemplateService;
import org.openiam.idm.srvc.searchbean.converter.MetadataElementTemplateSearchBeanConverter;
import org.openiam.idm.srvc.searchbean.converter.MetadataTemplateTypeSearchBeanConverter;
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
	
	@Autowired
	private MetadataTemplateTypeDozerConverter templateTypeDozerConverter;
	
	@Autowired
	private MetadataTemplateTypeSearchBeanConverter templateTypeSearchBeanConverter;
	
	@Autowired
	private MetadataTemplateTypeFieldDozerConverter uiFieldDozerConverter;
	
	private static Logger LOG = Logger.getLogger(MetadataElementTemplateWebServiceImpl.class);

	@Override
	public List<MetadataElementPageTemplate> findBeans(final MetadataElementPageTemplateSearchBean searchBean, final int from, final int size) {
		final List<MetadataElementPageTemplateEntity> entityList = templateService.findBeans(searchBean, from, size);
		return (entityList != null) ? templateDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy()) : null;
	}

	@Override
	public int count(final MetadataElementPageTemplateSearchBean searchBean) {
		return templateService.count(searchBean);
	}

	@Override
	public Response save(final MetadataElementPageTemplate template) {
		final Response response = new Response();
		try {
			if(template == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			if(StringUtils.isBlank(template.getMetadataTemplateTypeId())) {
				throw new BasicDataServiceException(ResponseCode.TEMPLATE_TYPE_REQUIRED);
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
			if(StringUtils.isBlank(templateId)) {
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

	@Override
	public PageTempate getTemplate(final TemplateRequest request) {
		return templateService.getTemplate(request);
	}

	@Override
	public MetadataTemplateType getTemplateType(final String templateId) {
		final MetadataTemplateTypeEntity entity = templateService.getTemplateType(templateId);
		return (entity != null) ? templateTypeDozerConverter.convertToDTO(entity, true) : null;
	}

	@Override
	public List<MetadataTemplateType> findTemplateTypes(final MetadataTemplateTypeSearchBean searchBean, final int from, final int size) {
		final MetadataTemplateTypeEntity entity = templateTypeSearchBeanConverter.convert(searchBean);
		final List<MetadataTemplateTypeEntity> entityList = templateService.findTemplateTypes(entity, from, size);
		return (entityList != null) ? templateTypeDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy()) : null;
	}

	@Override
	public List<MetadataTemplateTypeField> findUIFIelds(final MetadataTemplateTypeFieldSearchBean searchBean, final int from, final int size) {
		final List<MetadataTemplateTypeFieldEntity> entityList = templateService.findUIFields(searchBean, from, size);
		return (entityList != null) ? uiFieldDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy()) : null;
	}

    @Override
    public int countUIFields(final MetadataTemplateTypeFieldSearchBean searchBean) {
        final Integer count = templateService.countUIFields(searchBean);
        return (count != null) ? count.intValue() : 0;
    }
}
