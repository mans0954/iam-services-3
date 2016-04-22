package org.openiam.service.integration.provisioning;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.Response;
import org.openiam.connector.type.ObjectValue;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.ws.IdentityWebService;
import org.openiam.idm.srvc.auth.ws.LoginListResponse;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.property.service.PropertyValueSweeper;
import org.openiam.idm.srvc.res.service.ResourceDataService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Test(groups = "groupProv")
public class GroupManagementServiceTest extends AbstractServiceTest {

    private static final String REQUESTER_ID = "3000";
    private static final String AD_RES_ID = "110";
    private static final String AD_MNGSYS_ID = "110";

    private static final String adMngSysId = "110";
    private static final String ldapMngSysId = "101";
    private static final String groupSameName = getRandomNameStatic();
    private static final String groupSameName1 = getRandomNameStatic();
    private static final String groupSameName2 = getRandomNameStatic();

    @Resource(name="groupServiceClient")
    private GroupDataWebService groupServiceClient;

    @Resource(name = "groupProvisionServiceClient")
    protected ObjectProvisionService groupProvisionServiceClient;

    @Resource(name = "identityServiceClient")
    protected IdentityWebService identityServiceClient;

    @Resource(name = "provisionServiceClient")
    private ProvisionService provisionServiceClient;

    @Resource(name = "resourceServiceClient")
    private ResourceDataService resourceServiceClient;

    private List<String> groupIds;
    private List<String> userIds;

    private List<String> sameGroupIds;

    @BeforeClass(groups = {"groupProv"}, alwaysRun = true)
    public void initGroupProv() {
        groupIds = new ArrayList<String>();
        userIds = new ArrayList<String>();

        sameGroupIds = new ArrayList<String>();
    }

    @AfterClass(groups = {"groupProv"}, alwaysRun = true)
    public void destroyGroupProv() {
        if (CollectionUtils.isNotEmpty(groupIds)) {
            for (String groupId : groupIds) {
                try {
                    groupProvisionServiceClient.remove(groupId, REQUESTER_ID);
                } catch (Exception e) {}
            }
        }
        if (CollectionUtils.isNotEmpty(userIds)) {
            for (String userId : userIds) {
                try {
                    provisionServiceClient.deleteByUserId(userId, UserStatusEnum.REMOVE, REQUESTER_ID);
                } catch (Exception e) {}
            }
        }

        deleteGroupsWithSameName();
    }

    @Test
    public void createGroupHere() throws Exception{
        //create group
        Group group = new Group();
        group.setName(getRandomName());
        group.setDescription(getRandomName());
        Response res = groupServiceClient.saveGroup(group, REQUESTER_ID);
        Assert.assertNotNull(res, "Response can not be null");
        Assert.assertTrue(res.isSuccess(), "Response should be successful");
        String id = (String)res.getResponseValue();
        Assert.assertNotNull(id, "Group id can not be null");
        groupIds.add(id);

        //provision add group
        Group pGroup = new ProvisionGroup(groupServiceClient.getGroup(id, REQUESTER_ID));
        Response provResponse = groupProvisionServiceClient.add(pGroup);
        Assert.assertNotNull(provResponse, "Response can not be null");
        Assert.assertTrue(provResponse.isSuccess(), "Response should be successful");
        IdentityDto identity = identityServiceClient.getIdentityByManagedSys(pGroup.getId(), getDefaultManagedSystemId());
        Assert.assertNotNull(identity.getId(), "Identity id can not be null");

    }

