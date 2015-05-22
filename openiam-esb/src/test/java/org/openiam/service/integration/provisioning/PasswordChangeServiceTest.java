/*  enable=false
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
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

*/
/**
 * Created by anton on 17.05.15.
 *//*

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

    private static final String requestorId = "3000";
    private static final String oiamOrgId = "100";

    public String oiamUserId = null;
    public String adUserId = null;
    public String lockedUserId = null;
    public String deactiveUserId = null;

    @Test(priority = 1)
    public void changePasswordValidOIAMUser() throws Exception {
        PasswordSync passwordSync = new PasswordSync();
        passwordSync.setUserId(oiamUserId);
        passwordSync.setPassword("tht");
        passwordSync.setRequestorId(requestorId);
        passwordSync.setSendPasswordToUser(false);

        provisionService.resetPassword(passwordSync);

        User user = get(oiamUserId);

        String passDb = null;
        for (Login l : user.getPrincipalList()) {
            if (!l.getPassword().isEmpty()){
                passDb = l.getPassword();
            }
        }

        Response responsePwd = loginServiceClient.decryptPassword(passwordSync.getUserId(), passDb);
        String password = (String)responsePwd.getResponseValue();

        Assert.assertEquals(passwordSync.getPassword(), password);

    }


    @Test
    public void createUsers() throws Exception{
        oiamUserId = createOpeniamUser();
        adUserId = createAdUser();
        lockedUserId = createOpeniamUserLocked();
        deactiveUserId = createOpeniamUserDeactive();
    }

    //@Test
    public void createPolicy() throws Exception {
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

        }

        Policy policy = new Policy();
        policy.setName(getRandomName());
        policy.setDescription(getRandomName());
        policy.setStatus(1);
        policy.setPolicyDefId(PolicyConstants.PASSWORD_POLICY);
        policy.setPolicyAttributes(policyAttribute);


        Response response = policyServiceClient.savePolicy(policy);
        String policyId = (String)response.getResponseValue();

        PolicyObjectAssoc policyObjectAssoc = new PolicyObjectAssoc();
        policyObjectAssoc.setAssociationLevel("GLOBAL");
        policyObjectAssoc.setAssociationValue("GLOBAL");
        policyObjectAssoc.setPolicyId(policyId);
        policyServiceClient.savePolicyAssoc(policyObjectAssoc);
    }

    private String createOpeniamUser() throws Exception {
        ProvisionUser user = new ProvisionUser();
        user.setFirstName("OIAM Just");
        user.setLastName("User");
        user.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE).get(0).getId());

        if (CollectionUtils.isNotEmpty(searchOrg())){
            for (Organization o : searchOrg()){
                o.setOperation(AttributeOperationEnum.ADD);
                user.getAffiliations().add(o);
            }
        }

        ProvisionUser userResp = provisionService.addUser(user).getUser();
        String userId = userResp.getId();

        Assert.assertEquals(user.getFirstName(), userResp.getFirstName());
        Assert.assertEquals(user.getLastName(), userResp.getLastName());
        Assert.assertEquals(user.getMdTypeId(), userResp.getMdTypeId());
        Assert.assertEquals(user.getAffiliations(), userResp.getAffiliations());

        return userId;
    }

    private String createAdUser() throws Exception {
        ProvisionUser user = new ProvisionUser();
        user.setFirstName("Only AD");
        user.setLastName("User");
        user.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE).get(0).getId());
        final org.openiam.idm.srvc.res.dto.Resource resource = resourceDataService.getResource("110", null);
        resource.setOperation(AttributeOperationEnum.ADD);
        user.getResources().add(resource);

        if (CollectionUtils.isNotEmpty(searchOrg())){
            for (Organization o : searchOrg()){
                o.setOperation(AttributeOperationEnum.ADD);
                user.getAffiliations().add(o);
            }
        }

        ProvisionUser userResp = provisionService.addUser(user).getUser();
        String userId = userResp.getId();

        Assert.assertEquals(user.getFirstName(), userResp.getFirstName());
        Assert.assertEquals(user.getLastName(), userResp.getLastName());
        Assert.assertEquals(user.getMdTypeId(), userResp.getMdTypeId());
        Assert.assertEquals(user.getStatus(), userResp.getStatus());
        Assert.assertEquals(user.getSecondaryStatus(), userResp.getSecondaryStatus());
        Assert.assertEquals(user.getResources(), userResp.getResources());

        return userId;
    }

    private String createOpeniamUserLocked() throws Exception {
        ProvisionUser user = new ProvisionUser();
        user.setFirstName("OIAM Locked");
        user.setLastName("User");
        user.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE).get(0).getId());
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setSecondaryStatus(UserStatusEnum.LOCKED);

        if (CollectionUtils.isNotEmpty(searchOrg())){
            for (Organization o : searchOrg()){
                o.setOperation(AttributeOperationEnum.ADD);
                user.getAffiliations().add(o);
            }
        }

        ProvisionUser userResp = provisionService.addUser(user).getUser();
        String userId = userResp.getId();

        Assert.assertEquals(user.getFirstName(), userResp.getFirstName());
        Assert.assertEquals(user.getLastName(), userResp.getLastName());
        Assert.assertEquals(user.getMdTypeId(), userResp.getMdTypeId());
        Assert.assertEquals(user.getStatus(), userResp.getStatus());
        Assert.assertEquals(user.getSecondaryStatus(), userResp.getSecondaryStatus());
        Assert.assertEquals(user.getAffiliations(), userResp.getAffiliations());

        return userId;
    }

    private String createOpeniamUserDeactive() throws Exception {
        ProvisionUser user = new ProvisionUser();
        user.setFirstName("OIAM Deactive");
        user.setLastName("User");
        user.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE).get(0).getId());
        user.setStatus(UserStatusEnum.DELETED);
        user.setSecondaryStatus(UserStatusEnum.INACTIVE);

        if (CollectionUtils.isNotEmpty(searchOrg())){
            for (Organization o : searchOrg()){
                o.setOperation(AttributeOperationEnum.ADD);
                user.getAffiliations().add(o);
            }
        }

        ProvisionUser userResp = provisionService.addUser(user).getUser();
        String userId = userResp.getId();

        Assert.assertEquals(user.getFirstName(), userResp.getFirstName());
        Assert.assertEquals(user.getLastName(), userResp.getLastName());
        Assert.assertEquals(user.getMdTypeId(), userResp.getMdTypeId());
        Assert.assertEquals(user.getStatus(), userResp.getStatus());
        Assert.assertEquals(user.getSecondaryStatus(), userResp.getSecondaryStatus());
        Assert.assertEquals(user.getAffiliations(), userResp.getAffiliations());

        return userId;
    }

    private List<Organization> searchOrg() throws Exception {
        List<Organization> orgs = new ArrayList<Organization>();
        OrganizationSearchBean searchBean = new OrganizationSearchBean();
        searchBean.setKey(oiamOrgId);
        orgs = organizationDataService.findBeansLocalized(searchBean, null, -1, -1, null);
        return orgs;
    }

}
*/
