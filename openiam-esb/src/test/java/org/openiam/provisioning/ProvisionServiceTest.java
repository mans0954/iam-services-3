package org.openiam.provisioning;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;
import org.openiam.srvc.common.LanguageWebService;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.srvc.audit.IdmAuditLogWebDataService;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.srvc.user.LoginDataWebService;
import org.openiam.base.response.LoginResponse;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.srvc.common.MetadataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.srvc.user.UserDataWebService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.base.response.ProvisionUserResponse;
import org.openiam.srvc.idm.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class ProvisionServiceTest extends AbstractTestNGSpringContextTests {
	
	private static final Log log = LogFactory.getLog(ProvisionServiceTest.class);

	@Autowired
	@Qualifier("provisionServiceClient")
	private ProvisionService provisionService;
	
	@Autowired
	@Qualifier("loginServiceClient")
	private LoginDataWebService loginServiceClient;
	
	@Autowired
    @Qualifier("metadataServiceClient")
    protected MetadataWebService metadataServiceClient;
	
	@Autowired
	@Qualifier("userServiceClient")
	private UserDataWebService userServiceClient;
	
	@Autowired
	@Qualifier("languageServiceClient")
	protected LanguageWebService languageServiceClient;

	@Autowired
	@Qualifier("auditServiceClient")
	protected IdmAuditLogWebDataService auditLogService;
	
	private String getRandomName() {
		return getRandomName(5);
	}
	
	private String getRandomName(final int count) {
		return RandomStringUtils.randomAlphanumeric(count);
	}
	
	protected Language getDefaultLanguage() {
		final LanguageSearchBean searchBean = new LanguageSearchBean();
		searchBean.addKey("1");
		searchBean.setFindInCache(true);
		return languageServiceClient.findBeans(searchBean, 0, 1, null).get(0);
	}
	
	protected List<MetadataType> getMetadataTypesByGrouping(final MetadataTypeGrouping grouping) {
    	final MetadataTypeSearchBean searchBean = new MetadataTypeSearchBean();
    	searchBean.setGrouping(grouping);
    	searchBean.setActive(true);
        final List<MetadataType> types = metadataServiceClient.findTypeBeans(searchBean, 0, Integer.MAX_VALUE);
        return types;
    }
	
	@Test
	public void testAuditLogElasticSearch() throws Exception {
		final SimpleAsyncTaskExecutor e = new SimpleAsyncTaskExecutor();
		final ThreadGroup g = new ThreadGroup("test");
		e.setThreadGroup(g);
		e.setConcurrencyLimit(5);
		final AtomicInteger i = new AtomicInteger();
		while(true) {
			e.execute(new Runnable() {
				
				@Override
				public void run() {
					final StopWatch sw = new StopWatch();
					sw.start();
					final IdmAuditLogEntity record = new IdmAuditLogEntity();
					record.setAction(getRandomName());
					record.setActivitiTaskName(getRandomName());
					record.setUserId("3000");
					final Response response = auditLogService.addLog(record);
					Assert.assertNotNull(response);
					Assert.assertTrue(response.isSuccess());
					sw.stop();
					final int idx = i.incrementAndGet();
					log.info(String.format("Took: %s, idx: %s", sw.getTime(), idx));
				}
			});
		}
	}
	
	@Test(enabled=false)
	public void testIDMAPPS2488() throws Exception {
		while(true) {
			final User user = new User();
			user.setFirstName(getRandomName());
			user.setLastName(getRandomName());
			user.setClassification(getRandomName());
			user.setBirthdate(new Date());
			user.setClaimDate(new Date());
			user.setCostCenter(getRandomName());
			user.setEmployeeId(getRandomName());
			user.setEmployeeTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_TYPE).get(0).getId());
			user.setJobCodeId(getMetadataTypesByGrouping(MetadataTypeGrouping.JOB_CODE).get(0).getId());
			user.setLocationCd(getRandomName());
			user.setLocationName(getRandomName());
			user.setMaidenName(getRandomName());
			user.setMailCode(getRandomName());
			user.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE).get(0).getId());
			user.setMiddleInit(getRandomName(1));
			user.setName(getRandomName());
			user.setNickname(getRandomName());
			user.setPrefix(getRandomName(3));
			user.setSecondaryStatus(UserStatusEnum.ACTIVE);
			user.setSex("M");
			user.setShowInSearch(Integer.valueOf(1));
			user.setStatus(UserStatusEnum.ACTIVE);
			user.setSuffix(getRandomName(3));
			user.setTitle(getRandomName());
			user.setUserTypeInd(getRandomName());
			
			
			final List<Address> addresses = new LinkedList<Address>();
			final Address address = new Address();
			address.setAddress1(getRandomName());
			address.setBldgNumber(getRandomName(2));
			address.setCity(getRandomName());
			address.setCountry(getRandomName());
			address.setDescription(getRandomName());
			address.setIsActive(true);
			address.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.ADDRESS).get(0).getId());
			address.setName(getRandomName());
			address.setPostalCd(getRandomName());
			address.setSuite(getRandomName());
			address.setOperation(AttributeOperationEnum.ADD);
			addresses.add(address);
			user.setAddresses(new HashSet<Address>(addresses));
			
			final List<EmailAddress> emails = new LinkedList<EmailAddress>();
			final EmailAddress email = new EmailAddress();
			email.setDescription(getRandomName());
			email.setIsActive(true);
			email.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.EMAIL).get(0).getId());
			email.setName(getRandomName());
			email.setEmailAddress(getRandomName() + "@" + getRandomName());
			email.setOperation(AttributeOperationEnum.ADD);
			emails.add(email);
			user.setEmailAddresses(new HashSet<EmailAddress>(emails));
			
			final List<Phone> phones = new LinkedList<Phone>();
			final Phone phone = new Phone();
			phone.setAreaCd(getRandomName(3));
			phone.setCountryCd(getRandomName(3));
			phone.setDescription(getRandomName());
			phone.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.PHONE).get(0).getId());
			phone.setName(getRandomName());
			phone.setPhoneExt(getRandomName(3));
			phone.setPhoneNbr(getRandomName());
			phones.add(phone);
			user.setPhones(new HashSet<Phone>(phones));
			
			final List<Login> loginList = new LinkedList<Login>();
			final Login login = new Login();
			login.setManagedSysId("0");
			login.setLogin(getRandomName());
			loginList.add(login);
			user.setPrincipalList(loginList);
			
			final ProvisionUser pUser = new ProvisionUser(user);
			final ProvisionUserResponse pResponse = provisionService.addUser(pUser);
			Assert.assertNotNull(pResponse);
			Assert.assertTrue(pResponse.isSuccess());
		
			try {
				final LoginResponse loginResponse = loginServiceClient.getLoginByManagedSys(login.getLogin(), login.getManagedSysId());
				if(loginResponse == null) {
					throw new RuntimeException(String.format("loginResponse == null.  Login=%s", login.getLogin()));
				}
				if(!loginResponse.isSuccess()) {
					throw new RuntimeException(String.format("loginResponse.isFailure.  Login=%s", login.getLogin()));
				}
				if(loginResponse.getPrincipal() == null) {
					throw new RuntimeException(String.format("loginResponse.getPrincipal == null.  Login=%s", login.getLogin()));
				}
				final Response userResponse = userServiceClient.removeUser(loginResponse.getPrincipal().getUserId());
				if(userResponse == null) {
					throw new RuntimeException(String.format("userResponse == null.  Login=%s", login.getLogin()));
				}
				
				if(!userResponse.isSuccess()) {
					throw new RuntimeException(String.format("!userResponse.isSuccess.  Login=%s", login.getLogin()));
				}
			} catch(Throwable e) {
				log.error("Can't delete user", e);
			}
			Thread.sleep(1000L);
		}
	}
}
