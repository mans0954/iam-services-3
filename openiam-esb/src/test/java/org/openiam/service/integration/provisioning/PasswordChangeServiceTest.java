package org.openiam.service.integration.provisioning;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.policy.dto.*;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.pswd.dto.Password;
import org.openiam.idm.srvc.pswd.dto.PasswordValidationResponse;
import org.openiam.idm.srvc.pswd.service.PasswordGenerator;
import org.openiam.idm.srvc.pswd.ws.PasswordWebService;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.PasswordResponse;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class PasswordChangeServiceTest extends AbstractUserManagementServiceTest {

    @Resource(name = "organizationServiceClient")
    protected OrganizationDataService organizationDataService;

    @Resource(name = "resourceServiceClient")
    protected ResourceDataService resourceDataService;

    @Resource(name = "policyServiceClient")
    protected PolicyDataService policyServiceClient;

    @Resource(name = "provisionServiceClient")
    protected ProvisionService provisionService;

    @Autowired
    @Qualifier("loginServiceClient")
    private LoginDataWebService loginServiceClient;

    @Autowired
    @Qualifier("passwordServiceClient")
    private PasswordWebService passwordWebService;

    private static final String requestorId = "3000";
    private static final String oiamOrgId = "100";
    private static final String managedSysAdId = "110";
    private static final String managedSysDefId = "0";
    private static final String validPassword = "A!b12356";
    private static final String validPassword2 = "C!l654321";
    private static final String invalidPassword = "AABB5";

    public List<String> oiamUserInfo = new ArrayList<>();
    public List<String> adUserInfo = new ArrayList<>();
    public List<String> lockedUserInfo = new ArrayList<>();
    public List<String> deactivatedUserInfo = new ArrayList<>();
    public List<String> policyIDs = new ArrayList<>();

    @BeforeClass(alwaysRun = true)
    public void createUsersAndPolicy() throws Exception{
        oiamUserInfo = createOpeniamUser();
        adUserInfo = createAdUser();
        lockedUserInfo = createOpeniamUserLocked();
        deactivatedUserInfo = createOpeniamUserDeactivated();
        policyIDs = createPolicy();
    }

    @AfterClass(alwaysRun = true)
    public void doIt() throws Exception{
        returnDefaultPolicyAssocc();
        provisionService.deleteByUserId(oiamUserInfo.get(0), UserStatusEnum.REMOVE, requestorId);
        provisionService.deleteByUserId(adUserInfo.get(0), UserStatusEnum.REMOVE, requestorId);
        provisionService.deleteByUserId(lockedUserInfo.get(0), UserStatusEnum.REMOVE, requestorId);
        provisionService.deleteByUserId(deactivatedUserInfo.get(0), UserStatusEnum.REMOVE, requestorId);
    }

    @Test
    public void changePasswordValidOIAMUser() throws Exception {
        PasswordSync passwordSync = new PasswordSync();
        passwordSync.setUserId(oiamUserInfo.get(0));
        passwordSync.setPassword(validPassword);
        passwordSync.setRequestorId(requestorId);
        passwordSync.setManagedSystemId(managedSysDefId);
        passwordSync.setSendPasswordToUser(false);

        PasswordValidationResponse response = provisionService.setPassword(passwordSync);

        User user = get(oiamUserInfo.get(0));

        String passDb = null;
        for (Login l : user.getPrincipalList()) {
            if (!l.getPassword().isEmpty()){
                passDb = l.getPassword();
            }
        }

        Response responsePwd = loginServiceClient.decryptPassword(passwordSync.getUserId(), passDb);
        String password = (String)responsePwd.getResponseValue();

        Assert.assertEquals(passwordSync.getPassword(), password);

        Assert.assertTrue(response.isSuccess());
    }

    @Test(priority = 1)
    public void changePasswordInvalidOIAMUser() throws Exception {
        PasswordSync passwordSync = new PasswordSync();
        passwordSync.setUserId(oiamUserInfo.get(0));
        passwordSync.setPassword(invalidPassword);
        passwordSync.setRequestorId(requestorId);
        passwordSync.setManagedSystemId(managedSysDefId);
        passwordSync.setSendPasswordToUser(false);

        PasswordValidationResponse response = provisionService.setPassword(passwordSync);


        User user = get(oiamUserInfo.get(0));

        String passDb = null;
        for (Login l : user.getPrincipalList()) {
            if (!l.getPassword().isEmpty()){
                passDb = l.getPassword();
            }
        }

        Response responsePwd = loginServiceClient.decryptPassword(passwordSync.getUserId(), passDb);
        String password = (String)responsePwd.getResponseValue();

        Assert.assertTrue(response.isFailure());
        Assert.assertNotEquals(passwordSync.getPassword(), password);
    }

    @Test(priority = 2)
    public void resetPasswordValidOIAMUser() throws Exception {
        PasswordSync passwordSync = new PasswordSync();
        passwordSync.setUserId(oiamUserInfo.get(0));
        passwordSync.setPassword(validPassword2);
        passwordSync.setRequestorId(requestorId);
        passwordSync.setManagedSystemId(managedSysDefId);
        passwordSync.setSendPasswordToUser(false);

        PasswordResponse passwordResponse = provisionService.resetPassword(passwordSync);
        Assert.assertTrue(passwordResponse.isSuccess());

        User user = get(oiamUserInfo.get(0));

        String passDb = null;
        for (Login l : user.getPrincipalList()) {
            if (!l.getPassword().isEmpty()){
                passDb = l.getPassword();
            }
        }

        Response responsePwd = loginServiceClient.decryptPassword(passwordSync.getUserId(), passDb);
        String password = (String)responsePwd.getResponseValue();
        Assert.assertEquals(passwordSync.getPassword(), password);

        Password pswdVal = new Password();
        pswdVal.setPassword(validPassword2);
        pswdVal.setPrincipal(oiamUserInfo.get(1));
        pswdVal.setManagedSysId(managedSysDefId);

        PasswordValidationResponse response =  passwordWebService.isPasswordValid(pswdVal);
        Assert.assertTrue(response.isSuccess());
    }

    @Test(priority = 3)
    public void resetPasswordAutoGenerateOIAMUser() throws Exception {
        Policy pp = passwordWebService.getPasswordPolicy(oiamUserInfo.get(1), null);

        String autoGenerPass = PasswordGenerator.generatePassword(pp);

        PasswordSync passwordSync = new PasswordSync();
        passwordSync.setUserId(oiamUserInfo.get(0));
        passwordSync.setPassword(autoGenerPass);
        passwordSync.setRequestorId(requestorId);
        passwordSync.setManagedSystemId(managedSysDefId);
        passwordSync.setSendPasswordToUser(false);

        PasswordResponse passwordResponse = provisionService.resetPassword(passwordSync);
        Assert.assertTrue(passwordResponse.isSuccess());

        User user = get(oiamUserInfo.get(0));

        String passDb = null;
        for (Login l : user.getPrincipalList()) {
            if (!l.getPassword().isEmpty()){
                passDb = l.getPassword();
            }
        }

        Response responsePwd = loginServiceClient.decryptPassword(passwordSync.getUserId(), passDb);
        String password = (String)responsePwd.getResponseValue();
        Assert.assertEquals(passwordSync.getPassword(), password);

        Password pswdVal = new Password();
        pswdVal.setPassword(password);
        pswdVal.setPrincipal(oiamUserInfo.get(1));
        pswdVal.setManagedSysId(managedSysDefId);

        PasswordValidationResponse response =  passwordWebService.isPasswordValid(pswdVal);
        Assert.assertTrue(response.isSuccess());
    }

    @Test(priority = 4)
    public void resetPasswordADandIAM() throws Exception {
        PasswordSync passwordSync = new PasswordSync();
        passwordSync.setUserId(adUserInfo.get(0));
        passwordSync.setPassword(validPassword);
        passwordSync.setRequestorId(requestorId);
        //passwordSync.setManagedSystemId(all MngSys);
        passwordSync.setSendPasswordToUser(false);

        PasswordResponse passwordResponse = provisionService.resetPassword(passwordSync);

        User user = get(adUserInfo.get(0));

        String loginOiam = null;
        String passDbOiam = null;
        String loginAd = null;
        String passDbAd = null;
        for (Login l : user.getPrincipalList()) {
            if (l.getManagedSysId().equals(managedSysDefId)){
                loginOiam = l.getLogin();
                passDbOiam = l.getPassword();
            } else if (l.getManagedSysId().equals(managedSysAdId)){
                loginAd = l.getLogin();
                passDbAd = l.getPassword();
            }
        }

        Response responsePwd = loginServiceClient.decryptPassword(passwordSync.getUserId(), passDbOiam);
        Response responsePwd1 = loginServiceClient.decryptPassword(passwordSync.getUserId(), passDbAd);
        String password = (String)responsePwd.getResponseValue();
        String password1 = (String)responsePwd1.getResponseValue();
        Assert.assertEquals(password, password1);
        Assert.assertEquals(passwordSync.getPassword(), password);
        Assert.assertEquals(passwordSync.getPassword(), password1);

        Password pswdVal = new Password();
        pswdVal.setPassword(password);
        pswdVal.setPrincipal(loginOiam);
        pswdVal.setManagedSysId(managedSysDefId);
        PasswordValidationResponse response =  passwordWebService.isPasswordValid(pswdVal);

        Password pswdVal1 = new Password();
        pswdVal1.setPassword(password1);
        pswdVal1.setPrincipal(loginAd);
        pswdVal1.setManagedSysId(managedSysAdId);
        PasswordValidationResponse response1 = passwordWebService.isPasswordValid(pswdVal1);

        Assert.assertTrue(response.isSuccess());
        Assert.assertTrue(response1.isSuccess());
    }

    @Test(priority = 5)
    public void resyncPasswordADandIAM() throws Exception {
        PasswordSync passwordSync = new PasswordSync();
        passwordSync.setUserId(adUserInfo.get(0));
        //passwordSync.setPassword(validPassword2);
        passwordSync.setRequestorId(requestorId);
        passwordSync.setManagedSystemId(managedSysAdId);
        passwordSync.setResyncMode(true);
        passwordSync.setSendPasswordToUser(false);

        PasswordValidationResponse response = provisionService.setPassword(passwordSync);
        Assert.assertTrue(response.isSuccess());

        User user = get(adUserInfo.get(0));

        String passDbOiam = null;
        String passDbAd = null;
        for (Login l : user.getPrincipalList()) {
            if (l.getManagedSysId().equals(managedSysDefId)){
                passDbOiam = l.getPassword();
            } else if (l.getManagedSysId().equals(managedSysAdId)){
                passDbAd = l.getPassword();
            }
        }

        Response responsePwd = loginServiceClient.decryptPassword(passwordSync.getUserId(), passDbOiam);
        Response responsePwd1 = loginServiceClient.decryptPassword(passwordSync.getUserId(), passDbAd);
        String password = (String)responsePwd.getResponseValue();
        String password1 = (String)responsePwd1.getResponseValue();
        Assert.assertEquals(password, password1);
        Assert.assertEquals(passwordSync.getPassword(), password);
        Assert.assertEquals(passwordSync.getPassword(), password1);

    }
    @Test(priority = 6)
    public void changePasswordInADnotIAM() throws Exception {
        PasswordSync passwordSync = new PasswordSync();
        passwordSync.setUserId(adUserInfo.get(0));
        passwordSync.setPassword(validPassword2);
        passwordSync.setRequestorId(requestorId);
        passwordSync.setManagedSystemId(managedSysAdId);
        passwordSync.setSendPasswordToUser(false);

        PasswordValidationResponse passwordResponse = provisionService.setPassword(passwordSync);
        Assert.assertTrue(passwordResponse.isSuccess());

        User user = get(adUserInfo.get(0));

        String passDbOiam = null;
        String passDbAd = null;
        for (Login l : user.getPrincipalList()) {
            if (l.getManagedSysId().equals(managedSysDefId)){
                passDbOiam = l.getPassword();
            } else if (l.getManagedSysId().equals(managedSysAdId)){
                passDbAd = l.getPassword();
            }
        }

        Response responsePwd = loginServiceClient.decryptPassword(passwordSync.getUserId(), passDbOiam);
        Response responsePwd1 = loginServiceClient.decryptPassword(passwordSync.getUserId(), passDbAd);
        String password = (String)responsePwd.getResponseValue();
        String password1 = (String)responsePwd1.getResponseValue();
        Assert.assertNotEquals(password, password1);
        Assert.assertNotEquals(passwordSync.getPassword(), password);
        Assert.assertEquals(passwordSync.getPassword(), password1);
    }

    @Test(priority = 7)
    public void resetPasswordLockedUser() throws Exception {
        PasswordSync passwordSync = new PasswordSync();
        passwordSync.setUserId(lockedUserInfo.get(0));
        passwordSync.setPassword(validPassword);
        passwordSync.setRequestorId(requestorId);
        passwordSync.setManagedSystemId(managedSysDefId);
        passwordSync.setSendPasswordToUser(false);
        passwordSync.setUserActivateFlag(true);

        PasswordResponse passwordResponse = provisionService.resetPassword(passwordSync);
        Assert.assertTrue(passwordResponse.isSuccess());

        User user = get(lockedUserInfo.get(0));

        String passDb = null;
        for (Login l : user.getPrincipalList()) {
            if (!l.getPassword().isEmpty()){
                passDb = l.getPassword();
            }
        }

        Response responsePwd = loginServiceClient.decryptPassword(passwordSync.getUserId(), passDb);
        String password = (String)responsePwd.getResponseValue();
        Assert.assertEquals(passwordSync.getPassword(), password);
        Assert.assertEquals(null, user.getSecondaryStatus());


        Password pswdVal = new Password();
        pswdVal.setPassword(password);
        pswdVal.setPrincipal(lockedUserInfo.get(1));
        pswdVal.setManagedSysId(managedSysDefId);

        PasswordValidationResponse response =  passwordWebService.isPasswordValid(pswdVal);
        Assert.assertTrue(response.isSuccess());
    }

    @Test(priority = 8)
    public void resetPasswordDeactivatedUser() throws Exception {
        PasswordSync passwordSync = new PasswordSync();
        passwordSync.setUserId(deactivatedUserInfo.get(0));
        passwordSync.setPassword(validPassword);
        passwordSync.setRequestorId(requestorId);
        passwordSync.setManagedSystemId(managedSysDefId);
        passwordSync.setSendPasswordToUser(false);


        PasswordResponse passwordResponse = provisionService.resetPassword(passwordSync);
        Assert.assertTrue(passwordResponse.isFailure());

        User user = get(deactivatedUserInfo.get(0));

        String passDb = null;
        for (Login l : user.getPrincipalList()) {
            if (!l.getPassword().isEmpty()){
                passDb = l.getPassword();
            }
        }

        Response responsePwd = loginServiceClient.decryptPassword(passwordSync.getUserId(), passDb);
        String password = (String)responsePwd.getResponseValue();
        Assert.assertNotEquals(passwordSync.getPassword(), password);

        Password pswdVal = new Password();
        pswdVal.setPassword(password);
        pswdVal.setPrincipal(deactivatedUserInfo.get(1));
        pswdVal.setManagedSysId(managedSysDefId);

        PasswordValidationResponse response =  passwordWebService.isPasswordValid(pswdVal);
        Assert.assertTrue(response.isSuccess());

    }

    private void returnDefaultPolicyAssocc() throws Exception {

        policyServiceClient.deletePolicy(policyIDs.get(0));

        List<PolicyObjectAssoc> policyDefaultAssoc = policyServiceClient.getAssociationsForPolicy(policyIDs.get(1));
        PolicyObjectAssoc changeAssocPolicyDef = new PolicyObjectAssoc();

        for (PolicyObjectAssoc poa : policyDefaultAssoc){
            if (poa.getAssociationLevel().equals("TEST-TEST") && poa.getAssociationValue().equals("TEST-TEST")) {
                changeAssocPolicyDef = poa;
            }
        }

        changeAssocPolicyDef.setAssociationLevel("GLOBAL");
        changeAssocPolicyDef.setAssociationValue("GLOBAL");
        policyServiceClient.savePolicyAssoc(changeAssocPolicyDef);
    }

    private List<String> createPolicy() throws Exception {
        Set<PolicyAttribute> policyAttribute = new HashSet<>();
        //PolicyAttribute attribute = new PolicyAttribute();
        List<PolicyAttribute> l = new ArrayList<>();

        final List<PolicyDefParam> allDefaultPolicies = policyServiceClient.getAllPolicyAttributes(PolicyConstants.PASSWORD_POLICY, null);

        for (PolicyDefParam param : allDefaultPolicies) {
            if (param.getDefParamId().equals("112")) {
                PolicyAttribute attribute = new PolicyAttribute();
                attribute.setDefParamId(param.getDefParamId());
                attribute.setName(param.getName());
                attribute.setDescription(param.getDescription());
                attribute.setGrouping(param.getParamGroup());
                attribute.setValue1("6");
                attribute.setValue2("12");
                policyAttribute.add(attribute);
            }
            if (param.getDefParamId().equals("110")){
                PolicyAttribute attribute = new PolicyAttribute();
                attribute.setDefParamId(param.getDefParamId());
                attribute.setName(param.getName());
                attribute.setDescription(param.getDescription());
                attribute.setGrouping(param.getParamGroup());
                attribute.setValue1("6");
                policyAttribute.add(attribute);
            }
            if (param.getDefParamId().equals("114")){
                PolicyAttribute attribute = new PolicyAttribute();
                attribute.setDefParamId(param.getDefParamId());
                attribute.setName(param.getName());
                attribute.setDescription(param.getDescription());
                attribute.setGrouping(param.getParamGroup());
                attribute.setValue1("1");
                attribute.setValue2("1");
                policyAttribute.add(attribute);
            }
            if (param.getDefParamId().equals("115")){
                PolicyAttribute attribute = new PolicyAttribute();
                attribute.setDefParamId(param.getDefParamId());
                attribute.setName(param.getName());
                attribute.setDescription(param.getDescription());
                attribute.setGrouping(param.getParamGroup());
                attribute.setValue1("1");
                attribute.setValue2("1");
                policyAttribute.add(attribute);
            }
            if (param.getDefParamId().equals("116")){
                PolicyAttribute attribute = new PolicyAttribute();
                attribute.setDefParamId(param.getDefParamId());
                attribute.setName(param.getName());
                attribute.setDescription(param.getDescription());
                attribute.setGrouping(param.getParamGroup());
                attribute.setValue1("1");
                attribute.setValue2("1");
                policyAttribute.add(attribute);
            }
            if (param.getDefParamId().equals("130")){
                PolicyAttribute attribute = new PolicyAttribute();
                attribute.setDefParamId(param.getDefParamId());
                attribute.setName(param.getName());
                attribute.setDescription(param.getDescription());
                attribute.setGrouping(param.getParamGroup());
                attribute.setValue1("4");
                policyAttribute.add(attribute);
            }
            if (param.getDefParamId().equals("171")){
                PolicyAttribute attribute = new PolicyAttribute();
                attribute.setDefParamId(param.getDefParamId());
                attribute.setName(param.getName());
                attribute.setDescription(param.getDescription());
                attribute.setGrouping(param.getParamGroup());
                attribute.setValue1("3");
                policyAttribute.add(attribute);
            }
            //--------------------------------------------------
            if (!param.getDefParamId().equals("112") && !param.getDefParamId().equals("110")
                    && !param.getDefParamId().equals("114") && !param.getDefParamId().equals("115")
                    && !param.getDefParamId().equals("116") && !param.getDefParamId().equals("130")
                    && !param.getDefParamId().equals("171")) {
                PolicyAttribute attribute = new PolicyAttribute();
                attribute.setDefParamId(param.getDefParamId());
                attribute.setName(param.getName());
                attribute.setDescription(param.getDescription());
                attribute.setGrouping(param.getParamGroup());
                attribute.setRequired(false);
                policyAttribute.add(attribute);
            }
        }

        Policy policy = new Policy();
        policy.setName(getRandomName());
        policy.setDescription("Test Password Policy");
        policy.setStatus(1);
        policy.setPolicyDefId(PolicyConstants.PASSWORD_POLICY);
        policy.setPolicyAttributes(policyAttribute);

        Response response = policyServiceClient.savePolicy(policy);
        String newPolicyId = (String)response.getResponseValue();

        Policy defaultGlobalPolicy = passwordWebService.getPasswordPolicy("", null);
        List<PolicyObjectAssoc> policyObjectAssocList = policyServiceClient.getAssociationsForPolicy(defaultGlobalPolicy.getPolicyId());
        PolicyObjectAssoc changeAssoc = new PolicyObjectAssoc();

        for (PolicyObjectAssoc pob : policyObjectAssocList){
            if (pob.getAssociationLevel().equals("GLOBAL") && pob.getAssociationValue().equals("GLOBAL")) {
                changeAssoc = pob;
            }
        }

        changeAssoc.setAssociationLevel("TEST-TEST");
        changeAssoc.setAssociationValue("TEST-TEST");
        policyServiceClient.savePolicyAssoc(changeAssoc);

        PolicyObjectAssoc policyObjectAssoc = new PolicyObjectAssoc();
        policyObjectAssoc.setAssociationLevel("GLOBAL");
        policyObjectAssoc.setAssociationValue("GLOBAL");
        policyObjectAssoc.setPolicyId(newPolicyId);
        policyServiceClient.savePolicyAssoc(policyObjectAssoc);

        List<String> policyIds = new ArrayList<>();
        policyIds.add(newPolicyId);
        policyIds.add(defaultGlobalPolicy.getPolicyId());

        return policyIds;
    }

    private List<String> createOpeniamUser() throws Exception {
        ProvisionUser user = new ProvisionUser();
        user.setFirstName(getRandomName());
        user.setLastName(getRandomName());
        user.setLogin(getRandomName());
        user.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE).get(0).getId());
        user.setStatus(UserStatusEnum.ACTIVE);
        final org.openiam.idm.srvc.res.dto.Resource resource = resourceDataService.getResource(managedSysDefId, null);
        resource.setOperation(AttributeOperationEnum.ADD);
        user.getResources().add(resource);

        ProvisionUser userResp = provisionService.addUser(user).getUser();
        String userId = userResp.getId();

        String login = null;
        for (Login l : userResp.getPrincipalList()) {
            login = l.getLogin();
        }

        List<String> userInfo = new ArrayList<>();
        userInfo.add(userId);
        userInfo.add(login);

        return userInfo;
    }

    private List<String> createAdUser() throws Exception {
        ProvisionUser user = new ProvisionUser();
        user.setFirstName(getRandomName());
        user.setLastName(getRandomName());
        user.setLogin(getRandomName());
        user.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE).get(0).getId());
        user.setStatus(UserStatusEnum.ACTIVE);
        List<String> mngSys = new ArrayList<>();
        mngSys.add(managedSysDefId);
        mngSys.add(managedSysAdId);
        final List<org.openiam.idm.srvc.res.dto.Resource> resource = resourceDataService.getResourcesByIds(mngSys, null);
        resource.get(0).setOperation(AttributeOperationEnum.ADD);
        resource.get(1).setOperation(AttributeOperationEnum.ADD);
        user.getResources().add(resource.get(0));
        user.getResources().add(resource.get(1));

        ProvisionUser userResp = provisionService.addUser(user).getUser();
        String userId = userResp.getId();

        String login = null;
        for (Login l : userResp.getPrincipalList()) {
            login = l.getLogin();
        }

        List<String> userInfo = new ArrayList<>();
        userInfo.add(userId);
        userInfo.add(login);

        return userInfo;
    }

    private List<String> createOpeniamUserLocked() throws Exception {
        ProvisionUser user = new ProvisionUser();
        user.setFirstName(getRandomName());
        user.setLastName(getRandomName());
        user.setLogin(getRandomName());
        user.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE).get(0).getId());
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setSecondaryStatus(UserStatusEnum.LOCKED);
        final org.openiam.idm.srvc.res.dto.Resource resource = resourceDataService.getResource(managedSysDefId, null);
        resource.setOperation(AttributeOperationEnum.ADD);
        user.getResources().add(resource);

        ProvisionUser userResp = provisionService.addUser(user).getUser();
        String userId = userResp.getId();

        String login = null;
        for (Login l : userResp.getPrincipalList()) {
            login = l.getLogin();
        }

        List<String> userInfo = new ArrayList<>();
        userInfo.add(userId);
        userInfo.add(login);

        return userInfo;
    }

    private List<String> createOpeniamUserDeactivated() throws Exception {
        ProvisionUser user = new ProvisionUser();
        user.setFirstName(getRandomName());
        user.setLastName(getRandomName());
        user.setLogin(getRandomName());
        user.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE).get(0).getId());
        user.setStatus(UserStatusEnum.DELETED);
        final org.openiam.idm.srvc.res.dto.Resource resource = resourceDataService.getResource(managedSysDefId, null);
        resource.setOperation(AttributeOperationEnum.ADD);
        user.getResources().add(resource);

        ProvisionUser userResp = provisionService.addUser(user).getUser();
        String userId = userResp.getId();

        String login = null;
        for (Login l : userResp.getPrincipalList()) {
            login = l.getLogin();
        }

        List<String> userInfo = new ArrayList<>();
        userInfo.add(userId);
        userInfo.add(login);

        return userInfo;
    }

    /*private List<Organization> searchOrg() throws Exception {
        List<Organization> orgs = new ArrayList<Organization>();
        OrganizationSearchBean searchBean = new OrganizationSearchBean();
        searchBean.setKey(oiamOrgId);
        orgs = organizationDataService.findBeansLocalized(searchBean, null, -1, -1, null);
        return orgs;
    }

    if (CollectionUtils.isNotEmpty(searchOrg())){
            for (Organization o : searchOrg()){
                o.setOperation(AttributeOperationEnum.ADD);
                user.getAffiliations().add(o);
            }
        }

    */

}
