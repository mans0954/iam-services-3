package org.openiam.service.integration.entitlements;

import org.apache.commons.collections.CollectionUtils;
import org.junit.AfterClass;
import org.mortbay.jetty.servlet.HashSessionIdManager;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.service.AuthenticationService;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.service.integration.AbstractAttributeServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class GroupServiceTest extends AbstractAttributeServiceTest<Group, GroupSearchBean, GroupAttribute> {

    @Autowired
    @Qualifier("groupServiceClient")
    private GroupDataWebService groupServiceClient;

    @BeforeClass
    protected void _setUp() throws Exception {
       
    }

    @AfterClass
    public void _tearDown() throws Exception {
    	
    }

	@Override
	protected Group newInstance() {
		return new Group();
	}

	@Override
	protected Response save(Group t) {
		return groupServiceClient.saveGroup(t, null);
	}

	@Override
	protected GroupSearchBean newSearchBean() {
		return new GroupSearchBean();
	}

	@Override
	public List<Group> find(GroupSearchBean searchBean, int from, int size) {
		return groupServiceClient.findBeansLocalize(searchBean, null, from, size, null);
	}

	@Override
	protected Response delete(Group t) {
		return (t != null && t.getId() != null) ? groupServiceClient.deleteGroup(t.getId(), null) : null;
	}
	
	@Override
	protected Group get(String key) {
		return groupServiceClient.getGroup(key, null);
	}

	@Override
	protected GroupAttribute createAttribute(final Group group) {
		final GroupAttribute attribute = new GroupAttribute();
		attribute.setGroupId(group.getId());
		return attribute;
	}

	@Override
	protected Set<GroupAttribute> createAttributeSet() {
		final Set<GroupAttribute> set = new HashSet<>();
		return set;
	}

	@Override
	protected void setAttributes(Group t, Set<GroupAttribute> attributes) {
		t.setAttributes(attributes);
	}

	@Override
	protected Set<GroupAttribute> getAttributes(Group t) {
		return t.getAttributes();
	}
	
	@Test
	public void foo() {}
}
