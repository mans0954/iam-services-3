package org.openiam.provisioning;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;
import org.openiam.api.language.LanguageWebService;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.lang.dto.Language;
//import org.openiam.idm.srvc.lang.service.LanguageWebService;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.ws.MetadataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-integration-environment.xml", "classpath:test-esb-integration.xml"})
public class UserProvisionServiceTest extends AbstractTestNGSpringContextTests {

    private static final Log log = LogFactory.getLog(UserProvisionServiceTest.class);

    private ProvisionUser provisionUser = null;

    @Autowired
    @Qualifier("provisionServiceClient")
    private ProvisionService provisionService;

    @Autowired
    @Qualifier("languageServiceClient")
    protected LanguageWebService languageServiceClient;

    @Autowired
    @Qualifier("metadataServiceClient")
    protected MetadataWebService metadataServiceClient;

    private List<String> userIDsForDelete = new LinkedList<String>();
    private List<ProvisionUser> testUsers = new LinkedList<ProvisionUser>();

    @BeforeClass(alwaysRun = true)
    private void initTestProvisionUser() {
        for(int i =0; i<1; i++) {
            User user = new User();
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

            ProvisionUser testProvisionUser1 = new ProvisionUser(user);
            testProvisionUser1.setOperation(AttributeOperationEnum.ADD);
            testUsers.add(testProvisionUser1);
        }
    }

    @AfterClass(alwaysRun = true)
    public void _destroy() {
        if(userIDsForDelete != null && userIDsForDelete.size() > 0) {
            for(String userID : userIDsForDelete) {
                try {
                    provisionService.deleteByUserId(userID,UserStatusEnum.REMOVE, null);
                } catch (Throwable t) {
                    // do nothing
                }
            }
        }
    }

    @Test
    public void testAddNewUser() throws Exception {
        for(ProvisionUser pu : testUsers) {
            try {
                ProvisionUserResponse userResponse = provisionService.addUser(pu);
                // TODO
                Assert.assertNotNull(userResponse);
                Assert.assertTrue(userResponse.isSuccess());
                Assert.assertNotNull(userResponse.getUser());
                Assert.assertNotNull(userResponse.getUser().getId());
                userIDsForDelete.add(userResponse.getUser().getId());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }





    private String getRandomName() {
        return getRandomName(5);
    }

    private String getRandomName(final int count) {
        return RandomStringUtils.randomAlphanumeric(count);
    }

    protected Language getDefaultLanguage() {
        final LanguageSearchBean searchBean = new LanguageSearchBean();
        searchBean.setKey("1");
        return languageServiceClient.findBeans(searchBean, 0, 1, null).get(0);
    }

    protected List<MetadataType> getMetadataTypesByGrouping(final MetadataTypeGrouping grouping) {
        final MetadataTypeSearchBean searchBean = new MetadataTypeSearchBean();
        searchBean.setGrouping(grouping);
        searchBean.setActive(true);
        final List<MetadataType> types = metadataServiceClient.findTypeBeans(searchBean, 0, Integer.MAX_VALUE, getDefaultLanguage());
        return types;
    }
}
