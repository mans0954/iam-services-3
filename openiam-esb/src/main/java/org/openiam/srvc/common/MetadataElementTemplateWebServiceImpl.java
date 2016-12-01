package org.openiam.srvc.common;

import java.util.List;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.data.MetadataTemplateTypeResponse;
import org.openiam.base.response.data.PageTempateResponse;
import org.openiam.base.response.list.MetadataElementPageTemplateListResponse;
import org.openiam.base.response.list.MetadataTemplateTypeFieldListResponse;
import org.openiam.base.response.list.MetadataTemplateTypeListResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.LanguageDozerConverter;
import org.openiam.dozer.converter.MetadataElementTemplateDozerConverter;
import org.openiam.dozer.converter.MetadataTemplateTypeDozerConverter;
import org.openiam.dozer.converter.MetadataTemplateTypeFieldDozerConverter;
import org.openiam.exception.BasicDataServiceException;
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
import org.openiam.mq.constants.api.common.TemplateAPI;
import org.openiam.mq.constants.queue.MqQueue;
import org.openiam.mq.constants.queue.common.MetadataElementTemplateQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("metadataElementTemplateWebService")
@WebService(endpointInterface = "org.openiam.srvc.common.MetadataElementTemplateWebService",
			targetNamespace = "urn:idm.openiam.org/srvc/meta/ws", 
			portName = "MetadataElementTemplateWebServicePort", 
			serviceName = "MetadataElementTemplateWebService")
public class MetadataElementTemplateWebServiceImpl extends AbstractApiService implements MetadataElementTemplateWebService {


	
	@Autowired
	private MetadataElementTemplateDozerConverter templateDozerConverter;
	
	@Autowired
	private LanguageDozerConverter languageConverter;
	
	@Autowired
	private MetadataTemplateTypeDozerConverter templateTypeDozerConverter;
	
	@Autowired
	private MetadataTemplateTypeFieldDozerConverter uiFieldDozerConverter;
	
	private static final Log LOG = LogFactory.getLog(MetadataElementTemplateWebServiceImpl.class);

	@Autowired
	public MetadataElementTemplateWebServiceImpl(MetadataElementTemplateQueue queue) {
		super(queue);
	}

	@Override
	public List<MetadataElementPageTemplate> findBeans(final MetadataElementPageTemplateSearchBean searchBean, final int from, final int size) {
//		final List<MetadataElementPageTemplateEntity> entityList = templateService.findBeans(searchBean, from, size);
//		return (entityList != null) ? templateDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy()) : null;
		return this.getValueList(TemplateAPI.FindBeans, new BaseSearchServiceRequest<MetadataElementPageTemplateSearchBean>(searchBean, from, size), MetadataElementPageTemplateListResponse.class);
	}

	@Override
	public int count(final MetadataElementPageTemplateSearchBean searchBean) {
		return this.getIntValue(TemplateAPI.Count, new BaseSearchServiceRequest<MetadataElementPageTemplateSearchBean>(searchBean));
	}

	@Override
	public Response save(final MetadataElementPageTemplate template) {
//		final Response response = new Response();
//		try {
//			if(template == null) {
//				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
//			}
//
//			if(StringUtils.isBlank(template.getMetadataTemplateTypeId())) {
//				throw new BasicDataServiceException(ResponseCode.TEMPLATE_TYPE_REQUIRED);
//			}
//
//			final MetadataElementPageTemplateEntity entity = templateDozerConverter.convertToEntity(template, true);
//			templateService.save(entity);
//			response.setStatus(ResponseStatus.SUCCESS);
//			response.setResponseValue(entity.getId());
//		} catch(BasicDataServiceException e) {
//			response.fail();
//			response.setErrorCode(e.getCode());
//		} catch(Throwable e) {
//			LOG.error("Unkonwn Exception", e);
//			response.fail();
//			response.setErrorText(e.getMessage());
//		}
//		return response;
		return this.manageCrudApiRequest(TemplateAPI.Save, template);
	}

	@Override
	public Response delete(final String templateId) {
//		final Response response = new Response();
//		try {
//			if(StringUtils.isBlank(templateId)) {
//				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
//			}
//
//			templateService.delete(templateId);
//			response.setStatus(ResponseStatus.SUCCESS);
//		} catch(BasicDataServiceException e) {
//			response.setErrorCode(e.getCode());
//			response.setStatus(ResponseStatus.FAILURE);
//		} catch(Throwable e) {
//			LOG.error("Unkonwn Exception", e);
//			response.setStatus(ResponseStatus.FAILURE);
//		}
//		return response;
		MetadataElementPageTemplate template = new MetadataElementPageTemplate();
		template.setId(templateId);
		return this.manageCrudApiRequest(TemplateAPI.Delete, template);
	}

	@Override
	public PageTempate getTemplate(final TemplateRequest request) {
		return this.getValue(TemplateAPI.GetTemplate, request, PageTempateResponse.class);
	}

	@Override
	public MetadataTemplateType getTemplateType(final String id) {
		IdServiceRequest request = new IdServiceRequest();
		request.setId(id);

		return this.getValue(TemplateAPI.GetTemplateType, request, MetadataTemplateTypeResponse.class);
//		final MetadataTemplateTypeEntity entity = templateService.getTemplateType(id);
//		return (entity != null) ? templateTypeDozerConverter.convertToDTO(entity, true) : null;
	}

	@Override
	public List<MetadataTemplateType> findTemplateTypes(final MetadataTemplateTypeSearchBean searchBean, final int from, final int size) {

		return this.getValueList(TemplateAPI.FindTemplateType, new BaseSearchServiceRequest<>(searchBean, from, size), MetadataTemplateTypeListResponse.class);
//		final List<MetadataTemplateTypeEntity> entityList = templateService.findTemplateTypes(searchBean, from, size);
//		return (entityList != null) ? templateTypeDozerConverter.convertToDTOList(entityList, (searchBean != null) ? searchBean.isDeepCopy() : false) : null;
	}

	@Override
	public List<MetadataTemplateTypeField> findUIFIelds(final MetadataTemplateTypeFieldSearchBean searchBean, final int from, final int size) {

		return this.getValueList(TemplateAPI.FindUIFIelds, new BaseSearchServiceRequest<>(searchBean, from, size), MetadataTemplateTypeFieldListResponse.class);
//		final List<MetadataTemplateTypeFieldEntity> entityList = templateService.findUIFields(searchBean, from, size);
//		return (entityList != null) ? uiFieldDozerConverter.convertToDTOList(entityList, (searchBean != null) ? searchBean.isDeepCopy() : false) : null;
	}

    @Override
    public int countUIFields(final MetadataTemplateTypeFieldSearchBean searchBean) {
		return this.getIntValue(TemplateAPI.CountUIFields, new BaseSearchServiceRequest<>(searchBean));
    }
}
