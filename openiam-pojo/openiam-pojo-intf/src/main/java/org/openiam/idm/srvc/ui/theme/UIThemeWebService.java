package org.openiam.idm.srvc.ui.theme;

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
	public Response save(final UITheme entity);
	
	@WebMethod
	public Response delete(final String id);
	
	@WebMethod
	public UITheme get(final String id);
	
	@WebMethod
	public List<UITheme> findBeans(final UIThemeSearchBean searchBean, final int from, final int size);
}
