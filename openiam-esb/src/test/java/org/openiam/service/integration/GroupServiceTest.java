package org.openiam.service.integration;

import org.apache.commons.collections.CollectionUtils;
import org.junit.AfterClass;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.service.AuthenticationService;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GroupServiceTest extends AbstractMetadataTypeServiceTest<Group, GroupSearchBean> {

    @Autowired
    @Qualifier("groupServiceClient")
    private GroupDataWebService groupServiceClient;
    
    private Group group = null;

    @BeforeClass
    protected void setUp() throws Exception {
       
    }

    @Test
    public void createGroupTest() throws Exception {
    	group = createBean();
    	Response response = groupServiceClient.saveGroup(group, null);
    	Assert.assertTrue(response.isSuccess(), String.format("Could not save group.  %s", response));
    	group.setId((String)response.getResponseValue());
    	
    	final GroupSearchBean searchBean = new GroupSearchBean();
    	searchBean.setDeepCopy(false);
    	searchBean.setName(group.getName());
    	
    	Thread.sleep(2000L);
    	List<Group> resultListNode1 = groupServiceClient.findBeansLocalize(searchBean, null, 0, 1, null);
    	List<Group> resultListNode2 = groupServiceClient.findBeansLocalize(searchBean, null, 0, 1, null);
    	Assert.assertTrue(CollectionUtils.isNotEmpty(resultListNode1));
    	Assert.assertEquals(resultListNode1, resultListNode2, String.format("Multiclustered hit failed"));
    	
    	group = resultListNode1.get(0);
    	group.setName(getRandomName());
    	response = groupServiceClient.saveGroup(group, null);
    	Assert.assertTrue(response.isSuccess(), String.format("Could not save group.  %s", response));
    	
    	searchBean.setName(group.getName());
    	Thread.sleep(2000L);
    	resultListNode1 = groupServiceClient.findBeansLocalize(searchBean, null, 0, 1, null);
    	resultListNode2 = groupServiceClient.findBeansLocalize(searchBean, null, 0, 1, null);
    	Assert.assertTrue(CollectionUtils.isNotEmpty(resultListNode1));
    	Assert.assertEquals(resultListNode1, resultListNode2, String.format("Multiclustered hit failed"));
    }
    
    @AfterClass
    public void tearDown() throws Exception {
    	if(group != null && group.getId() != null) {
    		groupServiceClient.deleteGroup(group.getId(), null);
    	}
    }

	@Override
	protected Group newInstance() {
		return new Group();
	}
}
