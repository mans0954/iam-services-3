package org.openiam.service.integration.provisioning;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.openiam.base.ws.Response;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    protected Response save(User user) throws Exception {
        ProvisionUserResponse userResponse = null;
        if(StringUtils.isNotBlank(user.getId())){
            userResponse = provisionService.modifyUser(new ProvisionUser(user));
        } else {
            userResponse = provisionService.addUser(new ProvisionUser(user));
        }

        Assert.assertTrue(userResponse.isSuccess());
        Assert.assertNotNull(userResponse.getUser());
        Assert.assertNotNull(userResponse.getUser().getId());

        return userResponse;
    }

    @Override
    protected Response delete(User user) {
        return null;
    }

    @Override
    protected User get(String key) {
        UserSearchBean userSearchBean = newSearchBean();
        userSearchBean.setKey(key);
        userSearchBean.setDeepCopy(true);
        userSearchBean.setInitDefaulLogin(true);
        List<User> userList = this.find(userSearchBean, 0,1);
        Assert.assertTrue(CollectionUtils.isNotEmpty(userList));
        return userList.get(0);
    }

    @Override
    public List<User> find(UserSearchBean searchBean, int from, int size) {
        List<User> userList = userServiceClient.findBeans(searchBean, from, size);
        Assert.assertTrue(CollectionUtils.isNotEmpty(userList));
        return userList;
    }

    @Override
    protected String getId(User bean) {
        return null;
    }

    @Override
    protected void setId(User bean, String id) {

    }

    @Override
    protected void setName(User bean, String name) {

    }

    @Override
    protected String getName(User bean) {
        return null;
    }

    @Override
    protected void setNameForSearch(UserSearchBean searchBean, String name) {

    }
}
