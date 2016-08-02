package org.openiam.service.integration.user;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.SearchParam;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractKeyServiceTest;
import org.openiam.service.integration.AbstractServiceTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UserServiceTest extends AbstractServiceTest {

	private List<User> createdUsers = new LinkedList<User>();
	
	@BeforeClass
	protected void _setUp() throws Exception {
		final Set<String> emails = new HashSet<String>();
		for(int i = 0; i < 5; i++) {
			emails.add(String.format("%s@gmail.com", RandomStringUtils.randomAlphanumeric(5)));
		}
		for(final String email : emails) {
			final User user = super.createUser(email);
			Assert.assertNotNull(user);
			Assert.assertTrue(CollectionUtils.isNotEmpty(user.getEmailAddresses()));
			Assert.assertTrue(user.getEmailAddresses().iterator().next().getEmailAddress().equals(email));
			createdUsers.add(user);
		}
	}
	
	@Test
	public void testSearchByUniqueEmail() {
		createdUsers.forEach(e -> {
			final String email = e.getEmailAddresses().iterator().next().getEmailAddress();
			
			final UserSearchBean sb = new UserSearchBean();
			sb.setEmailAddressMatchToken(new SearchParam(email, MatchType.STARTS_WITH));
			final List<User> matchList = userServiceClient.findBeans(sb, 0, 10);
			Assert.assertTrue(CollectionUtils.isNotEmpty(matchList));
			Assert.assertEquals(1, matchList.size());
			Assert.assertEquals(e.getId(), matchList.get(0).getId());
		});
	}
	
	@AfterClass
	public void _tearDown()  throws Exception {
		createdUsers.forEach(e -> {
			assertSuccess(userServiceClient.deleteUser(e.getId()));
		});
	}
}
