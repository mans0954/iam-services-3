package org.openiam.service.integration.provisioning;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by alexander on 14.05.15.
 */
public class UserManagmentServiceTest extends AbstractUserManagementServiceTest {

    protected User createFullBean() {
        final User user = super.createBean();

        MetadataTypeSearchBean searchBean = new MetadataTypeSearchBean();
        searchBean.setName(USER_TYPE);
        List<MetadataType> userTypeList = metadataServiceClient.findTypeBeans(searchBean, 0, 1,null);

        Assert.assertFalse(userTypeList.isEmpty(), String.format("MetadataType %s is not found", USER_TYPE));

        user.setMdTypeId(userTypeList.get(0).getId());

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
        user.setMiddleInit(getRandomName(1));
        user.setNickname(getRandomName());
        user.setPrefix(getRandomName(3));
        user.setSecondaryStatus(UserStatusEnum.ACTIVE);
        user.setSex("M");
        user.setShowInSearch(Integer.valueOf(1));
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setSuffix(getRandomName(3));
        user.setTitle(getRandomName());
        user.setUserTypeInd(getRandomName());

        return user;
    }

    private User doCreateFullBean() throws Exception{
        User user = createFullBean();
        user = ((ProvisionUserResponse)saveAndAssert(user)).getUser();

        pushUserId(user.getId());
        return user;
    }

    private User doCreate() throws Exception{
        User user = super.createBean();
        user = ((ProvisionUserResponse)saveAndAssert(user)).getUser();
        pushUserId(user.getId());
        return user;
    }

    @Test(groups ={"MINIMAL_USER"})
    public void minimalUserCreate() throws Exception {
        User user = doCreate();

        User foundUser = getAndAssert(user.getId());

        Assert.assertNotNull(foundUser.getDefaultLogin());
        Assert.assertEquals(UserStatusEnum.PENDING_INITIAL_LOGIN, foundUser.getStatus());
    }

    @Test(groups ={"MINIMAL_USER"})
    public void minimalUserUpdate() throws Exception {
        User user = doCreate();

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
        user.setNickname(getRandomName());
        user.setPrefix(getRandomName(3));
        user.setSecondaryStatus(UserStatusEnum.ACTIVE);
        user.setSex("M");
        user.setShowInSearch(Integer.valueOf(1));
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setSuffix(getRandomName(3));
        user.setTitle(getRandomName());
        user.setUserTypeInd(getRandomName());

        saveAndAssert(user);

        User foundUser = getAndAssert(user.getId());

        Assert.assertEquals(user.getFirstName(), foundUser.getFirstName());
        Assert.assertEquals(user.getLastName(), foundUser.getLastName());
        Assert.assertEquals(user.getClassification(), foundUser.getClassification());

        Assert.assertEquals(removeMillis(user.getBirthdate()), removeMillis(foundUser.getBirthdate()));
        Assert.assertEquals(getDate(user.getClaimDate()), getDate(foundUser.getClaimDate()));

        Assert.assertEquals(user.getCostCenter(), foundUser.getCostCenter());
        Assert.assertEquals(user.getEmployeeId(), foundUser.getEmployeeId());
        Assert.assertEquals(user.getEmployeeTypeId(), foundUser.getEmployeeTypeId());
        Assert.assertEquals(user.getJobCodeId(), foundUser.getJobCodeId());
        Assert.assertEquals(user.getLocationCd(), foundUser.getLocationCd());
        Assert.assertEquals(user.getLocationName(), foundUser.getLocationName());
        Assert.assertEquals(user.getMaidenName(), foundUser.getMaidenName());
        Assert.assertEquals(user.getMailCode(), foundUser.getMailCode());
        Assert.assertEquals(user.getMdTypeId(), foundUser.getMdTypeId());
        Assert.assertEquals(user.getMiddleInit(), foundUser.getMiddleInit());
        Assert.assertEquals(user.getNickname(), foundUser.getNickname());
        Assert.assertEquals(user.getPrefix(), foundUser.getPrefix());
        Assert.assertEquals(user.getSecondaryStatus(), foundUser.getSecondaryStatus());
        Assert.assertEquals(user.getStatus(), foundUser.getStatus());
        Assert.assertEquals(user.getSex(), foundUser.getSex());
        Assert.assertEquals(user.getShowInSearch(), foundUser.getShowInSearch());
        Assert.assertEquals(user.getSuffix(), foundUser.getSuffix());
        Assert.assertEquals(user.getTitle(), foundUser.getTitle());
        Assert.assertEquals(user.getUserTypeInd(), foundUser.getUserTypeInd());
    }


