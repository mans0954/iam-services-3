package org.openiam.srvc.common;

import java.util.List;

import javax.jws.WebService;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.data.MetadataTemplateTypeResponse;
import org.openiam.base.response.data.PageTempateResponse;
import org.openiam.base.response.list.MetadataElementPageTemplateListResponse;
import org.openiam.base.response.list.MetadataTemplateTypeFieldListResponse;
import org.openiam.base.response.list.MetadataTemplateTypeListResponse;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeFieldSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeSearchBean;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateType;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateTypeField;
import org.openiam.idm.srvc.meta.dto.PageTempate;
import org.openiam.idm.srvc.meta.dto.TemplateRequest;
import org.openiam.mq.constants.api.common.TemplateAPI;
import org.openiam.mq.constants.queue.common.MetadataElementTemplateQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("metadataElementTemplateWebService")
@WebService(endpointInterface = "org.openiam.srvc.common.MetadataElementTemplateWebService",
			targetNamespace = "urn:idm.openiam.org/srvc/meta/ws", 
			portName = "MetadataElementTemplateWebServicePort", 
			serviceName = "MetadataElementTemplateWebService")
public class MetadataElementTemplateWebServiceImpl extends AbstractApiService implements MetadataElementTemplateWebService {

	@Autowired
	public MetadataElementTemplateWebServiceImpl(MetadataElementTemplateQueue queue) {
		super(queue);
	}

	@Override
	public List<MetadataElementPageTemplate> findBeans(final MetadataElementPageTemplateSearchBean searchBean, final int from, final int size) {
		return this.getValueList(TemplateAPI.FindBeans, new BaseSearchServiceRequest<MetadataElementPageTemplateSearchBean>(searchBean, from, size), MetadataElementPageTemplateListResponse.class);
	}

	@Override
	public int count(final MetadataElementPageTemplateSearchBean searchBean) {
		return this.getIntValue(TemplateAPI.Count, new BaseSearchServiceRequest<MetadataElementPageTemplateSearchBean>(searchBean));
	}

	@Override
	public Response save(final MetadataElementPageTemplate template) {
		return this.manageCrudApiRequest(TemplateAPI.Save, template);
	}

	@Override
	public Response delete(final String templateId) {
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
	}

	@Override
	public List<MetadataTemplateType> findTemplateTypes(final MetadataTemplateTypeSearchBean searchBean, final int from, final int size) {
		return this.getValueList(TemplateAPI.FindTemplateType, new BaseSearchServiceRequest<>(searchBean, from, size), MetadataTemplateTypeListResponse.class);
	}

	@Override
	public List<MetadataTemplateTypeField> findUIFIelds(final MetadataTemplateTypeFieldSearchBean searchBean, final int from, final int size) {

		return this.getValueList(TemplateAPI.FindUIFIelds, new BaseSearchServiceRequest<>(searchBean, from, size), MetadataTemplateTypeFieldListResponse.class);
	}

    @Override
    public int countUIFields(final MetadataTemplateTypeFieldSearchBean searchBean) {
		return this.getIntValue(TemplateAPI.CountUIFields, new BaseSearchServiceRequest<>(searchBean));
    }
}