    @Test(dependsOnMethods = {"createGroupHere"})
    public void modifyGroup() throws Exception {
        Assert.assertEquals(CollectionUtils.isNotEmpty(groupIds), true);
        String id = groupIds.get(0);
        //modify group
        Group group1 = groupServiceClient.getGroup(id, REQUESTER_ID);
        String origDesc = group1.getDescription();
        Assert.assertNotNull(group1, "Group can not be null");
        group1.setDescription(origDesc + " modified");
        Response res = groupServiceClient.saveGroup(group1, REQUESTER_ID);
        Assert.assertNotNull(res, "Response can not be null");
        Assert.assertTrue(res.isSuccess(), "Response should be successful");
        Group group2 = groupServiceClient.getGroup(id, REQUESTER_ID);
        Assert.assertNotNull(group2, "Group can not be null");
        Assert.assertFalse(StringUtils.equals(origDesc, group2.getDescription()));

        //provision modify group
        Group pGroup = new ProvisionGroup(group2);
        Response provResponse = groupProvisionServiceClient.modify(pGroup);
        Assert.assertNotNull(provResponse, "Response can not be null");
        Assert.assertTrue(provResponse.isSuccess(), "Response should be successful");
    }

    @Test(dependsOnMethods = {"modifyGroup"})
    public void addUserToGroup() {
        Assert.assertEquals(CollectionUtils.isNotEmpty(groupIds), true);
        String id = groupIds.get(0);
        User user = createUser();
        Assert.assertNotNull(user, "User can not be null");
        String userId = user.getId();
        Assert.assertNotNull(userId, "User id can not be null");
        userIds.add(userId);
        Response res = groupServiceClient.addUserToGroup(id, userId, REQUESTER_ID, null, new Date(), new Date());
        Assert.assertNotNull(res, "Response can not be null");
        Assert.assertTrue(res.isSuccess(), "Response should be successful");

        // check if user is a member
        Response response = groupServiceClient.isUserInGroup(id, userId);
        Assert.assertNotNull(response, "Response can not be null");
        Assert.assertTrue(response.isSuccess(), "Response should be successful");

    }

    @Test(dependsOnMethods = {"addUserToGroup"})
    public void removeUserFromGroup() {
        Assert.assertEquals(CollectionUtils.isNotEmpty(groupIds), true);
        String id = groupIds.get(0);
        Assert.assertEquals(CollectionUtils.isNotEmpty(userIds), true);
        String userId = userIds.get(0);
        Response res = groupServiceClient.removeUserFromGroup(id, userId, REQUESTER_ID);
        Assert.assertNotNull(res, "Response can not be null");
        Assert.assertTrue(res.isSuccess(), "Response should be successful");

        // check if user is a member
        Response response = groupServiceClient.isUserInGroup(id, userId);
        Assert.assertTrue(response.isSuccess(), "Response should be successful");
        Assert.assertFalse((Boolean) response.getResponseValue());
    }

