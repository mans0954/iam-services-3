package org.openiam.idm.srvc.ui.theme;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.UIThemeDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.EsbErrorToken;
import org.openiam.idm.searchbeans.UIThemeSearchBean;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;
import org.openiam.idm.srvc.ui.theme.dto.UITheme;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("uiThemeWS")
@WebService(endpointInterface = "org.openiam.idm.srvc.ui.theme.UIThemeWebService", targetNamespace = "urn:idm.openiam.org/srvc/ui/theme/service", portName = "UIThemeWebServiceServicePort", serviceName = "UIThemeWebService")
public class UIThemeWebServiceImpl implements UIThemeWebService {

	private static final Log log = LogFactory.getLog(UIThemeWebServiceImpl.class);
	
	@Autowired
	private UIThemeService uiThemeService;

	@Autowired
	private UIThemeDozerConverter dozerConverter;
	
	
	@WebMethod
	public Response save(final UITheme dto) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			final UIThemeEntity entity = dozerConverter.convertToEntity(dto, false);
			uiThemeService.validateSave(entity);
			uiThemeService.save(entity);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
		} catch(Throwable e) {
			log.error("Can't perform operation", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            response.setErrorCode(ResponseCode.INTERNAL_ERROR);
            response.addErrorToken(new EsbErrorToken(e.getMessage()));
		}
		return response;
	}
	
	@WebMethod
	public Response delete(final String id) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			uiThemeService.validateDelete(id);
			uiThemeService.delete(id);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
		} catch(Throwable e) {
			log.error("Can't perform operation", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            response.setErrorCode(ResponseCode.INTERNAL_ERROR);
            response.addErrorToken(new EsbErrorToken(e.getMessage()));
		}
		return response;
	}
	
	@WebMethod
	public UITheme get(final String id) {
		final UIThemeEntity entity = uiThemeService.get(id);
		return dozerConverter.convertToDTO(entity, true);
	}
	
	@WebMethod
	public List<UITheme> findBeans(final UIThemeSearchBean searchBean, final int from, final int size) {
		final List<UIThemeEntity> entityList = uiThemeService.findBeans(searchBean, from, size);
		return dozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy());
	}
}
