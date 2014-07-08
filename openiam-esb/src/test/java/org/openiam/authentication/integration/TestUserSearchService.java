package org.openiam.authentication.integration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.junit.Assert;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchParam;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class TestUserSearchService extends AbstractTestNGSpringContextTests {
	
	private static Logger LOG = Logger.getLogger(TestUserSearchService.class);

    @Resource(name = "userServiceClient")
    protected UserDataWebService userDataWebService;
    
	@Autowired
	@Qualifier("jdbcTemplate")
	protected JdbcTemplate jdbcTemplate;
	
	private List<User> userList = null;
	private static final String QUERY = "SELECT " +
										"	u.FIRST_NAME AS FIRST_NAME, " +
										"	u.LAST_NAME AS LAST_NAME, " +
										"	u.USER_ID AS USER_ID, " +
										"	u.MAIDEN_NAME AS MAIDEN_NAME, " +
										"	u.EMPLOYEE_ID AS EMPLOYEE_ID, " +
										"	e.EMAIL_ADDRESS AS EMAIL, " +
										"	l.LOGIN AS LOGIN, " +
										"	l.MANAGED_SYS_ID AS MANAGED_SYS_ID " +
										"FROM USERS u, LOGIN l, EMAIL_ADDRESS e " +
										"	WHERE u.USER_ID=l.USER_ID " +
										"	AND e.PARENT_ID=u.USER_ID " +
										"	LIMIT 0, 300";
    
    @BeforeClass
    protected void setUp() throws Exception {
    	LOG.info(String.format("Query: %s", QUERY));
    	userList = jdbcTemplate.query(QUERY, new ResultSetExtractor<List<User>>() {

			@Override
			public List<User> extractData(final ResultSet rs) throws SQLException,
					DataAccessException {
				final Map<String, User> userMap = new HashMap<>();
				while(rs.next()) {
					final String firstName = rs.getString("FIRST_NAME");
					final String lastName = rs.getString("LAST_NAME");
					final String userId = rs.getString("USER_ID");
					final String maidenName = rs.getString("MAIDEN_NAME");
					final String employeeId = rs.getString("EMPLOYEE_ID");
					final String emailAddress = rs.getString("EMAIL");
					final String login = rs.getString("LOGIN");
					final String managedSysId = rs.getString("MANAGED_SYS_ID");
					
					User user = userMap.get(userId);
					if(user == null) {
						user = new User();
						user.setFirstName(firstName);
						user.setLastName(lastName);
						user.setMaidenName(maidenName);
						user.setEmployeeId(employeeId);
						userMap.put(userId, user);
					}
					
					if(CollectionUtils.isEmpty(user.getEmailAddresses())) {
						user.setEmailAddresses(new HashSet<EmailAddress>());
					}
					final EmailAddress address = new EmailAddress();
					address.setEmailAddress(emailAddress);
					user.getEmailAddresses().add(address);
					
					if(CollectionUtils.isEmpty(user.getPrincipalList())) {
						user.setPrincipalList(new LinkedList<Login>());
					}
					final Login principal = new Login();
					principal.setLogin(login);
					principal.setManagedSysId(managedSysId);
					user.getPrincipalList().add(principal);
				}
				return new LinkedList<>(userMap.values());
			}
    		
		});
    }
    
    @Test
    public void testFirstNameExactSearch() {
    	MatchType matchType = MatchType.EXACT;
    	for(int i = 0; i < userList.size(); i++) {
    		final User user = userList.get(i);
    		if(StringUtils.isNotBlank(user.getFirstName())) {
	    		final UserSearchBean searchBean = new UserSearchBean();
	    		searchBean.setFirstNameMatchToken(new SearchParam(user.getFirstName(), matchType));
	    		final List<User> results = userDataWebService.findBeans(searchBean, 0, 10);
	    		Assert.assertTrue(String.format("No results were produced for firstName:%s", user.getFirstName()), CollectionUtils.isNotEmpty(results));
	    		for(final User result : results) {
	    			Assert.assertEquals(String.format("User result: %s did not match query:%s", result.getFirstName(), user.getFirstName()), user.getFirstName(), result.getFirstName());
	    		}
    		}
    	}
    }
    
    @Test
    public void testFirstNameStartsWithSearch() {
    	MatchType matchType = MatchType.STARTS_WITH;
    	String searchTerm = null;
    	for(final User u : userList) {
    		if(StringUtils.isNotBlank(u.getFirstName())) {
    			searchTerm = u.getFirstName().substring(0, 1);
    			break;
    		}
    	}
    	if(searchTerm != null) {
	    	final UserSearchBean searchBean = new UserSearchBean();
			searchBean.setFirstNameMatchToken(new SearchParam(searchTerm, matchType));
			final List<User> results = userDataWebService.findBeans(searchBean, 0, 10);
			Assert.assertTrue(String.format("No results were produced for firstName:%s", searchTerm), CollectionUtils.isNotEmpty(results));
			for(final User result : results) {
				Assert.assertTrue(String.format("User result: %s did not match query:%s", result.getFirstName(), searchTerm), startsWithIgnoreCase(result.getFirstName(), searchTerm));
			}
    	}
    }
    
    @Test
    public void testLastNameExactSearch() {
    	MatchType matchType = MatchType.EXACT;
    	for(int i = 0; i < userList.size(); i++) {
    		final User user = userList.get(i);
    		if(StringUtils.isNotBlank(user.getLastName())) {
	    		final UserSearchBean searchBean = new UserSearchBean();
	    		searchBean.setLastNameMatchToken(new SearchParam(user.getLastName(), matchType));
	    		final List<User> results = userDataWebService.findBeans(searchBean, 0, 10);
	    		Assert.assertTrue(String.format("No results were produced for lastName:%s", user.getLastName()), CollectionUtils.isNotEmpty(results));
	    		for(final User result : results) {
	    			Assert.assertEquals(String.format("User result: %s did not match query:%s", result.getLastName(), user.getLastName()), user.getLastName(), result.getLastName());
	    		}
    		}
    	}
    }
    

    @Test
    public void testLastNameStartsWithSearch() {
    	MatchType matchType = MatchType.STARTS_WITH;
    	String searchTerm = null;
    	for(final User u : userList) {
    		if(StringUtils.isNotBlank(u.getLastName())) {
    			searchTerm = u.getLastName().substring(0, 1);
    			break;
    		}
    	}
    	if(searchTerm != null) {
	    	final UserSearchBean searchBean = new UserSearchBean();
			searchBean.setLastNameMatchToken(new SearchParam(searchTerm, matchType));
			final List<User> results = userDataWebService.findBeans(searchBean, 0, 10);
			Assert.assertTrue(String.format("No results were produced for lastName:%s", searchTerm), CollectionUtils.isNotEmpty(results));
			for(final User result : results) {
				Assert.assertTrue(String.format("User result: %s did not match query:%s", result.getLastName(), searchTerm), startsWithIgnoreCase(result.getLastName(), searchTerm));
			}
    	}
    }
    
    @Test
    public void testMaidenNameExactSearch() {
    	MatchType matchType = MatchType.EXACT;
    	for(int i = 0; i < userList.size(); i++) {
    		final User user = userList.get(i);
    		if(StringUtils.isNotBlank(user.getMaidenName())) {
	    		final UserSearchBean searchBean = new UserSearchBean();
	    		searchBean.setMaidenNameMatchToken(new SearchParam(user.getMaidenName(), matchType));
	    		final List<User> results = userDataWebService.findBeans(searchBean, 0, 10);
	    		Assert.assertTrue(String.format("No results were produced for maidenName:%s", user.getMaidenName()), CollectionUtils.isNotEmpty(results));
	    		for(final User result : results) {
	    			Assert.assertEquals(String.format("User result: %s did not match query:%s", result.getMaidenName(), user.getMaidenName()), user.getMaidenName(), result.getMaidenName());
	    		}
    		}
    	}
    }
    
    @Test
    public void testMaidenNameStartsWithSearch() {
    	MatchType matchType = MatchType.STARTS_WITH;
    	String searchTerm = null;
    	for(final User u : userList) {
    		if(StringUtils.isNotBlank(u.getMaidenName())) {
    			searchTerm = u.getMaidenName().substring(0, 1);
    			break;
    		}
    	}
    	if(searchTerm != null) {
	    	final UserSearchBean searchBean = new UserSearchBean();
			searchBean.setMaidenNameMatchToken(new SearchParam(searchTerm, matchType));
			final List<User> results = userDataWebService.findBeans(searchBean, 0, 10);
			Assert.assertTrue(String.format("No results were produced for maidenName:%s", searchTerm), CollectionUtils.isNotEmpty(results));
			for(final User result : results) {
				Assert.assertTrue(String.format("User result: %s did not match query:%s", result.getMaidenName(), searchTerm), startsWithIgnoreCase(result.getMaidenName(), searchTerm));
			}
    	}
    }

    @Test
    public void testEmployeeIdExactSearch() {
    	MatchType matchType = MatchType.EXACT;
    	for(int i = 0; i < userList.size(); i++) {
    		final User user = userList.get(i);
    		if(StringUtils.isNotBlank(user.getEmployeeId())) {
	    		final UserSearchBean searchBean = new UserSearchBean();
	    		searchBean.setEmployeeIdMatchToken(new SearchParam(user.getEmployeeId(), matchType));
	    		final List<User> results = userDataWebService.findBeans(searchBean, 0, 10);
	    		Assert.assertTrue(String.format("No results were produced for employeeId:%s", user.getEmployeeId()), CollectionUtils.isNotEmpty(results));
	    		for(final User result : results) {
	    			Assert.assertEquals(String.format("User result: %s did not match query:%s", result.getEmployeeId(), user.getEmployeeId()), user.getEmployeeId(), result.getEmployeeId());
	    		}
    		}
    	}
    }
    
    @Test
    public void testEmployeeIdStartsWithSearch() {
    	MatchType matchType = MatchType.STARTS_WITH;
    	String searchTerm = null;
    	for(final User u : userList) {
    		if(StringUtils.isNotBlank(u.getEmployeeId())) {
    			searchTerm = u.getEmployeeId().substring(0, 1);
    			break;
    		}
    	}
    	if(searchTerm != null) {
	    	final UserSearchBean searchBean = new UserSearchBean();
			searchBean.setEmployeeIdMatchToken(new SearchParam(searchTerm, matchType));
			final List<User> results = userDataWebService.findBeans(searchBean, 0, 10);
			Assert.assertTrue(String.format("No results were produced for employeeId:%s", searchTerm), CollectionUtils.isNotEmpty(results));
			for(final User result : results) {
				Assert.assertTrue(String.format("User result: %s did not match query:%s", result.getEmployeeId(), searchTerm), startsWithIgnoreCase(result.getEmployeeId(), searchTerm));
			}
    	}
    }
    
    @Test
    public void testEmailExactSearch() {
    	MatchType matchType = MatchType.EXACT;
    	for(int i = 0; i < userList.size(); i++) {
    		final User user = userList.get(i);
    		if(CollectionUtils.isNotEmpty(user.getEmailAddresses())) {
    			for(final EmailAddress emailAddress : user.getEmailAddresses()) {
    				final UserSearchBean searchBean = new UserSearchBean();
    				searchBean.setDeepCopy(true);
    				searchBean.setEmailAddressMatchToken(new SearchParam(emailAddress.getEmailAddress(), matchType));
    				final List<User> results = userDataWebService.findBeans(searchBean, 0, 10);
    				Assert.assertTrue(String.format("No results were produced for email:%s", emailAddress.getEmailAddress()), CollectionUtils.isNotEmpty(results));
    				for(final User result : results) {
    					Assert.assertTrue(String.format("No results were produced for email:%s", emailAddress.getEmailAddress()), CollectionUtils.isNotEmpty(result.getEmailAddresses()));
    					boolean hasEmail = false;
    					for(final EmailAddress resultAddress : result.getEmailAddresses()) {
    						if(!hasEmail) {
    							hasEmail = StringUtils.equalsIgnoreCase(resultAddress.getEmailAddress(), emailAddress.getEmailAddress());
    						}
    					}
    					
    					Assert.assertTrue(String.format("User result: %s did not match query", emailAddress.getEmailAddress()), hasEmail);
    				}
    			}
    		}
    	}
    }
    
    @Test
    public void testEmailStartsWithSearch() {
    	MatchType matchType = MatchType.STARTS_WITH;
    	for(int i = 0; i < userList.size(); i++) {
    		final User user = userList.get(i);
    		String searchTerm = null;
    		if(CollectionUtils.isNotEmpty(user.getEmailAddresses())) {
    			for(final EmailAddress emailAddress : user.getEmailAddresses()) {
    				if(StringUtils.isNotBlank(emailAddress.getEmailAddress())) {
    					searchTerm = emailAddress.getEmailAddress().substring(0, 1);
    				}
    			}
    		}
    		
			final UserSearchBean searchBean = new UserSearchBean();
			searchBean.setDeepCopy(true);
			searchBean.setEmailAddressMatchToken(new SearchParam(searchTerm, matchType));
			final List<User> results = userDataWebService.findBeans(searchBean, 0, 10);
			Assert.assertTrue(String.format("No results were produced for email:%s", searchTerm), CollectionUtils.isNotEmpty(results));
			for(final User result : results) {
				Assert.assertTrue(String.format("No results were produced for email:%s", searchTerm), CollectionUtils.isNotEmpty(result.getEmailAddresses()));
				boolean hasEmail = false;
				for(final EmailAddress resultAddress : result.getEmailAddresses()) {
					if(!hasEmail) {
						hasEmail = startsWithIgnoreCase(resultAddress.getEmailAddress(), searchTerm);
					}
				}
				
				Assert.assertTrue(String.format("User result: %s did not match query", searchTerm), hasEmail);
			}
    	}
    }
    
    @Test
    public void testLoginExactSearch() {
    	MatchType matchType = MatchType.EXACT;
    	for(int i = 0; i < userList.size(); i++) {
    		final User user = userList.get(i);
    		if(CollectionUtils.isNotEmpty(user.getPrincipalList())) {
    			for(final Login login : user.getPrincipalList()) {
    				final UserSearchBean searchBean = new UserSearchBean();
    				searchBean.setDeepCopy(true);
    				final LoginSearchBean loginSearchBean = new LoginSearchBean();
    				loginSearchBean.setLoginMatchToken(new SearchParam(login.getLogin(), matchType));
    				searchBean.setPrincipal(loginSearchBean);
    				final List<User> results = userDataWebService.findBeans(searchBean, 0, 10);
    				Assert.assertTrue(String.format("No results were produced for login:%s", login.getLogin()), CollectionUtils.isNotEmpty(results));
    				for(final User result : results) {
    					Assert.assertTrue(String.format("No results were produced for login:%s", login.getLogin()), CollectionUtils.isNotEmpty(result.getPrincipalList()));
    					boolean hasPrincipal = false;
    					for(final Login resultLogin : result.getPrincipalList()) {
    						if(!hasPrincipal) {
    							hasPrincipal = StringUtils.equalsIgnoreCase(resultLogin.getLogin(), login.getLogin());
    						}
    					}
    					
    					Assert.assertTrue(String.format("User result: %s did not match query", login.getLogin()), hasPrincipal);
    				}
    			}
    		}
    	}
    }
    
    @Test
    public void testLoginStartsWithSearch() {
    	MatchType matchType = MatchType.STARTS_WITH;
    	for(int i = 0; i < userList.size(); i++) {
    		final User user = userList.get(i);
    		String searchTerm = null;
    		if(CollectionUtils.isNotEmpty(user.getPrincipalList())) {
    			for(final Login login : user.getPrincipalList()) {
    				if(StringUtils.isNotBlank(login.getLogin()) && login.getManagedSysId().equals("0")) {
    					searchTerm = login.getLogin().substring(0, 1);
    				}
    			}
    		}
    		
    		
			final UserSearchBean searchBean = new UserSearchBean();
			searchBean.setDeepCopy(true);
			final LoginSearchBean loginSearchBean = new LoginSearchBean();
			loginSearchBean.setLoginMatchToken(new SearchParam(searchTerm, matchType));
			loginSearchBean.setManagedSysId("0");
			searchBean.setPrincipal(loginSearchBean);
			final List<User> results = userDataWebService.findBeans(searchBean, 0, 10);
			Assert.assertTrue(String.format("No results were produced for login:%s", searchTerm), CollectionUtils.isNotEmpty(results));
			for(final User result : results) {
				Assert.assertTrue(String.format("No results were produced for login:%s", searchTerm), CollectionUtils.isNotEmpty(result.getPrincipalList()));
				boolean hasPrincipal = false;
				for(final Login resultLogin : result.getPrincipalList()) {
					if(!hasPrincipal) {
						hasPrincipal = startsWithIgnoreCase(resultLogin.getLogin(), searchTerm);
					}
				}
				
				Assert.assertTrue(String.format("User result: %s did not match query", searchTerm), hasPrincipal);
			}
    	}
    }
    
    private boolean startsWithIgnoreCase(final String s, final String prefix) {
    	return s.toLowerCase().startsWith(prefix.toLowerCase());
    }
}
