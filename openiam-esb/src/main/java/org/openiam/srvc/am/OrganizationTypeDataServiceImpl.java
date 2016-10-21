package org.openiam.srvc.am;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.BaseGrudServiceRequest;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.MembershipRequest;
import org.openiam.base.response.IntResponse;
import org.openiam.base.response.OrganizationTypeListResponse;
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
	
	public OrganizationTypeDataServiceImpl() {
		super(OpenIAMQueue.OrganizationTypeQueue);
	}

	@Override
	public OrganizationType findById(final String id, final Language language) {
		IdServiceRequest request = new IdServiceRequest(id);
		request.setLanguage(language);
		return this.getValue(OrganizationTypeAPI.GetById, request, OrganizationTypeResponse.class);
	}
	
	@Override
	public List<OrganizationType> findAllowedChildrenByDelegationFilter(final String requesterId, final Language language) {
		BaseSearchServiceRequest<OrganizationTypeSearchBean> request = new BaseSearchServiceRequest<>(new OrganizationTypeSearchBean());
		request.setLanguage(language);
		request.setRequesterId(requesterId);
		return this.getValueList(OrganizationTypeAPI.FindAllowedChildren, request, OrganizationTypeListResponse.class);
	}

	@Override
	public List<OrganizationType> findBeans(final OrganizationTypeSearchBean searchBean, final int from, final int size, final Language language) {
		BaseSearchServiceRequest<OrganizationTypeSearchBean> request = new BaseSearchServiceRequest<>(searchBean, from, size);
		request.setLanguage(language);
		return this.getValueList(OrganizationTypeAPI.FindBeans, request, OrganizationTypeListResponse.class);
	}

	@Override
	public int count(final OrganizationTypeSearchBean searchBean) {
		BaseSearchServiceRequest<OrganizationTypeSearchBean> request = new BaseSearchServiceRequest<>(searchBean);
		return this.getValue(OrganizationTypeAPI.Count, request, IntResponse.class);
	}

	@Override
	public Response save(final OrganizationType type) {
		return this.manageGrudApiRequest(OrganizationTypeAPI.Save, type);
	}

	@Override
	public Response delete(final String id) {
		return this.manageGrudApiRequest(OrganizationTypeAPI.Delete, id);
	}

	@Override
	public Response addChild(final String id, final String childId) {
		MembershipRequest request = new MembershipRequest();
		request.setObjectId(id);
		request.setLinkedObjectId(childId);
		return this.manageApiRequest(OrganizationTypeAPI.AddChild, request, Response.class);
	}

	@Override
	public Response removeChild(String id, String childId) {
		MembershipRequest request = new MembershipRequest();
		request.setObjectId(id);
		request.setLinkedObjectId(childId);
		return this.manageApiRequest(OrganizationTypeAPI.RemoveChild, request, Response.class);
	}

    @Override
    public List<OrganizationType> getAllowedParents(final String organizationTypeId, final String requesterId, final Language language){
		OrganizationTypeSearchBean searchBean = new OrganizationTypeSearchBean();
		searchBean.setKey(organizationTypeId);

		BaseSearchServiceRequest<OrganizationTypeSearchBean> request = new BaseSearchServiceRequest<>(searchBean);
		request.setLanguage(language);
		request.setRequesterId(requesterId);
		return this.getValueList(OrganizationTypeAPI.GetAllowedParents, request, OrganizationTypeListResponse.class);
    }
}
