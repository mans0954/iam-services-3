package org.openiam.service.integration.provisioning;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.OrderConstants;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.SortParam;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationUserDTO;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.openiam.idm.srvc.pswd.service.ChallengeResponseWebService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.ProvisionActionEnum;
import org.openiam.provision.dto.ProvisionActionEvent;
import org.openiam.provision.dto.ProvisionActionTypeEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.ActionEventBuilder;
import org.openiam.provision.service.ProvisionServiceEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class UserManagmentServiceTest2 extends AbstractUserManagementServiceTest {

    @Autowired
    @Qualifier("roleServiceClient")
    protected RoleDataWebService roleServiceClient;

    @Autowired
    @Qualifier("challengeResponseServiceClient")
    protected ChallengeResponseWebService challengeResponseServiceClient;

    @Autowired
    @Qualifier("organizationServiceClient")
    private OrganizationDataService organizationServiceClient;

    @Autowired
    @Qualifier("userServiceClient")
    protected UserDataWebService userServiceClient;

    @Autowired
    @Qualifier("loginServiceClient")
    protected LoginDataWebService loginServiceClient;


    protected String ROLE_ID = "1";
    protected String COMPANY_ID = "100";
    protected String SUPERVISOR_ID = "3000";


    @Test(groups = {"CHANGE_STATUS"})
    public void changeStatus() throws Exception {
        User user = doCreate();
        User foundUser = getAndAssert(user.getId());
        Assert.assertNotNull(foundUser.getDefaultLogin());
        user.setStatus(UserStatusEnum.DELETED);
        saveAndAssert(user);
        Thread.sleep(3000);
        foundUser = getAndAssert(user.getId());
        Assert.assertEquals(UserStatusEnum.DELETED, foundUser.getStatus());

        //********************************IDMAPPS-2912************************************************
        final ProvisionUser pUser = new ProvisionUser(user);
        pUser.setRequestorUserId(REQUESTER_ID);
        pUser.setStatus(UserStatusEnum.ACTIVE);
        Response wsResponse = provisionService.modifyUser(pUser);
        Assert.assertTrue(wsResponse.isSuccess());
        Thread.sleep(3000);
        foundUser = getAndAssert(user.getId());
        Assert.assertEquals(UserStatusEnum.ACTIVE, foundUser.getStatus());

        //********************************IDMAPPS-2910************************************************
        wsResponse = provisionService.deleteByUserId(user.getId(), UserStatusEnum.DELETED, REQUESTER_ID);
        Assert.assertTrue(wsResponse.isSuccess());
        Thread.sleep(3000);
        foundUser = getAndAssert(user.getId());
        Assert.assertEquals(UserStatusEnum.DELETED, foundUser.getStatus());

        //********************************IDMAPPS-2925************************************************
        wsResponse = provisionService.disableUser(user.getId(), true, REQUESTER_ID);
        Assert.assertTrue(wsResponse.isSuccess());
        Thread.sleep(3000);
        foundUser = getAndAssert(user.getId());
        Assert.assertEquals(UserStatusEnum.DISABLED, foundUser.getSecondaryStatus());

        //********************************IDMAPPS-2926************************************************
        wsResponse = provisionService.disableUser(user.getId(), false, REQUESTER_ID);
        Assert.assertTrue(wsResponse.isSuccess());
        Thread.sleep(3000);
        foundUser = getAndAssert(user.getId());
        Assert.assertNull(foundUser.getSecondaryStatus());

    }

    // ********************************IDMAPPS-2908************************************************
    // Create a user with a predefined role
    // ********************************************************************************************
    @Test(groups = {"NEW_USER_WITH_ROLE"})
    public void newUserWithRole() throws Exception {
        Role role = roleServiceClient.getRoleLocalized(ROLE_ID, REQUESTER_ID, getDefaultLanguage());
        role.setOperation(AttributeOperationEnum.ADD);
        Assert.assertNotNull(role, "Cann't find role with ID :" + ROLE_ID);

        User user = new User();
        user.setFirstName(getRandomName());
        user.setLastName(getRandomName());
        user.setNotifyUserViaEmail(false);
        user.getRoles().add(role);

        user = ((ProvisionUserResponse) saveAndAssert(user)).getUser();
        pushUserId(user.getId());

        Thread.sleep(5000);

        User foundUser = getAndAssert(user.getId());
        Assert.assertEquals(user.getFirstName(), foundUser.getFirstName());
        Assert.assertEquals(user.getLastName(), foundUser.getLastName());

        Assert.assertNotNull(foundUser.getRoles());
        if (foundUser.getRoles() != null) {
            for (Role rl : foundUser.getRoles())
                Assert.assertEquals(rl.getId(), role.getId());
        }
    }


    // ********************************IDMAPPS-2907************************************************
    // Create a user with a predefined identity
    // ********************************************************************************************
    @Test(groups = {"NEW_USER_WITH_IDENTITY"})
    public void newUserWithIdentity() throws Exception {
        Login login = new Login();
        login.setLogin(getRandomName());
        login.setStatus(LoginStatusEnum.ACTIVE);
        login.setManagedSysId(getDefaultManagedSystemId());

        User user = new User();
        user.setFirstName(getRandomName());
        user.setLastName(getRandomName());
        user.setNotifyUserViaEmail(false);
        user.addPrincipal(login);

        user = ((ProvisionUserResponse) saveAndAssert(user)).getUser();
        pushUserId(user.getId());

        Thread.sleep(5000);

        User foundUser = getAndAssert(user.getId());
        Assert.assertEquals(user.getFirstName(), foundUser.getFirstName());
        Assert.assertEquals(user.getLastName(), foundUser.getLastName());

        Assert.assertNotNull(foundUser.getPrincipalList());
        if (foundUser.getPrincipalList() != null) {
            Login lg = foundUser.getPrincipalList().get(0);
            Assert.assertEquals(lg.getLogin(), login.getLogin());
            Assert.assertEquals(lg.getStatus(), login.getStatus());
            Assert.assertEquals(lg.getManagedSysId(), login.getManagedSysId());
        }
    }


    // ********************************IDMAPPS-2904************************************************
    // Create a user with a predefine password
    // ********************************************************************************************
    @Test(groups = {"NEW_USER_WITH_PASSWORD"})
    public void newUserWithPassword() throws Exception {
        Login login = new Login();
        login.setLogin(getRandomName());
        login.setStatus(LoginStatusEnum.ACTIVE);
        login.setPassword("testPas00");
        login.setManagedSysId(getDefaultManagedSystemId());
        login.setOperation(AttributeOperationEnum.ADD);

        User user = new User();
        user.setFirstName(getRandomName());
        user.setLastName(getRandomName());
        user.setNotifyUserViaEmail(false);
        user.addPrincipal(login);

        user = ((ProvisionUserResponse) saveAndAssert(user)).getUser();
        pushUserId(user.getId());

        Thread.sleep(5000);

        User foundUser = getAndAssert(user.getId());
        Assert.assertEquals(user.getFirstName(), foundUser.getFirstName());
        Assert.assertEquals(user.getLastName(), foundUser.getLastName());

        Assert.assertNotNull(foundUser.getPrincipalList());
        if (foundUser.getPrincipalList() != null) {
            Login lg = foundUser.getPrincipalList().get(0);
            Assert.assertEquals(lg.getLogin(), login.getLogin());
            Assert.assertEquals(loginServiceClient.decryptPassword(user.getId(), lg.getPassword()).getResponseValue(), login.getPassword());
            Assert.assertEquals(lg.getStatus(), login.getStatus());
            Assert.assertEquals(lg.getManagedSysId(), login.getManagedSysId());
        }
    }


    // ********************************IDMAPPS-2929************************************************
    // Reset the challenge questions for the user
    // ********************************************************************************************
    @Test(groups = {"RESET_CHALLENGE_QUESTIONS"})
    public void resetChallengeQuestions() throws Exception {
        User user = doCreate();
        User foundUser = getAndAssert(user.getId());
        Assert.assertNotNull(foundUser.getDefaultLogin());

        List<UserIdentityAnswer> answList = new ArrayList<UserIdentityAnswer>();

        UserIdentityAnswer answ1 = new UserIdentityAnswer();
        answ1.setQuestionId("200");
        answ1.setQuestionAnswer("answer 1");
        answ1.setUserId(user.getId());
        answList.add(answ1);

        UserIdentityAnswer answ2 = new UserIdentityAnswer();
        answ2.setQuestionId("201");
        answ2.setQuestionAnswer("answer 2");
        answ2.setUserId(user.getId());
        answList.add(answ2);

        UserIdentityAnswer answ3 = new UserIdentityAnswer();
        answ3.setQuestionId("202");
        answ3.setQuestionAnswer("answer 3");
        answ3.setUserId(user.getId());
        answList.add(answ3);

        challengeResponseServiceClient.saveAnswers(answList);

        IdentityAnswerSearchBean iasb = new IdentityAnswerSearchBean();
        iasb.setUserId(user.getId());
        iasb.setDeepCopy(false);

        try {
            answList = challengeResponseServiceClient.findAnswerBeans(iasb, null, 0, Integer.MAX_VALUE);
        } catch (Exception e) {
            Assert.assertNull(e, "Cann't save challenge question answer");
        }

        if ((answList != null) && (answList.size() > 0)) {
            challengeResponseServiceClient.resetQuestionsForUser(user.getId());
        } else {
            Assert.assertTrue(false, "Cann't find new Answer for User");
        }

        try {
            answList = challengeResponseServiceClient.findAnswerBeans(iasb, null, 0, Integer.MAX_VALUE);
            Assert.assertNull(answList, "Cann't reset user's Answers");
        } catch (Exception e) {
            Assert.assertNull(e, "Exception when try find answer for User");
        }
    }


    //**********************************************************************************************
    // User in Company
    ///IDMAPPS-2933,2934,2935
    @Test(groups = {"USER_IN_COMPANY"})
    public void userInCompany() throws Exception {
        Organization org = organizationServiceClient.getOrganizationLocalized(COMPANY_ID, REQUESTER_ID, getDefaultLanguage());
        Assert.assertNotNull(org, "Cann't find default ORGANIZATION");

        User user = doCreate();
        User foundUser = getAndAssert(user.getId());

        user.getOrganizationUserDTOs().add(new OrganizationUserDTO(user.getId(), org.getId(), AttributeOperationEnum.ADD));

        saveAndAssert(user);

        Thread.sleep(3000);
        foundUser = getAndAssert(user.getId());
        Assert.assertNotNull(foundUser.getOrganizationUserDTOs());
        if (foundUser.getOrganizationUserDTOs() != null) {
            for (OrganizationUserDTO foundOrg : foundUser.getOrganizationUserDTOs()) {
                Assert.assertEquals(org.getId(), foundOrg.getOrganization().getId());
            }
        }


        final OrganizationSearchBean searchBean = new OrganizationSearchBean();
        searchBean.addUserId(user.getId());
        Integer count = organizationServiceClient.count(searchBean, REQUESTER_ID);
        Assert.assertTrue(count == 1);
        List<Organization> results = organizationServiceClient.findBeansLocalized(searchBean, REQUESTER_ID, 0, 1, getDefaultLanguage());
        Assert.assertNotNull(results);
        if (results != null) {
            Assert.assertEquals(org.getId(), results.get(0).getId());
        }
        OrganizationUserDTO organizationUserDTO = null;
        for (OrganizationUserDTO userDTO : user.getOrganizationUserDTOs()) {
            if (userDTO.getOrganization().getId().equals(org.getId())) {
                organizationUserDTO = userDTO;
                break;
            }
        }

        user.getOrganizationUserDTOs().remove(organizationUserDTO);
        organizationUserDTO.setOperation(AttributeOperationEnum.DELETE);
        user.getOrganizationUserDTOs().add(organizationUserDTO);
        saveAndAssert(user);
        Thread.sleep(3000);

        count = organizationServiceClient.count(searchBean, REQUESTER_ID);
        results = organizationServiceClient.findBeansLocalized(searchBean, REQUESTER_ID, 0, 1, getDefaultLanguage());
        Assert.assertTrue(count == 0);
        Assert.assertNull(results);

    }


    //**********************************************************************************************
    // Supervisors
    //////IDMAPPS-2930, 2931, 2932
    @Test(groups = {"SUPERVISOR"})
    public void supervisorForUser() throws Exception {

        User user = doCreate();
        User foundUser = getAndAssert(user.getId());

        final User superior = userServiceClient.getUserWithDependent(SUPERVISOR_ID, REQUESTER_ID, true);
        user = userServiceClient.getUserWithDependent(user.getId(), REQUESTER_ID, true);

        ProvisionUser pUser = new ProvisionUser(user);
        pUser.setRequestorUserId(REQUESTER_ID);
        List<User> superiors = userServiceClient.getSuperiors(user.getId(), -1, -1);
        superior.setOperation(AttributeOperationEnum.ADD);
        pUser.addSuperior(superior);
        pUser.addSuperior(superior);

        saveAndAssert(pUser);

        superiors = userServiceClient.getSuperiors(user.getId(), 0, 1);
        Integer count = userServiceClient.getSuperiorsCount(user.getId());

        Assert.assertNotNull(superiors);
        if (superiors != null) {
            Assert.assertEquals(superiors.get(0).getId(), superior.getId());
        }
        Assert.assertNotNull(count);
        Assert.assertTrue(count == 1);

        pUser = new ProvisionUser(user);
        pUser.setRequestorUserId(REQUESTER_ID);
        superior.setOperation(AttributeOperationEnum.DELETE);
        pUser.addSuperior(superior);

        saveAndAssert(pUser);

        superiors = userServiceClient.getSuperiors(user.getId(), 0, 1);
        count = userServiceClient.getSuperiorsCount(user.getId());

        Assert.assertNull(superiors);
        Assert.assertTrue(count == 0);
    }


    //**********************************************************************************************
    // USER CONTACT ADDRESS
    //IDMAPPS-2942, 2939, 2937
    @Test(groups = {"CONTACT_ADDRESS"})
    public void userContactAddress() throws Exception {
        User user = doCreate();
        User foundUser = getAndAssert(user.getId());

        Address adr = new Address();
        adr.setName(getRandomName());
        adr.setAddress1(getRandomName());
        adr.setParentId(foundUser.getId());
        adr.setOperation(AttributeOperationEnum.ADD);

        ProvisionUser pUser = new ProvisionUser(user);
        pUser.setRequestorUserId(REQUESTER_ID);
        pUser.getAddresses().add(adr);
        User userResult = ((ProvisionUserResponse) saveAndAssert(pUser)).getUser();


        String adrId = null;
        List<Address> addressList = userServiceClient.getAddressListByPage(userResult.getId(), 1, 0);
        Integer count = userServiceClient.getNumOfAddressesForUser(userResult.getId());
        Assert.assertNotNull(addressList);
        if (addressList != null) {
            Assert.assertEquals(addressList.get(0).getName(), adr.getName());
            Assert.assertEquals(addressList.get(0).getAddress1(), adr.getAddress1());
            adrId = addressList.get(0).getAddressId();
        }
        Assert.assertTrue(count == 1);


        User userDel = userServiceClient.getUserWithDependent(userResult.getId(), REQUESTER_ID, true);
        ProvisionUser pUserDel = new ProvisionUser(userDel);
        pUserDel.setRequestorUserId(REQUESTER_ID);
        Address addr = userServiceClient.getAddressById(adrId);
        for (Address a : pUserDel.getAddresses()) {
            if (addr.getAddressId().equals(a.getAddressId())) {
                a.setOperation(AttributeOperationEnum.DELETE);
            }
        }

        userResult = ((ProvisionUserResponse) saveAndAssert(pUserDel)).getUser();

        addressList = userServiceClient.getAddressListByPage(userResult.getId(), 1, 0);
        count = userServiceClient.getNumOfAddressesForUser(userResult.getId());
        Assert.assertNull(addressList);
        Assert.assertTrue(count == 0);
    }


    //**********************************************************************************************
    // USER CONTACT PHONE
    //IDMAPPS-2938, 2941, 2944
    @Test(groups = {"CONTACT_PHONE"})
    public void userContactPhone() throws Exception {

        User user = doCreate();
        User foundUser = getAndAssert(user.getId());

        Phone ph = new Phone();
        ph.setName(getRandomName());
        ph.setPhoneNbr(getRandomName());
        ph.setAreaCd(getRandomName());
        ph.setCountryCd("123");
        ph.setParentId(foundUser.getId());
        ph.setOperation(AttributeOperationEnum.ADD);

        ProvisionUser pUser = new ProvisionUser(user);
        pUser.setRequestorUserId(REQUESTER_ID);
        pUser.getPhones().add(ph);
        User userResult = ((ProvisionUserResponse) saveAndAssert(pUser)).getUser();


        String phId = null;
        List<Phone> phoneList = userServiceClient.getPhoneListByPage(userResult.getId(), 1, 0);
        Integer count = userServiceClient.getNumOfPhonesForUser(userResult.getId());
        Assert.assertNotNull(phoneList);
        if (phoneList != null) {
            Assert.assertEquals(phoneList.get(0).getName(), ph.getName());
            Assert.assertEquals(phoneList.get(0).getAreaCd(), ph.getAreaCd());
            Assert.assertEquals(phoneList.get(0).getCountryCd(), ph.getCountryCd());
            Assert.assertEquals(phoneList.get(0).getPhoneNbr(), ph.getPhoneNbr());
            phId = phoneList.get(0).getPhoneId();
        }
        Assert.assertTrue(count == 1);


        User userDel = userServiceClient.getUserWithDependent(userResult.getId(), REQUESTER_ID, true);
        ProvisionUser pUserDel = new ProvisionUser(userDel);
        pUserDel.setRequestorUserId(REQUESTER_ID);
        Phone phone = userServiceClient.getPhoneById(phId);
        for (Phone a : pUserDel.getPhones()) {
            if (phone.getPhoneId().equals(a.getPhoneId())) {
                a.setOperation(AttributeOperationEnum.DELETE);
            }
        }

        userResult = ((ProvisionUserResponse) saveAndAssert(pUserDel)).getUser();

        phoneList = userServiceClient.getPhoneListByPage(userResult.getId(), 1, 0);
        count = userServiceClient.getNumOfPhonesForUser(userResult.getId());
        Assert.assertNull(phoneList);
        Assert.assertTrue(count == 0);
    }


    //**********************************************************************************************
    // USER CONTACT EMAIL
    //IDMAPPS-2936, 2940, 2943
    @Test(groups = {"CONTACT_EMAIL"})
    public void userContactEmail() throws Exception {

        User user = doCreate();
        User foundUser = getAndAssert(user.getId());

        EmailAddress em = new EmailAddress();
        em.setName(getRandomName());
        em.setEmailAddress("qwe@asd.net");
        em.setIsActive(true);
        em.setParentId(foundUser.getId());
        em.setOperation(AttributeOperationEnum.ADD);

        ProvisionUser pUser = new ProvisionUser(user);
        pUser.setRequestorUserId(REQUESTER_ID);
        pUser.getEmailAddresses().add(em);
        User userResult = ((ProvisionUserResponse) saveAndAssert(pUser)).getUser();


        String emailId = null;
        List<EmailAddress> emailList = userServiceClient.getEmailAddressListByPage(userResult.getId(), 1, 0);
        Integer count = userServiceClient.getNumOfEmailsForUser(userResult.getId());
        Assert.assertNotNull(emailList);
        if (emailList != null) {
            Assert.assertEquals(emailList.get(0).getName(), em.getName());
            Assert.assertEquals(emailList.get(0).getEmailAddress(), em.getEmailAddress());
            Assert.assertEquals(emailList.get(0).getIsActive(), em.getIsActive());
            emailId = emailList.get(0).getEmailId();
        }
        Assert.assertTrue(count == 1);


        User userDel = userServiceClient.getUserWithDependent(userResult.getId(), REQUESTER_ID, true);
        ProvisionUser pUserDel = new ProvisionUser(userDel);
        pUserDel.setRequestorUserId(REQUESTER_ID);
        EmailAddress phone = userServiceClient.getEmailAddressById(emailId);
        for (EmailAddress a : pUserDel.getEmailAddresses()) {
            if (phone.getEmailId().equals(a.getEmailId())) {
                a.setOperation(AttributeOperationEnum.DELETE);
            }
        }

        userResult = ((ProvisionUserResponse) saveAndAssert(pUserDel)).getUser();

        emailList = userServiceClient.getEmailAddressListByPage(userResult.getId(), 1, 0);
        count = userServiceClient.getNumOfEmailsForUser(userResult.getId());
        Assert.assertNull(emailList);
        Assert.assertTrue(count == 0);

    }
}
