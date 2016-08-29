/*

DUPLICATE TEST
package org.openiam.service.integration.provisioning;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.srvc.user.LoginDataWebService;
import org.openiam.base.response.LoginResponse;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.srvc.am.GroupDataWebService;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationUserDTO;
import org.openiam.srvc.am.OrganizationDataService;
import org.openiam.idm.srvc.provision.NewUserModelToProvisionConverter;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.srvc.am.RoleDataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.base.response.ProvisionUserResponse;
import org.openiam.srvc.idm.ProvisionService;
import org.openiam.service.integration.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProvisionServiceTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("provisionServiceClient")
	private ProvisionService provisionService;
	
	@Autowired
	@Qualifier("loginServiceClient")
	private LoginDataWebService loginServiceClient;
	
	@Autowired
	@Qualifier("roleServiceClient")
	protected RoleDataWebService roleServiceClient;
	
    @Autowired
    @Qualifier("groupServiceClient")
    private GroupDataWebService groupServiceClient;
    
	@Autowired
	@Qualifier("organizationServiceClient")
	private OrganizationDataService organizationServiceClient;
	
	@Test(threadPoolSize = 1, invocationCount = 6000)
	public void stressTestForAkzo() throws Exception {
		final ProvisionUser pUser = getPUser();
		final ProvisionUserResponse pResponse = provisionService.addUser(pUser);
		Assert.assertNotNull(pResponse);
		Assert.assertTrue(pResponse.isSuccess());
	}
	
	@Test
	public void testIDMAPPS2488() throws Exception {
		for(int i = 0; i < 500; i++) {
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
		
			final LoginResponse loginResponse = loginServiceClient.getLoginByManagedSys(login.getLogin(), login.getManagedSysId());
			Assert.assertNotNull(loginResponse);
			Assert.assertTrue(loginResponse.isSuccess());
			Assert.assertNotNull(loginResponse.getPrincipal());
			final Response userResponse = userServiceClient.removeUser(loginResponse.getPrincipal().getUserId());
			Assert.assertNotNull(userResponse);
			Assert.assertTrue(userResponse.isSuccess());


		}
	}
}
*/