    @Test(groups ={"MINIMAL_USER"})
    public void minimalUserDelete() throws Exception {
        User user = doCreate();

        deleteAndAssert(user);

        User dbUser = get(user.getId());

        Assert.assertNull(dbUser, String.format("Can not delete user with ID: %s", user.getId()));
    }

    @Test(groups ={"MINIMAL_USER"})
    public void minimalUserDeleteTest() throws Exception {
        User user = doCreate();
        user = get(user.getId());

        ProvisionUserResponse response = provisionService.deleteUser(getDefaultManagedSystemId(), user.getDefaultLogin(), UserStatusEnum.REMOVE, "3000");
        Assert.assertTrue(response.isSuccess(), String.format("Could not delete element '%s' with ID '%s.  Response: %s", user, user.getId(), response));
        dropUserId(user.getId());
        User dbUser = get(user.getId());
        Assert.assertNull(dbUser, String.format("Can not delete user with ID: %s", user.getId()));
    }

    @Test(groups ={"COMPLETE_USER"})
    public void completeUserCreateTest() throws Exception {
        User user = doCreateFullBean();

        Thread.sleep(5000);

        User foundUser = getAndAssert(user.getId());

        Assert.assertEquals(foundUser.getId(), user.getId());
        Assert.assertNotNull(foundUser.getDefaultLogin());

        Map<String, UserAttribute> userAttributeMap = foundUser.getUserAttributes();
        Assert.assertFalse(userAttributeMap.isEmpty(), "User must have required attributes");

        Assert.assertEquals( userAttributeMap.keySet().size(), NUMBER_OF_REQUIRED_ATTRIBUTES);

        for(String attrName: userAttributeMap.keySet()){
            UserAttribute attr = userAttributeMap.get(attrName);

            Assert.assertNotNull(attr, "Attribute Can not be null");
            Assert.assertNotNull(attr.getMetadataId(), "Attribute Metadata can not be null");
            Assert.assertNotNull(attr.getValue(), "Attribute Value can not be null");
        }
    }

    @Test(groups ={"COMPLETE_USER"}, dependsOnMethods = {"completeUserCreateTest"})
    public void completeUserUpdateTest() throws Exception {
        User user = getAndAssert(getUserId());

        user.setFirstName(getRandomName());
        user.setLastName(getRandomName());

        saveAndAssert(user);

        User foundUser = getAndAssert(user.getId());

        Assert.assertEquals(foundUser.getId(), user.getId());
        Assert.assertEquals(user.getFirstName(), foundUser.getFirstName());
        Assert.assertEquals(user.getLastName(), foundUser.getLastName());
    }

    @Test(groups ={"COMPLETE_USER"}, dependsOnMethods = {"completeUserUpdateTest"})
    public void completeUserGetAllAttributeTest() throws Exception {
        User user = getAndAssert(getUserId());

        Map<String, UserAttribute> userAttributeMap = user.getUserAttributes();

        List<UserAttribute> dbAttributeList = userServiceClient.getUserAttributes(getUserId());

        Assert.assertEquals(userAttributeMap.values().size(), dbAttributeList.size());

        for(UserAttribute attr : dbAttributeList){
            UserAttribute userAttr  = userAttributeMap.get(attr.getName());

            Assert.assertNotNull(userAttr, String.format("User Attribute %s not exists in user but return from service method: userServiceClient.getUserAttributes()", userAttr.getName()));

        }
    }

