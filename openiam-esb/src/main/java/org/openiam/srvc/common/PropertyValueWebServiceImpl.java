package org.openiam.srvc.common;

import java.util.List;

import javax.jws.WebService;

import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.PropertyValueCrudRequest;
import org.openiam.base.response.data.StringResponse;
import org.openiam.base.response.list.PropertyValueListResponse;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.mq.constants.api.common.PropertyValueAPI;
import org.openiam.mq.constants.queue.common.PropertyValueQueue;
import org.openiam.property.dto.PropertyValue;
import org.openiam.srvc.AbstractApiService;
import org.openiam.util.SpringSecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("propertyValueWS")
@WebService(
	endpointInterface = "org.openiam.srvc.common.PropertyValueWebService",
	targetNamespace = "urn:idm.openiam.org/srvc/property/service", 
	portName = "PropertyValueWebServicePort", 
	serviceName = "PropertyValueWebService"
)
public class PropertyValueWebServiceImpl extends AbstractApiService implements PropertyValueWebService {
	
	@Autowired
	public PropertyValueWebServiceImpl(PropertyValueQueue queue) {
		super(queue);
	}

	 @Override
	 public Response save(List<PropertyValue> dtoList) {
		PropertyValueCrudRequest request = new PropertyValueCrudRequest();
		request.setRequesterId(SpringSecurityHelper.getRequestorUserId());
		request.setPropertyValueList(dtoList);
		return this.manageCrudApiRequest(PropertyValueAPI.Save, request);
	 }

	@Override
	public List<PropertyValue> getAll() {
		return this.getValueList(PropertyValueAPI.GetAll, new EmptyServiceRequest(), PropertyValueListResponse.class);
	}

	@Override
	public String getCachedValue(final String key) {
		IdServiceRequest request = new IdServiceRequest();
		request.setId(key);
		return this.getValue(PropertyValueAPI.GetCachedValue, request, StringResponse.class);
	}

}
