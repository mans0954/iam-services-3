package org.openiam.service.integration.provisioning;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.testng.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anton on 17.05.15.
 */
public class PasswordChangeServiceTest extends AbstractUserManagementServiceTest {

    @Resource(name = "organizationServiceClient")
    protected OrganizationDataService organizationDataService;

    @Test
    public void createUsers() throws Exception{
        createOpeniamUser();
        //createAdUser();
        createOpeniamUserLocked();
        createOpeniamUserDeactive();
    }

    private User doCreate() throws Exception{
        User user = super.createBean();
        user.setFirstName(getRandomName());
        user.setLastName(getRandomName());

        return ((ProvisionUserResponse)saveAndAssert(user)).getUser();
    }

    private List<Organization> searchOrg() throws Exception {
        List<Organization> orgs = new ArrayList<Organization>();
        OrganizationSearchBean searchBean = new OrganizationSearchBean();
        searchBean.setKey("100");
        orgs = organizationDataService.findBeansLocalized(searchBean, null, -1, -1, null);

        return orgs;
    }

    private void createOpeniamUser() throws Exception {
        User user = doCreate();
        user.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE).get(0).getId());

        if (CollectionUtils.isNotEmpty(searchOrg())){
            for (Organization o : searchOrg()){
                o.setOperation(AttributeOperationEnum.ADD);
                user.getAffiliations().add(o);
            }
        }

        saveAndAssert(user);
        User foundUser = getAndAssert(user.getId());
        Assert.assertEquals(user.getFirstName(), foundUser.getFirstName());
        Assert.assertEquals(user.getLastName(), foundUser.getLastName());
        Assert.assertEquals(user.getMdTypeId(), foundUser.getMdTypeId());
        Assert.assertEquals(user.getAffiliations(), foundUser.getAffiliations());
    }

    private void createAdUser() throws Exception {
        User user = doCreate();
        user.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE).get(0).getId());

    }

    private void createOpeniamUserLocked() throws Exception {
        User user = doCreate();
        user.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE).get(0).getId());
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setSecondaryStatus(UserStatusEnum.LOCKED);

        if (CollectionUtils.isNotEmpty(searchOrg())){
            for (Organization o : searchOrg()){
                o.setOperation(AttributeOperationEnum.ADD);
                user.getAffiliations().add(o);
            }
        }

        saveAndAssert(user);
        User foundUser = getAndAssert(user.getId());
        Assert.assertEquals(user.getFirstName(), foundUser.getFirstName());
        Assert.assertEquals(user.getLastName(), foundUser.getLastName());
        Assert.assertEquals(user.getMdTypeId(), foundUser.getMdTypeId());
        Assert.assertEquals(user.getStatus(), foundUser.getStatus());
        Assert.assertEquals(user.getSecondaryStatus(), foundUser.getSecondaryStatus());
        Assert.assertEquals(user.getAffiliations(), foundUser.getAffiliations());
    }

    private void createOpeniamUserDeactive() throws Exception {
        User user = doCreate();
        user.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE).get(0).getId());
        user.setStatus(UserStatusEnum.DELETED);
        user.setSecondaryStatus(UserStatusEnum.INACTIVE);

        if (CollectionUtils.isNotEmpty(searchOrg())){
            for (Organization o : searchOrg()){
                o.setOperation(AttributeOperationEnum.ADD);
                user.getAffiliations().add(o);
            }
        }

        saveAndAssert(user);
        User foundUser = getAndAssert(user.getId());
        Assert.assertEquals(user.getFirstName(), foundUser.getFirstName());
        Assert.assertEquals(user.getLastName(), foundUser.getLastName());
        Assert.assertEquals(user.getMdTypeId(), foundUser.getMdTypeId());
        Assert.assertEquals(user.getStatus(), foundUser.getStatus());
        Assert.assertEquals(user.getSecondaryStatus(), foundUser.getSecondaryStatus());
        Assert.assertEquals(user.getAffiliations(), foundUser.getAffiliations());
    }

}
