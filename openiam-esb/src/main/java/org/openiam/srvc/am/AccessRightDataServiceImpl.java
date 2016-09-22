package org.openiam.srvc.am;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.BaseGrudServiceRequest;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.IdsServiceRequest;
import org.openiam.base.response.AccessRightListResponse;
import org.openiam.base.response.AccessRightResponse;
import org.openiam.base.response.IntResponse;
import org.openiam.base.response.StringResponse;
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
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.model.AccessViewResponse;
import org.openiam.mq.constants.AccessReviewAPI;
import org.openiam.mq.constants.AccessRightAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("accessRightWS")
@WebService(endpointInterface = "org.openiam.srvc.am.AccessRightDataService", targetNamespace = "urn:idm.openiam.org/srvc/access/service", portName = "AccessRightDataServicePort", serviceName = "AccessRightDataService")
public class AccessRightDataServiceImpl extends AbstractApiService implements AccessRightDataService {

	public AccessRightDataServiceImpl() {
		super(OpenIAMQueue.AccessRightQueue);
	}

	@Override
	public StringResponse save(final AccessRight dto) {
		final BaseGrudServiceRequest<AccessRight> request = new BaseGrudServiceRequest<>(dto);
		final StringResponse response= this.manageApiRequest(AccessRightAPI.Save, request, StringResponse.class);
		return response;
	}

	@Override
	public Response delete(String id) {
		final IdServiceRequest request= new IdServiceRequest();
		request.setId(id);
		return this.manageApiRequest(AccessRightAPI.Delete, request, Response.class);
	}

	@Override
	public AccessRight get(String id) {
		final IdServiceRequest request= new IdServiceRequest();
		request.setId(id);
		final AccessRightResponse response = this.manageApiRequest(AccessRightAPI.GetAccessRight, request, AccessRightResponse.class);
		if(response.isFailure()){
			return null;
		}
		return response.getAccessRight();
	}

	@Override
	@LocalizedServiceGet
	public List<AccessRight> findBeans(final AccessRightSearchBean searchBean, final int from, final int size, final Language language) {
		final BaseSearchServiceRequest<AccessRightSearchBean> request = new BaseSearchServiceRequest();
		request.setSearchBean(searchBean);
		request.setFrom(from);
		request.setSize(size);
		request.setLanguage(language);


		final AccessRightListResponse response = this.manageApiRequest(AccessRightAPI.FindBeans, request, AccessRightListResponse.class);
		if(response.isFailure()){
			return null;
		}
		return response.getAccessRightList();
	}

	@Override
	public int count(AccessRightSearchBean searchBean) {
		final BaseSearchServiceRequest<AccessRightSearchBean> request = new BaseSearchServiceRequest();
		request.setSearchBean(searchBean);

		final IntResponse response = this.manageApiRequest(AccessRightAPI.Count, request, IntResponse.class);
		if(response.isFailure()){
			return 0;
		}
		return response.getValue();
	}

	@Override
	public List<AccessRight> getByIds(final Collection<String> ids) {

		final IdsServiceRequest request= new IdsServiceRequest();
		request.setIds(new ArrayList<>(ids));
		final AccessRightListResponse response = this.manageApiRequest(AccessRightAPI.GetByIds, request, AccessRightListResponse.class);
		if(response.isFailure()){
			return null;
		}
		return response.getAccessRightList();
	}

}
