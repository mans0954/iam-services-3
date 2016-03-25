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
    private static final String roleSameName1 = "Role Unique Name 1";
    private static final String roleSameName2 = "Role Unique Name 2";

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
        newRole.setName(roleSameName);
        newRole.setManagedSysId(ldapMngSysId);
        Response newRes = roleServiceClient.saveRole(newRole, REQUESTER_ID);
        Assert.assertNotNull(newRes);
        String newGroupId = (String)newRes.getResponseValue();
        sameRoleIds.add(newGroupId);

        Assert.assertTrue(res.isSuccess());
        Assert.assertNotNull(groupId);

        Assert.assertTrue(newRes.isSuccess());
        Assert.assertNotNull(newGroupId);

    }

    @Test
    public void createRolesWithSameNameInSameMngSys() {
        Role role = new Role();
        role.setName(roleSameName1);
        role.setManagedSysId(adMngSysId);
        Response res = roleServiceClient.saveRole(role, REQUESTER_ID);
        Assert.assertNotNull(res);
        String groupId = (String)res.getResponseValue();
        sameRoleIds.add(groupId);

        Role newRole = new Role();
        newRole.setName(roleSameName1);
        newRole.setManagedSysId(adMngSysId);
        Response newRes = roleServiceClient.saveRole(newRole, REQUESTER_ID);
        Assert.assertNotNull(newRes);
        String newGroupId = (String)newRes.getResponseValue();
        sameRoleIds.add(newGroupId);

        Assert.assertTrue(res.isSuccess());
        Assert.assertNotNull(groupId);

        Assert.assertTrue(newRes.isFailure());
        Assert.assertNull(newGroupId);
    }

    @Test
    public void createRolesWithoutMngSys() throws Exception {
        Role role = new Role();
        role.setName(roleSameName2);
        role.setManagedSysId(null);
        Response res = roleServiceClient.saveRole(role, REQUESTER_ID);
        Assert.assertNotNull(res);
        String groupId = (String)res.getResponseValue();
        sameRoleIds.add(groupId);

        Role newRole = new Role();
        newRole.setName(roleSameName2);
        newRole.setManagedSysId(null);
        Response newRes = roleServiceClient.saveRole(newRole, REQUESTER_ID);
        Assert.assertNotNull(newRes);
        String newGroupId = (String)newRes.getResponseValue();
        sameRoleIds.add(newGroupId);

        Assert.assertTrue(res.isSuccess());
        Assert.assertNotNull(groupId);

        Assert.assertTrue(newRes.isFailure());
        Assert.assertNull(newGroupId);
    }

    private void deleteRoleWithSameName() {
        //for createRoleWithSameName
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

        //for createRolesWithSameNameInSameMngSys
        if (sameRoleIds.get(2) != null) {
            String firstRoleName = sameRoleIds.get(2);
            Response resFirst = roleServiceClient.validateDelete(firstRoleName);
            if (resFirst.isSuccess()) {
                roleServiceClient.removeRole(firstRoleName, REQUESTER_ID);
            }
        }

        if (sameRoleIds.get(3) != null) {
            String secondRoleName = sameRoleIds.get(3);
            Response resSecond = roleServiceClient.validateDelete(secondRoleName);
            if (resSecond.isSuccess()) {
                roleServiceClient.removeRole(secondRoleName, REQUESTER_ID);
            }
        }

        //for createRolesWithoutMngSys
        if (sameRoleIds.get(4) != null) {
            String firstRoleName = sameRoleIds.get(4);
            Response resFirst = roleServiceClient.validateDelete(firstRoleName);
            if (resFirst.isSuccess()) {
                roleServiceClient.removeRole(firstRoleName, REQUESTER_ID);
            }
        }

        if (sameRoleIds.get(5) != null) {
            String secondRoleName = sameRoleIds.get(5);
            Response resSecond = roleServiceClient.validateDelete(secondRoleName);
            if (resSecond.isSuccess()) {
                roleServiceClient.removeRole(secondRoleName, REQUESTER_ID);
            }
        }

    }
}
