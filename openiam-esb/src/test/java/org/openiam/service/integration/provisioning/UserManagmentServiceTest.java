package org.openiam.service.integration.provisioning;


import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.resp.ProvisionUserResponse;

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

}
