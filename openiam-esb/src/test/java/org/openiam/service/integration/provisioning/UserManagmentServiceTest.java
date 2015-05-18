package org.openiam.service.integration.provisioning;


import org.testng.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by alexander on 14.05.15.
 */
public class UserManagmentServiceTest extends AbstractUserManagementServiceTest {

    @Test
    public void minimalUserCreate() throws Exception {
        User user = doCreate();

        User foundUser = getAndAssert(user.getId());

        Assert.assertNotNull(foundUser.getDefaultLogin());
        Assert.assertEquals(UserStatusEnum.PENDING_INITIAL_LOGIN, foundUser.getStatus());
    }

    @Test
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


    @Test
    public void minimalUserDelete() throws Exception {
        User user = doCreate();

        ProvisionUserResponse response = (ProvisionUserResponse)deleteAndAssert(user);

        User dbUser = get(user.getId());

        Assert.assertNull(dbUser, String.format("Can not delete user with ID: %s", user.getId()));
    }

    private User doCreate() throws Exception{
        User user = super.createBean();
        user.setFirstName(getRandomName());
        user.setLastName(getRandomName());

        return ((ProvisionUserResponse)saveAndAssert(user)).getUser();
    }
}
