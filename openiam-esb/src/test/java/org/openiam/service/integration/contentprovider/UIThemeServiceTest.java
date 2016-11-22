package org.openiam.service.integration.contentprovider;

import java.util.List;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.UIThemeSearchBean;
import org.openiam.srvc.common.UIThemeWebService;
import org.openiam.idm.srvc.ui.theme.dto.UITheme;
import org.openiam.service.integration.AbstractKeyServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

public class UIThemeServiceTest extends AbstractKeyServiceTest<UITheme, UIThemeSearchBean> {
	
	@Test
	public void clusterTest() throws Exception {
		final ClusterKey<UITheme, UIThemeSearchBean> key = doClusterTest();
		final UITheme instance = key.getDto();
		if(instance != null && instance.getId() != null) {
			deleteAndAssert(instance);
    	}
	}
	
	public ClusterKey<UITheme, UIThemeSearchBean> doClusterTest() throws Exception {
/*
 create and save
*/

		UITheme instance = createBean();
		Response response = saveAndAssert(instance);
		instance.setId((String)response.getResponseValue());

/*
 find
*/

		final UIThemeSearchBean searchBean = newSearchBean();
		searchBean.setDeepCopy(useDeepCopyOnFindBeans());
		searchBean.addKey(instance.getId());

/*
 confirm save on both nodes
*/

		instance = assertClusteredSave(searchBean);
		return new ClusterKey<UITheme, UIThemeSearchBean>(instance, searchBean);
	}

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

/*	@Override
	protected String getId(UITheme bean) {
		return bean.getId();
	}

	@Override
	protected void setId(UITheme bean, String id) {
		bean.setId(id);
	}*/
}
