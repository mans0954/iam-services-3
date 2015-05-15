package org.openiam.service.integration.provisioning;


import junit.framework.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;

import java.util.Collection;
import java.util.List;

/**
 * Created by alexander on 14.05.15.
 */
public class UserManagmentServiceTest extends AbstractUserManagementServiceTest {

    @Test
    public void minimalUserCreate() throws Exception {
        User user = super.createBean();
        user.setFirstName(getRandomName());
        user.setLastName(getRandomName());

        ProvisionUserResponse userResponse = provisionService.addUser(new ProvisionUser(user));

        Assert.assertTrue(userResponse.isSuccess());
        Assert.assertNotNull(userResponse.getUser());
        Assert.assertNotNull(userResponse.getUser().getId());

        UserSearchBean userSearchBean = newSearchBean();
        userSearchBean.setKey(userResponse.getUser().getId());
        userSearchBean.setDeepCopy(true);
        userSearchBean.setInitDefaulLogin(true);

        List<User> userList = userServiceClient.findBeans(userSearchBean, 0, 1);

        Assert.assertTrue(CollectionUtils.isNotEmpty(userList));

        User foundUser = userList.get(0);

        Assert.assertNotNull(foundUser.getDefaultLogin());
        Assert.assertEquals(UserStatusEnum.PENDING_INITIAL_LOGIN, foundUser.getStatus());

    }
}
