package org.openiam.service.integration.login;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractKeyServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LoginServiceTest extends AbstractKeyServiceTest<Login, LoginSearchBean> {
	
	private User user = null;
	
	@BeforeClass
	public void _init() {
		user = super.createUser();
	}
	
	@AfterClass
	public void _destroy() {
		userServiceClient.removeUser(user.getId());
	}

	@Autowired
	@Qualifier("loginServiceClient")
	private LoginDataWebService loginServiceClient;
	
	@Override
	protected Login newInstance() {
		final Login login = new Login();
		login.setManagedSysId(getDefaultManagedSystemId());
		login.setUserId(user.getId());
		login.setLogin(getRandomName());
		return login;
	}

	@Override
	protected LoginSearchBean newSearchBean() {
		final LoginSearchBean searchBean = new LoginSearchBean();
		return searchBean;
	}

	@Override
	protected Response save(Login t) {
		return loginServiceClient.saveLogin(t);
	}

	@Override
	protected Response delete(Login t) {
		return loginServiceClient.deleteLogin(t.getId());
	}

	@Override
	protected Login get(String key) {
		return loginServiceClient.findById(key);
	}

	@Override
	public List<Login> find(LoginSearchBean searchBean, int from, int size) {
		return loginServiceClient.findBeans(searchBean, from, size);
	}

/*	@Override
	protected String getId(Login bean) {
		return bean.getLoginId();
	}

	@Override
	protected void setId(Login bean, String id) {
		bean.setLoginId(id);
	}*/

	@Test
	public void testElasticSearch() {
		final LoginSearchBean sb = newSearchBean();
		sb.setUserId("3000");
		Assert.assertTrue(CollectionUtils.isNotEmpty(find(sb, 0, 1)));
		
		sb.setManagedSysId("0");
		Assert.assertTrue(CollectionUtils.isNotEmpty(find(sb, 0, 1)));
	}
	
	@Test
	public void clusterTest() throws Exception {
		final ClusterKey<Login, LoginSearchBean> key = doClusterTest();
		final Login instance = key.getDto();
		if(instance != null && instance.getId() != null) {
			deleteAndAssert(instance);
    	}
	}
	
	public ClusterKey<Login, LoginSearchBean> doClusterTest() throws Exception {
/*
 create and save
*/

		Login instance = createBean();
		Response response = saveAndAssert(instance);
		instance.setId((String)response.getResponseValue());

 /*find*/

		final LoginSearchBean searchBean = newSearchBean();
		searchBean.setDeepCopy(useDeepCopyOnFindBeans());
		searchBean.setKey(instance.getId());

/*
 confirm save on both nodes
*/

		instance = assertClusteredSave(searchBean);
		return new ClusterKey<Login, LoginSearchBean>(instance, searchBean);
	}
}