    @Test(groups ={"COMPLETE_USER"}, dependsOnMethods = {"completeUserGetAllAttributeTest"})
    public void completeUserAddAttributeWithMetadataTest() throws Exception {
        User user = getAndAssert(getUserId());

        final UserAttribute userAttribute = new UserAttribute();
        userAttribute.setOperation(AttributeOperationEnum.ADD);

        for(String attrName: defaultUserAttributes.keySet()){
            MetadataElement metadataElement = defaultUserAttributes.get(attrName);
            if(metadataElement.getRequired())
                continue;
            userAttribute.setName(attrName);
            userAttribute.setValue(metadataElement.getStaticDefaultValue());
            userAttribute.setMetadataId(metadataElement.getId());
            break;
        }
        user.getUserAttributes().put(userAttribute.getName(), userAttribute);

        saveAndAssert(user);

        User foundUser = getAndAssert(user.getId());

        Assert.assertEquals(foundUser.getUserAttributes().size(), user.getUserAttributes().size());

        UserAttribute dbUserAttribute = foundUser.getUserAttributes().get(userAttribute.getName());

        Assert.assertNotNull(dbUserAttribute, "User Attribute not saved");
        Assert.assertEquals(dbUserAttribute.getValue(), userAttribute.getValue());
        Assert.assertEquals(dbUserAttribute.getMetadataId(), userAttribute.getMetadataId());
    }

    @Test(groups ={"COMPLETE_USER"}, dependsOnMethods = {"completeUserAddAttributeWithMetadataTest"})
    public void completeUserModifyAttributeWithMetadataTest() throws Exception {
        User user = getAndAssert(getUserId());

        UserAttribute userAttribute = user.getUserAttributes().get(DRIVERS_LICENSE);
        userAttribute.setOperation(AttributeOperationEnum.REPLACE);
        userAttribute.setValue(getRandomName());

        user.getUserAttributes().put(userAttribute.getName(), userAttribute);

        saveAndAssert(user);

        User foundUser = getAndAssert(user.getId());

        Assert.assertEquals(foundUser.getUserAttributes().size(), user.getUserAttributes().size());

        UserAttribute dbUserAttribute = foundUser.getUserAttributes().get(userAttribute.getName());

        Assert.assertNotNull(dbUserAttribute, "User Attribute not saved");
        Assert.assertEquals(dbUserAttribute.getValue(), userAttribute.getValue());
    }

    @Test(groups ={"COMPLETE_USER"}, dependsOnMethods = {"completeUserModifyAttributeWithMetadataTest"})
    public void completeUserDeleteAttributeWithMetadataTest() throws Exception {
        User user = getAndAssert(getUserId());

        UserAttribute userAttribute = null;
        for(String attrName: defaultUserAttributes.keySet()){
            MetadataElement metadataElement = defaultUserAttributes.get(attrName);
            if(metadataElement.getRequired())
                continue;
            userAttribute = user.getUserAttributes().get(attrName);
            break;
        }
        userAttribute.setOperation(AttributeOperationEnum.DELETE);
        user.getUserAttributes().put(userAttribute.getName(), userAttribute);
        saveAndAssert(user);

        User foundUser = getAndAssert(user.getId());
        Assert.assertNotEquals(foundUser.getUserAttributes().size(), user.getUserAttributes().size());
        UserAttribute dbUserAttribute = foundUser.getUserAttributes().get(userAttribute.getName());
        Assert.assertNull(dbUserAttribute, "User Attribute not deleted");
    }


