package org.openiam.service.integration.provisioning;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.Response;
import org.openiam.connector.type.ObjectValue;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.ws.IdentityWebService;
import org.openiam.idm.srvc.auth.ws.LoginListResponse;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.LookupObjectResponse;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.ObjectProvisionService;
import org.openiam.provision.service.ProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.service.integration.AbstractServiceTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


public class RoleManagementServiceTest extends AbstractServiceTest {

    private static final String REQUESTER_ID = "3000";
    private static final String adMngSysId = "110";
    private static final String ldapMngSysId = "101";
    private static final String roleSameName = "Role Unique Name";

    @Resource(name="roleServiceClient")
    private RoleDataWebService roleServiceClient;

    private List<String> sameRoleIds;

    @BeforeClass(alwaysRun = true)
    public void first() {

        sameRoleIds = new ArrayList<String>();
    }

    @AfterClass(alwaysRun = true)
    public void last() {
        deleteRoleWithSameName();
    }

    @Test
    public void createRoleWithSameName() throws Exception {
        //create role with same name for different mngSys

        Role role = new Role();
        role.setName(roleSameName);
        role.setManagedSysId(adMngSysId);
        Response res = roleServiceClient.saveRole(role, REQUESTER_ID);
        Assert.assertNotNull(res);
        String groupId = (String)res.getResponseValue();
        sameRoleIds.add(groupId);



        Role newRole = new Role();
        role.setName(roleSameName);
        role.setManagedSysId(ldapMngSysId);
        Response newRes = roleServiceClient.saveRole(newRole, REQUESTER_ID);
        Assert.assertNotNull(newRes);
        String newGroupId = (String)newRes.getResponseValue();
        sameRoleIds.add(newGroupId);



        Assert.assertTrue(res.isSuccess());
        Assert.assertNotNull(groupId);

        Assert.assertTrue(newRes.isSuccess());
        Assert.assertNotNull(newGroupId);

    }

    private void deleteRoleWithSameName() {
        if (sameRoleIds.get(0) != null) {
            String firstRoleName = sameRoleIds.get(0);
            Response resFirst = roleServiceClient.validateDelete(firstRoleName);
            if (resFirst.isSuccess()) {
                roleServiceClient.removeRole(firstRoleName, REQUESTER_ID);
            }
        }

        if (sameRoleIds.get(1) != null) {
            String secondRoleName = sameRoleIds.get(1);
            Response resSecond = roleServiceClient.validateDelete(secondRoleName);
            if (resSecond.isSuccess()) {
                roleServiceClient.removeRole(secondRoleName, REQUESTER_ID);
            }
        }

    }
}
