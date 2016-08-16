package org.openiam.srvc.common;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.UIThemeSearchBean;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;
import org.openiam.idm.srvc.ui.theme.dto.UITheme;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/ui/theme/service", name = "UIThemeWebService")
public interface UIThemeWebService {

	@WebMethod
	Response save(final UITheme entity);
	
	@WebMethod
	Response delete(final String id);
	
	@WebMethod
	UITheme get(final String id);
	
	@WebMethod
	List<UITheme> findBeans(final UIThemeSearchBean searchBean, final int from, final int size);
}