    @Test(groups ={"COMPLETE_USER"}, dependsOnMethods = {"completeUserDeleteAttributeWithMetadataTest"})
    public void completeUserAddAttribute() throws Exception {
        User user = getAndAssert(getUserId());

        final UserAttribute userAttribute = new UserAttribute();
        userAttribute.setOperation(AttributeOperationEnum.ADD);
        userAttribute.setName(getRandomName());
        userAttribute.setValue(getRandomName());
        userAttribute.setMetadataId(null);
        user.getUserAttributes().put(userAttribute.getName(), userAttribute);

        saveAndAssert(user);

        User foundUser = getAndAssert(user.getId());

        Assert.assertEquals(foundUser.getUserAttributes().size(), user.getUserAttributes().size());
        UserAttribute dbUserAttribute = foundUser.getUserAttributes().get(userAttribute.getName());
        Assert.assertEquals(dbUserAttribute.getValue(), userAttribute.getValue());
    }
    @Test(groups ={"COMPLETE_USER"}, dependsOnMethods = {"completeUserAddAttribute"})
    public void completeUserAddMVAttributeWithMetadata() throws Exception {
        User user = getAndAssert(getUserId());

        MetadataElement elem = defaultUserAttributes.get(RANDOM_ATTRIBUTE);
        final UserAttribute userAttribute = new UserAttribute();
        userAttribute.setOperation(AttributeOperationEnum.ADD);
        userAttribute.setName(elem.getAttributeName());
        userAttribute.setIsMultivalued(true);
        List<String> values = new ArrayList<String>();
        values.add(getRandomName());
        values.add(getRandomName());
        values.add(getRandomName());
        values.add(getRandomName());
        userAttribute.setValues(values);
        userAttribute.setMetadataId(elem.getId());
        user.getUserAttributes().put(userAttribute.getName(), userAttribute);

        saveAndAssert(user);

        User foundUser = getAndAssert(user.getId());

        Assert.assertEquals(foundUser.getUserAttributes().size(), user.getUserAttributes().size());
        UserAttribute dbUserAttribute = foundUser.getUserAttributes().get(userAttribute.getName());

        Assert.assertEquals(dbUserAttribute.getIsMultivalued(), userAttribute.getIsMultivalued());

        Assert.assertEquals(dbUserAttribute.getValues().size(), values.size());
    }
    @Test(groups ={"COMPLETE_USER"}, dependsOnMethods = {"completeUserAddMVAttributeWithMetadata"})
    public void completeUserUpdateMVAttributeWithMetadata() throws Exception {
        User user = getAndAssert(getUserId());

        MetadataElement elem = defaultUserAttributes.get(RANDOM_ATTRIBUTE);
        final UserAttribute userAttribute = user.getAttribute(RANDOM_ATTRIBUTE);

        List<String> origValues = new ArrayList<>(userAttribute.getValues());

        userAttribute.setOperation(AttributeOperationEnum.REPLACE);
        userAttribute.setIsMultivalued(true);
        List<String> values = new ArrayList<String>();
        values.add(getRandomName());
        values.add(getRandomName());
        values.add(getRandomName());
        values.add(getRandomName());
        values.add(getRandomName());
        userAttribute.setValues(values);
        user.getUserAttributes().put(userAttribute.getName(), userAttribute);

        saveAndAssert(user);

        User foundUser = getAndAssert(user.getId());


        UserAttribute dbUserAttribute = foundUser.getUserAttributes().get(userAttribute.getName());
        Assert.assertEquals(dbUserAttribute.getIsMultivalued(), userAttribute.getIsMultivalued());

        Assert.assertTrue(dbUserAttribute.getValues().size() > origValues.size());
        for(int i=0;i<origValues.size();i++){
            String origValue = origValues.get(i);
            String newValue = dbUserAttribute.getValues().get(i);
            Assert.assertNotEquals(newValue, origValue);
        }
    }
    @Test(groups ={"COMPLETE_USER"}, dependsOnMethods = {"completeUserUpdateMVAttributeWithMetadata"})
    public void completeUserDeleteMVAttributeWithMetadata() throws Exception {
        User user = getAndAssert(getUserId());

        final UserAttribute userAttribute = user.getAttribute(RANDOM_ATTRIBUTE);
        userAttribute.setOperation(AttributeOperationEnum.DELETE);

        user.getUserAttributes().put(userAttribute.getName(), userAttribute);

        saveAndAssert(user);

        User foundUser = getAndAssert(user.getId());

        UserAttribute dbUserAttribute = foundUser.getUserAttributes().get(userAttribute.getName());
        Assert.assertNull(dbUserAttribute);
    }