    /*
    NEED FIX THE TESTS
    @Test(dependsOnMethods = {"removeUserFromGroup"})
    public void createADGroup() throws InterruptedException {
        Assert.assertEquals(CollectionUtils.isNotEmpty(groupIds), true);
        String id = groupIds.get(0);
        Response res = resourceServiceClient.addGroupToResource(AD_RES_ID, id, REQUESTER_ID, null, new Date(), new Date());
        Assert.assertNotNull(res, "Response can not be null");
        Assert.assertTrue(res.isSuccess(), "Response should be successful");

        Group group = groupServiceClient.getGroup(id, REQUESTER_ID);
        ProvisionGroup pGroup = new ProvisionGroup(group);
        pGroup.setName(getRandomName());
        Set<org.openiam.idm.srvc.res.dto.Resource> ressss = group.getResources();
        org.openiam.idm.srvc.res.dto.Resource newFuckingRes = new org.openiam.idm.srvc.res.dto.Resource();
        if (ressss != null) {
            for (org.openiam.idm.srvc.res.dto.Resource res1 : ressss) {
                if (res1.getId() != null && res1.getId().equals(AD_RES_ID)) {
                    newFuckingRes = res1;
                }
            }
        }
        //org.openiam.idm.srvc.res.dto.Resource resource = pGroup.findResource(AD_RES_ID);
        Assert.assertNotNull(newFuckingRes, "Resource can not be null");
        newFuckingRes.setOperation(AttributeOperationEnum.ADD);
        Response provResponse = groupProvisionServiceClient.modify(pGroup);
        Assert.assertNotNull(provResponse, "Response can not be null");
        Assert.assertTrue(provResponse.isSuccess(), "Response should be successful");

        IdentityDto identity = identityServiceClient.getIdentityByManagedSys(id, AD_RES_ID);
        Assert.assertNotNull(identity.getId(), "Identity id can not be null");

        List<ExtensibleAttribute> extAttrs = new ArrayList<ExtensibleAttribute>();
        LookupObjectResponse lookupResp = null;

        for (int i=0; i<15; i++) {
            Thread.sleep(2000);
            lookupResp = groupProvisionServiceClient.getTargetSystemObject(identity.getIdentity(), AD_MNGSYS_ID, extAttrs);
            if (lookupResp.isSuccess()) {
                break;
            }
        }
        Assert.assertNotNull(lookupResp, "Response can not be null");

    }

    @Test(groups = {"AD"}, dependsOnMethods = {"createADGroup"})
    public void modifyADGroup() throws InterruptedException {
        Assert.assertEquals(CollectionUtils.isNotEmpty(groupIds), true);
        String id = groupIds.get(0);

        Group group1 = groupServiceClient.getGroup(id, REQUESTER_ID);
        group1.setDescription("AD DESCRIPTION");
        Response res = groupServiceClient.saveGroup(group1, REQUESTER_ID);

        Group group2 = groupServiceClient.getGroup(id, REQUESTER_ID);
        ProvisionGroup pGroup = new ProvisionGroup(group2);
        groupProvisionServiceClient.modify(pGroup);

        IdentityDto identity = identityServiceClient.getIdentityByManagedSys(id, AD_RES_ID);
        List<ExtensibleAttribute> extAttrs = new ArrayList<ExtensibleAttribute>();
        extAttrs.add(new ExtensibleAttribute("description", ""));

        LookupObjectResponse lookupResp = null;
        for (int i=0; i<15; i++) {
            Thread.sleep(2000);
            lookupResp = groupProvisionServiceClient.getTargetSystemObject(identity.getIdentity(), AD_MNGSYS_ID, extAttrs);
            if (lookupResp.isSuccess()) {
                break;
            }
        }
        Assert.assertNotNull(lookupResp, "Response can not be null");
        ObjectValue objectValue = (ObjectValue)lookupResp.getResponseValue();
        Assert.assertNotNull(objectValue, "Object value can not be null");
        Assert.assertTrue(CollectionUtils.isNotEmpty(objectValue.getAttributeList()), "Attribute list should not be empty");

        String description = null;
        for (ExtensibleAttribute ea : objectValue.getAttributeList()) {
            if ("description".equals(ea.getName())) {
                description = ea.getValue();
                break;
            }
        }
        Assert.assertNotNull(description, "Description can not be null");
        Assert.assertEquals(description, "AD DESCRIPTION");
    }

    @Test(groups = {"AD"}, dependsOnMethods = {"modifyADGroup"})
    public void addUserToADGroup() throws Exception {
        Assert.assertEquals(CollectionUtils.isNotEmpty(groupIds), true);
        String id = groupIds.get(0);

        final ProvisionUser pUser = new ProvisionUser();
        pUser.setFirstName(getRandomName());
        pUser.setLastName(getRandomName());
        pUser.setRequestorUserId(REQUESTER_ID);
        pUser.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE).get(0).getId());
        org.openiam.idm.srvc.res.dto.Resource resource = resourceServiceClient.getResource(AD_RES_ID, getDefaultLanguage());
        pUser.addResource(resource);
        ProvisionUserResponse res = provisionServiceClient.addUser(pUser);
        Assert.assertNotNull(res, "Response can not be null");
        Assert.assertTrue(res.isSuccess(), "Response should be successful");
        String userId = res.getUser().getId();
        userIds.add(userId);
        LoginListResponse response = loginServiceClient.getLoginByUser(userId);
        Assert.assertNotNull(response, "Response can not be null");
        Assert.assertTrue(response.isSuccess(), "Response should be successful");
        List<Login> logins = response.getPrincipalList();
        Assert.assertTrue(CollectionUtils.isNotEmpty(logins), "Logins should not be empty");
        String principal = null;
        for (Login l : logins) {
            if (AD_MNGSYS_ID.equals(l.getManagedSysId())) {
                principal = l.getLogin();
                break;
            }
        }
        Assert.assertNotNull(principal, "Principal can not be null");
        List<ExtensibleAttribute> extAttrs = new ArrayList<ExtensibleAttribute>();
        extAttrs.add(new ExtensibleAttribute("sAMAccountName",""));

        LookupUserResponse lookupResp = null;
        for (int i=0; i<15; i++) {
            Thread.sleep(2000);
            lookupResp = provisionServiceClient.getTargetSystemUser(principal, AD_MNGSYS_ID, extAttrs);
            if (lookupResp.isSuccess()) {
                break;
            }
        }
        Assert.assertNotNull(lookupResp, "Response can not be null");
        ObjectValue objectValue = (ObjectValue)lookupResp.getResponseValue();
        Assert.assertNotNull(objectValue, "Object value can not be null");
        Assert.assertTrue(CollectionUtils.isNotEmpty(objectValue.getAttributeList()), "Attribute list should not be empty");

        Group group = groupServiceClient.getGroup(id, REQUESTER_ID);
        Response res1 = groupServiceClient.addUserToGroup(id, userId, REQUESTER_ID, null, new Date(), new Date());
        Assert.assertNotNull(res1, "Response can not be null");
        Assert.assertTrue(res1.isSuccess(), "Response should be successful");

        // check if user is a member
        Response res2 = groupServiceClient.isUserInGroup(id, userId);
        Assert.assertNotNull(res2, "Response can not be null");
        Assert.assertTrue(res2.isSuccess(), "Response should be successful");

        Response provResponse = groupProvisionServiceClient.modify(group);
        Assert.assertNotNull(provResponse, "Response can not be null");
        Assert.assertTrue(provResponse.isSuccess(), "Response should be successful");

        IdentityDto identity = identityServiceClient.getIdentityByManagedSys(id, AD_RES_ID);
        List<ExtensibleAttribute> grpExtAttrs = new ArrayList<ExtensibleAttribute>();
        grpExtAttrs.add(new ExtensibleAttribute("member",""));

        LookupObjectResponse groupLookupResp = null;
        for (int i=0; i<15; i++) {
            Thread.sleep(2000);
            groupLookupResp = groupProvisionServiceClient.getTargetSystemObject(identity.getIdentity(), AD_MNGSYS_ID, grpExtAttrs);
            if (groupLookupResp.isSuccess()) {
                break;
            }
        }
        Assert.assertNotNull(groupLookupResp, "Response can not be null");
        ObjectValue objValue = (ObjectValue)groupLookupResp.getResponseValue();
        Assert.assertNotNull(objValue, "Object value can not be null");
        Assert.assertTrue(CollectionUtils.isNotEmpty(objValue.getAttributeList()), "Attribute list should not be empty");

        String memberDN = null;
        for (ExtensibleAttribute ea : objValue.getAttributeList()) {
            if ("member".equals(ea.getName())) {
                memberDN = ea.getValue();
                break;
            }
        }
        Assert.assertNotNull(memberDN, "Member can not be null");
    }

    @Test(groups = {"AD"}, dependsOnMethods = {"addUserToADGroup"})
    public void removeUserFromADGroup() throws InterruptedException {
        Assert.assertEquals(CollectionUtils.isNotEmpty(groupIds), true);
        String id = groupIds.get(0);

        User user = userServiceClient.getUsersForGroup(id, REQUESTER_ID, 0, 1).get(0);
        Assert.assertNotNull(user, "User can not be null");

        Response res = groupServiceClient.removeUserFromGroup(id, user.getId(), REQUESTER_ID);
        Assert.assertNotNull(res, "Response can not be null");
        Assert.assertTrue(res.isSuccess(), "Response should be successful");

        Group group = groupServiceClient.getGroup(id, REQUESTER_ID);
        Response provResponse = groupProvisionServiceClient.modify(group);
        Assert.assertNotNull(provResponse, "Response can not be null");
        Assert.assertTrue(provResponse.isSuccess(), "Response should be successful");

        IdentityDto identity = identityServiceClient.getIdentityByManagedSys(id, AD_RES_ID);
        List<ExtensibleAttribute> grpExtAttrs = new ArrayList<ExtensibleAttribute>();
        grpExtAttrs.add(new ExtensibleAttribute("member",""));

        LookupObjectResponse groupLookupResp = null;
        for (int i=0; i<15; i++) {
            Thread.sleep(2000);
            groupLookupResp = groupProvisionServiceClient.getTargetSystemObject(identity.getIdentity(), AD_MNGSYS_ID, grpExtAttrs);
            if (groupLookupResp.isSuccess()) {
                break;
            }
        }
        Assert.assertNotNull(groupLookupResp, "Response can not be null");
        ObjectValue objValue = (ObjectValue)groupLookupResp.getResponseValue();
        Assert.assertNotNull(objValue, "Object value can not be null");
        Assert.assertTrue(CollectionUtils.isNotEmpty(objValue.getAttributeList()), "Attribute list should not be empty");

        String memberDN = null;
        for (ExtensibleAttribute ea : objValue.getAttributeList()) {
            if ("member".equals(ea.getName())) {
                memberDN = ea.getValue();
                break;
            }
        }
        Assert.assertNull(memberDN, "Member should be deleted");

    }

    @Test(groups = {"AD"}, dependsOnMethods = {"removeUserFromADGroup"})
    public void deleteGroup() {
        Assert.assertEquals(CollectionUtils.isNotEmpty(groupIds), true);
        String id = groupIds.get(0);

        //provision delete group
        Response res = groupProvisionServiceClient.remove(id, REQUESTER_ID);
        Assert.assertNotNull(res, "Response can not be null");
        Assert.assertTrue(res.isSuccess(), "Response should be successful");

        Group group = groupServiceClient.getGroup(id, REQUESTER_ID);
        Assert.assertNull(group, "Group is not removed");
        IdentityDto identity = identityServiceClient.getIdentityByManagedSys(id, getDefaultManagedSystemId());
        Assert.assertNull(identity, "Group identity is not removed");

        groupIds.remove(id);
    }*/

