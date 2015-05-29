package org.openiam.service.integration.provisioning;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
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
    protected List<String> userIdList = new ArrayList<>();
    protected Map<String, MetadataElement> defaultUserAttributes = new HashMap<>();

    protected String REQUESTER_ID="3000";
    protected String USER_TYPE="TEST_USER";
    protected String DRIVERS_LICENSE ="DRIVERS_LICENSE";
    protected String USER_BIRTH_YEAR ="USER_BIRTH_YEAR";
    protected String FAVORITE_FOODS ="FAVORITE_FOODS";
    protected String EMERGENCY_CONTACT ="EMERGENCY_CONTACT";
    protected String RANDOM_ATTRIBUTE =null;
    protected int NUMBER_OF_REQUIRED_ATTRIBUTES =0;

    @Autowired
    @Qualifier("provisionServiceClient")
    protected ProvisionService provisionService;

    @BeforeClass(alwaysRun = true)
    public void _init() {
        userType = new MetadataType();
        userType.setDescription(USER_TYPE);
        userType.setGrouping(MetadataTypeGrouping.USER_OBJECT_TYPE);
        userType.setDisplayNameMap(generateRandomLanguageMapping());

        Response wsResponse = metadataServiceClient.saveMetadataType(userType);

        Assert.assertTrue(wsResponse.isSuccess());
        Assert.assertNotNull(wsResponse.getResponseValue());

        userType.setId((String)wsResponse.getResponseValue());

        createMetaDataElement(DRIVERS_LICENSE, true);
        createMetaDataElement(USER_BIRTH_YEAR, true);
        createMetaDataElement(FAVORITE_FOODS, true);
        createMetaDataElement(EMERGENCY_CONTACT, false);
        RANDOM_ATTRIBUTE = getRandomName();
        createMetaDataElement(RANDOM_ATTRIBUTE, false);

    }
    @AfterClass(alwaysRun = true)
    public void _destroy() {

        if(CollectionUtils.isNotEmpty(userIdList)){
            while(!userIdList.isEmpty()){
                String userId = userIdList.get(0);
                this.delete(userId);
            }
        }

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

        if(required)
            NUMBER_OF_REQUIRED_ATTRIBUTES++;

    }
    @Override
    protected User createBean() {
        final User bean = super.createBean();
        bean.setFirstName(getRandomName());
        bean.setLastName(getRandomName());
        return bean;
    }

    protected User doCreate() throws Exception{
        User user = super.createBean();
        user = ((ProvisionUserResponse)saveAndAssert(user)).getUser();
        pushUserId(user.getId());
        return user;
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

    protected Response saveAndAssert(ProvisionUser pUser) throws Exception {
        final Response response = save(pUser);
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

    protected Response save(ProvisionUser pUser) throws Exception {
        ProvisionUserResponse userResponse = null;
        if(StringUtils.isNotBlank(pUser.getId())){
            userResponse = provisionService.modifyUser(pUser);
        } else {
            userResponse = provisionService.addUser(pUser);
        }
        return userResponse;
    }

    @Override
    protected Response delete(User user) {
        return delete(user.getId());
    }
    protected Response delete(String userId) {
        Response response = provisionService.deleteByUserId(userId, UserStatusEnum.REMOVE, REQUESTER_ID);
        if(response.isSuccess())
            dropUserId(userId);
        return response;
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

    protected void pushUserId(String userId){
        if(StringUtils.isNotBlank(userId))
            this.userIdList.add(userId);
    }
    protected String getUserId(){
        Assert.assertTrue(CollectionUtils.isNotEmpty(userIdList), "User is not created");
        return this.userIdList.get(0);
    }
    protected void dropUserId(String userId){
        Assert.assertTrue(CollectionUtils.isNotEmpty(userIdList), "User is not created");
        this.userIdList.remove(userId);
    }
}
