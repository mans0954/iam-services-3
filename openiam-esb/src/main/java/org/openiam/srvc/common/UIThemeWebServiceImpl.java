package org.openiam.srvc.common;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.data.UIThemeResponse;
import org.openiam.base.response.list.UIThemeListResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.UIThemeDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.EsbErrorToken;
import org.openiam.idm.searchbeans.UIThemeSearchBean;
import org.openiam.idm.srvc.ui.theme.UIThemeService;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;
import org.openiam.idm.srvc.ui.theme.dto.UITheme;
import org.openiam.mq.constants.api.common.UIThemeAPI;
import org.openiam.mq.constants.queue.MqQueue;
import org.openiam.mq.constants.queue.common.UIThemeQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("uiThemeWS")
@WebService(endpointInterface = "org.openiam.srvc.common.UIThemeWebService", targetNamespace = "urn:idm.openiam.org/srvc/ui/theme/service", portName = "UIThemeWebServiceServicePort", serviceName = "UIThemeWebService")
public class UIThemeWebServiceImpl extends AbstractApiService implements UIThemeWebService {
	@Autowired
	public UIThemeWebServiceImpl(UIThemeQueue queue) {
		super(queue);
	}
	@WebMethod
	public Response save(final UITheme dto) {
		return this.manageCrudApiRequest(UIThemeAPI.Save, dto);
	}
	
	@WebMethod
	public Response delete(final String id) {
		UITheme dto = new UITheme();
		dto.setId(id);

		return this.manageCrudApiRequest(UIThemeAPI.Delete, dto);
	}
	
	@WebMethod
	public UITheme get(final String id) {
		IdServiceRequest request = new IdServiceRequest();
		request.setId(id);
		return this.getValue(UIThemeAPI.Get, request, UIThemeResponse.class);
	}
	
	@WebMethod
	public List<UITheme> findBeans(final UIThemeSearchBean searchBean, final int from, final int size) {
		return this.getValueList(UIThemeAPI.FindBeans, new BaseSearchServiceRequest<>(searchBean, from, size), UIThemeListResponse.class);
	}
}
