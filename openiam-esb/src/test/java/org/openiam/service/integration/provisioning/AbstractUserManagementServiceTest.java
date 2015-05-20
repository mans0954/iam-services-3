package org.openiam.service.integration.provisioning;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.testng.Assert;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.SearchParam;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.ProvisionService;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.*;

/**
 * Created by alexander on 14.05.15.
 */
public abstract class AbstractUserManagementServiceTest extends AbstractKeyNameServiceTest<User, UserSearchBean> {
    protected MetadataType userType = null;
    protected Map<String, MetadataElement> defaultUserAttributes = new HashMap<>();

    @Autowired
    @Qualifier("provisionServiceClient")
    protected ProvisionService provisionService;

    @BeforeClass
    public void _init() {
        userType = new MetadataType();
        userType.setDescription("TEST_USER");
        userType.setGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE);

        Response wsResponse = metadataServiceClient.saveMetadataType(userType);

        Assert.assertTrue(wsResponse.isSuccess());
        Assert.assertNotNull(wsResponse.getResponseValue());

        userType.setId((String)wsResponse.getResponseValue());

        createMetaDataElement("DRIVERS_LICENSE", true);
        createMetaDataElement("USER_BIRTH_YEAR", true);
        createMetaDataElement("FAVORITE_FOODS", true);
        createMetaDataElement("EMERGENCY_CONTACT", false);
        createMetaDataElement(getRandomName(), false);

    }
    @AfterClass
    public void _destroy() {

        if(!defaultUserAttributes.isEmpty()){
            for(String attrName: defaultUserAttributes.keySet()){
                MetadataElement attr = defaultUserAttributes.get(attrName);
                metadataServiceClient.deleteMetadataElement(attr.getId());
            }
        }

        if(userType!=null){
            metadataServiceClient.deleteMetadataType(userType.getId());
        }

    }

    private void createMetaDataElement(String name, boolean required){
        MetadataElement attr = new MetadataElement();
        attr.setAttributeName(name);
        attr.setMetadataTypeId(userType.getId());
        attr.setDescription(name);
        attr.setRequired(required);

        attr.setStaticDefaultValue(getRandomName());

        Response wsResponse = metadataServiceClient.saveMetadataEntity(attr);

        Assert.assertTrue(wsResponse.isSuccess());
        Assert.assertNotNull(wsResponse.getResponseValue());

        attr.setId((String)wsResponse.getResponseValue());
        defaultUserAttributes.put(name, attr);

    }

    @Override
    protected User newInstance() {
        return new User();
    }

    @Override
    protected UserSearchBean newSearchBean() {
        return new UserSearchBean();
    }

    protected Response saveAndAssert(User user) throws Exception {
        final Response response = save(user);
        Assert.assertTrue(response.isSuccess(), String.format("Could not save entity.  %s", response));

        ProvisionUserResponse userResponse = (ProvisionUserResponse)response;
        Assert.assertNotNull(userResponse.getUser(), String.format("Could not save entity.  %s", userResponse));
        Assert.assertNotNull(userResponse.getUser().getId(), String.format("Could not save entity.  %s", userResponse));
        return response;
    }

    @Override
    protected Response save(User user) throws Exception {
        ProvisionUserResponse userResponse = null;
        if(StringUtils.isNotBlank(user.getId())){
            userResponse = provisionService.modifyUser(new ProvisionUser(user));
        } else {
            userResponse = provisionService.addUser(new ProvisionUser(user));
        }
        return userResponse;
    }

    @Override
    protected Response delete(User user) {
        ProvisionUser pUser = new ProvisionUser(user);
        pUser.setStatus(UserStatusEnum.REMOVE);

        return provisionService.modifyUser(pUser);
//        return provisionService.deleteByUserId(user.getId(), UserStatusEnum.REMOVE, "3000");
    }

    protected User getAndAssert(String key){
        User user = get(key);
        Assert.assertNotNull(user, String.format("Could not find entity for key: %s", key));
        return user;
    }

    @Override
    protected User get(String key) {
        User user =null;
        UserSearchBean userSearchBean = newSearchBean();
        userSearchBean.setKey(key);
        userSearchBean.setDeepCopy(true);
        userSearchBean.setInitDefaulLogin(true);
        List<User> userList = this.find(userSearchBean, 0,1);

        if(CollectionUtils.isNotEmpty(userList))
            user = userList.get(0);

        return user;
    }

    @Override
    public List<User> find(UserSearchBean searchBean, int from, int size) {
        List<User> userList = userServiceClient.findBeans(searchBean, from, size);
        return userList;
    }

    @Override
    protected String getId(User bean) {
        return bean.getId();
    }

    @Override
    protected void setId(User bean, String id) {
        bean.setId(id);
    }

    @Override
    protected void setName(User bean, String name) {
        bean.setFirstName(name);
    }

    @Override
    protected String getName(User bean) {
        return bean.getFirstName();
    }

    @Override
    protected void setNameForSearch(UserSearchBean searchBean, String name) {
        searchBean.setFirstNameMatchToken(new SearchParam(name, MatchType.EXACT));
    }


    protected Date removeMillis(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MILLISECOND,0);
        return c.getTime();
    }

    protected Date getDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MILLISECOND,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.HOUR_OF_DAY,0);
        return c.getTime();
    }
}
