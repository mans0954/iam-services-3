package org.openiam.service.integration.contentprovider;

import java.util.List;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.UIThemeSearchBean;
import org.openiam.idm.srvc.ui.theme.UIThemeWebService;
import org.openiam.idm.srvc.ui.theme.dto.UITheme;
import org.openiam.service.integration.AbstractKeyServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class UIThemeServiceTest extends AbstractKeyServiceTest<UITheme, UIThemeSearchBean> {

	@Autowired
	@Qualifier("uiThemeClient")
	private UIThemeWebService uiThemeClient;

	@Override
	protected UITheme newInstance() {
		final UITheme theme = new UITheme();
		theme.setName(getRandomName());
		theme.setUrl(getRandomName());
		return theme;
	}

	@Override
	protected UIThemeSearchBean newSearchBean() {
		return new UIThemeSearchBean();
	}

	@Override
	protected Response save(UITheme t) {
		return uiThemeClient.save(t);
	}

	@Override
	protected Response delete(UITheme t) {
		return uiThemeClient.delete(t.getId());
	}

	@Override
	protected UITheme get(String key) {
		return uiThemeClient.get(key);
	}

	@Override
	public List<UITheme> find(UIThemeSearchBean searchBean, int from, int size) {
		return uiThemeClient.findBeans(searchBean, from, size);
	}

	@Override
	protected String getId(UITheme bean) {
		return bean.getId();
	}

	@Override
	protected void setId(UITheme bean, String id) {
		bean.setId(id);
	}
}