    @Test
    public void createGroupsWithSameName() throws Exception {
        //create groups with same name for different mngSys
    	
    	/* delete in case of previous run & fail */
    	deleteGroupsByName(groupSameName);

        Group group = new Group();
        group.setName(groupSameName);
        group.setManagedSysId(adMngSysId);
        Response res = groupServiceClient.saveGroup(group, REQUESTER_ID);
        Assert.assertNotNull(res);
        String groupId = (String)res.getResponseValue();
        sameGroupIds.add(groupId);

        Group newGroup = new Group();
        newGroup.setName(groupSameName);
        newGroup.setManagedSysId(ldapMngSysId);
        Response newRes = groupServiceClient.saveGroup(newGroup, REQUESTER_ID);
        Assert.assertNotNull(newRes);
        String newGroupId = (String)newRes.getResponseValue();
        sameGroupIds.add(newGroupId);

        Assert.assertTrue(res.isSuccess());
        Assert.assertNotNull(groupId);

        Assert.assertTrue(newRes.isSuccess());
        Assert.assertNotNull(newGroupId);

    }
    
    private void deleteGroupsByName(final String name) {
    	final GroupSearchBean sb = new GroupSearchBean();
    	sb.setName(name);
    	final List<Group> groups = groupServiceClient.findBeansLocalize(sb, REQUESTER_ID, 0, Integer.MAX_VALUE, getDefaultLanguage());
    	if(CollectionUtils.isNotEmpty(groups)) {
    		groups.forEach(e -> {
    			assertSuccess(groupServiceClient.deleteGroup(e.getId(), REQUESTER_ID));
    		});
    	}
    }

