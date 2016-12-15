package org.openiam.srvc.am;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.MembershipRequest;
import org.openiam.base.response.data.IntResponse;
import org.openiam.base.response.list.OrganizationTypeListResponse;
import org.openiam.base.response.data.OrganizationTypeResponse;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.org.dto.OrganizationType;
import org.openiam.mq.constants.api.OrganizationTypeAPI;
import org.openiam.mq.constants.queue.am.OrganizationTypeQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.hazelcast.security.SecurityContext;

import javax.jws.WebService;

import java.util.List;

@Service("organizationTypeDataService")
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/org/service", name = "OrganizationTypeDataService")
public class OrganizationTypeDataServiceImpl extends AbstractApiService implements OrganizationTypeDataService {
	@Autowired
	public OrganizationTypeDataServiceImpl(OrganizationTypeQueue queue) {
		super(queue);
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
		return this.manageCrudApiRequest(OrganizationTypeAPI.Save, type);
	}

	@Override
	public Response delete(final String id) {
		OrganizationType obj = new OrganizationType();
		obj.setId(id);
		return this.manageCrudApiRequest(OrganizationTypeAPI.Delete, obj);
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
		searchBean.addKey(organizationTypeId);

		BaseSearchServiceRequest<OrganizationTypeSearchBean> request = new BaseSearchServiceRequest<>(searchBean);
		request.setLanguage(language);
		request.setRequesterId(requesterId);
		return this.getValueList(OrganizationTypeAPI.GetAllowedParents, request, OrganizationTypeListResponse.class);
    }
}