    @Test(groups ={"COMPLETE_USER"}, dependsOnMethods = {"completeUserDeleteMVAttributeWithMetadata"})
    public void completeUserAddExistedAttribute() throws Exception {
        User user = getAndAssert(getUserId());

        MetadataElement elem = defaultUserAttributes.get(DRIVERS_LICENSE);
        final UserAttribute userAttribute = new UserAttribute();
        userAttribute.setOperation(AttributeOperationEnum.ADD);
        userAttribute.setName(elem.getAttributeName());
        userAttribute.setValue(getRandomName());
        userAttribute.setMetadataId(elem.getId());

        user.getUserAttributes().put(userAttribute.getName(), userAttribute);

        final Response response = save(user);
        Assert.assertTrue(response.isFailure(), "Attribute with the same name can be added");
        Assert.assertNotNull(response.getErrorCode(), String.format("Error code must not be null"));
        // TODO: need to assert with error code when it is added
    }



    @Test(groups ={"COMPLETE_USER"}, dependsOnMethods = {"completeUserAddExistedAttribute"})
    public void completeUserDeleteRequiredAttribute() throws Exception {
        User user = getAndAssert(getUserId());

        UserAttribute userAttribute = null;
        for(String attrName: defaultUserAttributes.keySet()) {
            MetadataElement metadataElement = defaultUserAttributes.get(attrName);
            if (metadataElement.getRequired()) {
                userAttribute = user.getUserAttributes().get(attrName);
                break;
            }
        }
        userAttribute.setOperation(AttributeOperationEnum.DELETE);
        user.getUserAttributes().put(userAttribute.getName(), userAttribute);
        saveAndAssert(user);

        User foundUser = getAndAssert(user.getId());
        // required Attribute must not be deleted
        Assert.assertEquals(foundUser.getUserAttributes().size(), user.getUserAttributes().size());
        UserAttribute dbUserAttribute = foundUser.getUserAttributes().get(userAttribute.getName());
        Assert.assertNotNull(dbUserAttribute, "User Attribute is deleted");
    }