    @Test
    public void createGroupsWithSameNameInSameMngSys() throws Exception {
    	/* clean up data in case of previous fail */
    	deleteGroupsByName(groupSameName1);
    	
        Group group = new Group();
        group.setName(groupSameName1);
        group.setManagedSysId(adMngSysId);
        Response res = groupServiceClient.saveGroup(group, REQUESTER_ID);
        Assert.assertNotNull(res);
        String groupId = (String)res.getResponseValue();
        sameGroupIds.add(groupId);

        Group newGroup = new Group();
        newGroup.setName(groupSameName1);
        newGroup.setManagedSysId(adMngSysId);
        Response newRes = groupServiceClient.saveGroup(newGroup, REQUESTER_ID);
        Assert.assertNotNull(newRes);
        String newGroupId = (String)newRes.getResponseValue();
        sameGroupIds.add(newGroupId);

        Assert.assertTrue(res.isSuccess());
        Assert.assertNotNull(groupId);

        Assert.assertTrue(newRes.isFailure());
        Assert.assertNull(newGroupId);
    }

    @Test
    public void createGroupsWithoutMngSys () throws Exception {
    	/* clean up data in case of previous fail */
    	deleteGroupsByName(groupSameName2);
    	
        Group group = new Group();
        group.setName(groupSameName2);
        group.setManagedSysId(null);
        Response res = groupServiceClient.saveGroup(group, REQUESTER_ID);
        Assert.assertNotNull(res);
        String groupId = (String)res.getResponseValue();
        sameGroupIds.add(groupId);

        Group newGroup = new Group();
        newGroup.setName(groupSameName2);
        newGroup.setManagedSysId(null);
        Response newRes = groupServiceClient.saveGroup(newGroup, REQUESTER_ID);
        Assert.assertNotNull(newRes);
        String newGroupId = (String)newRes.getResponseValue();
        sameGroupIds.add(newGroupId);

        Assert.assertTrue(res.isSuccess());
        Assert.assertNotNull(groupId);

        Assert.assertTrue(newRes.isFailure());
        Assert.assertNull(newGroupId);
    }

    private void deleteGroupsWithSameName() {
        // for createGroupsWithSameName
        if (sameGroupIds.size() >= 1 && sameGroupIds.get(0) != null) {
            String firstGroupName = sameGroupIds.get(0);
            Response resFirst = groupServiceClient.validateDelete(firstGroupName);
            if (resFirst.isSuccess()) {
                groupServiceClient.deleteGroup(firstGroupName, REQUESTER_ID);
            }
        }

        if (sameGroupIds.size() >= 2 && sameGroupIds.get(1) != null) {
            String secondGroupName = sameGroupIds.get(1);
            Response resSecond = groupServiceClient.validateDelete(secondGroupName);
            if (resSecond.isSuccess()) {
                groupServiceClient.deleteGroup(secondGroupName, REQUESTER_ID);
            }
        }

        //for createGroupsWithSameNameInSameMngSys
        if (sameGroupIds.size() >= 3 && sameGroupIds.get(2) != null) {
            String firstGroupName = sameGroupIds.get(2);
            Response resFirst = groupServiceClient.validateDelete(firstGroupName);
            if (resFirst.isSuccess()) {
                groupServiceClient.deleteGroup(firstGroupName, REQUESTER_ID);
            }
        }

        if (sameGroupIds.size() >= 4 && sameGroupIds.get(3) != null) {
            String secondGroupName = sameGroupIds.get(3);
            Response resSecond = groupServiceClient.validateDelete(secondGroupName);
            if (resSecond.isSuccess()) {
                groupServiceClient.deleteGroup(secondGroupName, REQUESTER_ID);
            }
        }

        //for createGroupsWithoutMngSys
        if (sameGroupIds.size() >= 5 && sameGroupIds.get(4) != null) {
            String firstGroupName = sameGroupIds.get(4);
            Response resFirst = groupServiceClient.validateDelete(firstGroupName);
            if (resFirst.isSuccess()) {
                groupServiceClient.deleteGroup(firstGroupName, REQUESTER_ID);
            }
        }

        if (sameGroupIds.size() >= 6 && sameGroupIds.get(5) != null) {
            String secondGroupName = sameGroupIds.get(5);
            Response resSecond = groupServiceClient.validateDelete(secondGroupName);
            if (resSecond.isSuccess()) {
                groupServiceClient.deleteGroup(secondGroupName, REQUESTER_ID);
            }
        }

    }
}