    @Test(groups ={"COMPLETE_USER"}, dependsOnMethods = {"completeUserDeleteRequiredAttribute"})
    public void completeUserAddRole() throws Exception {
        User user = getAndAssert(getUserId());

        //create role
        Role role = new Role();
        role.setName(getRandomName());
        role.setDescription(getRandomName());
        Response res = roleServiceClient.saveRole(role, REQUESTER_ID);
        Assert.assertNotNull(res, "Response can not be null");
        Assert.assertTrue(res.isSuccess(), "Response should be successful");
        String id = (String)res.getResponseValue();
        Assert.assertNotNull(id, "Role id can not be null");
        roleIdList.add(id);

        Role r = roleServiceClient.getRole(id, REQUESTER_ID);
        Assert.assertNotNull(r, "Role can not be null");
        r.setOperation(AttributeOperationEnum.ADD);
        user.getRoles().add(r);
        saveAndAssert(user);
        User foundUser = getAndAssert(user.getId());
        Assert.assertNotNull(foundUser.getRoles());
        boolean found = false;
        for (Role ur: foundUser.getRoles()) {
            if (StringUtils.equals(ur.getName(), r.getName())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found, "Role should be added to User");

    }

    @Test(groups ={"COMPLETE_USER"}, dependsOnMethods = {"completeUserAddRole"})
    public void completeUserDeleteRole() throws Exception {
        User user = getAndAssert(getUserId());
        Assert.assertTrue(CollectionUtils.isNotEmpty(roleIdList));
        Role role = null;
        for (Role ur : user.getRoles()) {
            if (StringUtils.equals(ur.getId(), roleIdList.get(0))) {
                role = ur;
                break;
            }
        }
        Assert.assertNotNull(role, "Role is not found");
        role.setOperation(AttributeOperationEnum.DELETE);
        user.getRoles().add(role);
        saveAndAssert(user);
        User foundUser = getAndAssert(user.getId());
        boolean found = false;
        for (Role ur: foundUser.getRoles()) {
            if (StringUtils.equals(ur.getName(), role.getName())) {
                found = true;
                break;
            }
        }
        Assert.assertFalse(found, "Role should be deleted from User");

        Response res = roleServiceClient.removeRole(role.getId(), REQUESTER_ID);
        Assert.assertNotNull(res, "Response can not be null");
        Assert.assertTrue(res.isSuccess(), "Response should be successful");
        roleIdList.remove(role.getId());

    }

    @Test(groups ={"COMPLETE_USER"}, dependsOnMethods = {"completeUserDeleteRole"})
    public void completeUserAddGroup() throws Exception {
        User user = getAndAssert(getUserId());

        //create group
        Group group = new Group();
        group.setName(getRandomName());
        group.setDescription(getRandomName());
        Response res = groupServiceClient.saveGroup(group, REQUESTER_ID);
        Assert.assertNotNull(res, "Response can not be null");
        Assert.assertTrue(res.isSuccess(), "Response should be successful");
        String id = (String)res.getResponseValue();
        Assert.assertNotNull(id, "Group id can not be null");
        groupIdList.add(id);

        Group g = groupServiceClient.getGroup(id, REQUESTER_ID);
        Assert.assertNotNull(g, "Group can not be null");
        g.setOperation(AttributeOperationEnum.ADD);
        user.getGroups().add(g);
        saveAndAssert(user);
        User foundUser = getAndAssert(user.getId());
        Assert.assertNotNull(foundUser.getGroups());
        boolean found = false;
        for (Group ug: foundUser.getGroups()) {
            if (StringUtils.equals(ug.getName(), g.getName())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found, "Group should be added to User");

    }

    @Test(groups ={"COMPLETE_USER"}, dependsOnMethods = {"completeUserAddGroup"})
    public void completeUserDeleteGroup() throws Exception {
        User user = getAndAssert(getUserId());

        Assert.assertTrue(CollectionUtils.isNotEmpty(groupIdList));
        Group group = null;
        for (Group gr : user.getGroups()) {
            if (StringUtils.equals(gr.getId(), groupIdList.get(0))) {
                group = gr;
                break;
            }
        }
        Assert.assertNotNull(group, "Group is not found");
        group.setOperation(AttributeOperationEnum.DELETE);
        user.getGroups().add(group);
        saveAndAssert(user);
        User foundUser = getAndAssert(user.getId());
        boolean found = false;
        for (Group ug: foundUser.getGroups()) {
            if (StringUtils.equals(ug.getName(), group.getName())) {
                found = true;
                break;
            }
        }
        Assert.assertFalse(found, "Group should be deleted from User");

        Response res = groupServiceClient.deleteGroup(group.getId(), REQUESTER_ID);
        Assert.assertNotNull(res, "Response can not be null");
        Assert.assertTrue(res.isSuccess(), "Response should be successful");
        groupIdList.remove(group.getId());
    }

    @Test(groups ={"COMPLETE_USER"}, dependsOnMethods = {"completeUserDeleteGroup"})
    public void completeUserDeleteTest() throws Exception {
        User user = getAndAssert(getUserId());

        deleteAndAssert(user);

        User foundUser = get(user.getId());
        // required Attribute must not be deleted
        Assert.assertNull(foundUser, "User cannot be deleted");
    }
}
