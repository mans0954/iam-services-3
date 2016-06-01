package org.openiam.service.integration.entitlements;

import org.apache.commons.collections.CollectionUtils;
import org.junit.AfterClass;
import org.mortbay.jetty.servlet.HashSessionIdManager;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.service.AuthenticationService;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.service.integration.AbstractAttributeServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GroupServiceTest extends AbstractAttributeServiceTest<Group, GroupSearchBean, GroupAttribute> {

    @Autowired
    @Qualifier("groupServiceClient")
    private GroupDataWebService groupServiceClient;
    
	@Autowired
	@Qualifier("organizationServiceClient")
	protected OrganizationDataService organizationServiceClient;

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

/*	@Override
	protected String getId(Group bean) {
		return bean.getId();
	}

	@Override
	protected void setId(Group bean, String id) {
		bean.setId(id);
	}

	@Override
	protected void setName(Group bean, String name) {
		bean.setName(name);
	}

	@Override
	protected String getName(Group bean) {
		return bean.getName();
	}

	@Override
	protected void setNameForSearch(GroupSearchBean searchBean, String name) {
		searchBean.setName(name);
	}*/


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
	public void saveGroupWithOrganizationsNew() {
		Group group = newInstance();
		try {
			group.setName(getRandomName());
			group.setDescription(getRandomName());
			final Organization org1 = organizationServiceClient.findBeansLocalized(null, null, 0, 1, getDefaultLanguage()).get(0);
			final Set<String> rightIds = accessRightServiceClient.findBeans(null, 0, Integer.MAX_VALUE, getDefaultLanguage()).stream().map(e -> e.getId()).collect(Collectors.toSet());
			group.addOrganization(org1, rightIds);
			group = saveAndAssert(org1, group, rightIds);
		} finally {
			if(group.getId() != null) {
				delete(group);
			}
		}
	}
	
	@Test
	public void saveGroupWithOrganizationsEdit() {
		Group group = newInstance();
		try {
			group.setName(getRandomName());
			group.setDescription(getRandomName());
			Response response = saveAndAssert(group);
			group = get((String)response.getResponseValue());
			final Organization org1 = organizationServiceClient.findBeansLocalized(null, null, 0, 1, getDefaultLanguage()).get(0);
			final Set<String> rightIds = accessRightServiceClient.findBeans(null, 0, Integer.MAX_VALUE, getDefaultLanguage()).stream().map(e -> e.getId()).collect(Collectors.toSet());
			final Set<String> modifiedRights = accessRightServiceClient.findBeans(null, 0, rightIds.size() - 1, getDefaultLanguage()).stream().map(e -> e.getId()).collect(Collectors.toSet());
			group.addOrganization(org1, rightIds);
			group = saveAndAssert(org1, group, rightIds);
			
			//update rights
			group.addOrganization(org1, modifiedRights);
			group = saveAndAssert(org1, group, modifiedRights);
			
			final Organization org2 = organizationServiceClient.findBeansLocalized(null, null, 1, 1, getDefaultLanguage()).get(0);
			group.addOrganization(org2, rightIds);
			group = saveAndAssert(org2, group, rightIds);
			
			group.addOrganization(org2, modifiedRights);
			group = saveAndAssert(org2, group, modifiedRights);
			
			final boolean wasRemoved = group.getOrganizations().removeIf(e -> e.getEntityId().equals(org2.getId()));
			Assert.assertTrue(wasRemoved, "org2 not removed - bug in test");
			group = saveAndAssert(org1, group, modifiedRights);
			Assert.assertTrue(group.getOrganizations().stream().filter(e -> e.getEntityId().equals(org2.getId())).count() == 0);
			
			group.getOrganizations().removeIf(e -> e.getEntityId().equals(org1.getId()));
			response = saveAndAssert(group);
			group = get((String)response.getResponseValue());
			Assert.assertTrue(CollectionUtils.isEmpty(group.getOrganizations()));
		} finally {
			if(group.getId() != null) {
				delete(group);
			}
		}
	}
	
	private Group saveAndAssert(final Organization organization, final Group group, final Set<String> rightIds) {
		final Response response = saveAndAssert(group);
		Assert.assertTrue(CollectionUtils.isNotEmpty(group.getOrganizations()));
		Assert.assertTrue(group.getOrganizations().stream().filter(o -> o.getEntityId().equals(organization.getId())).count() > 0);
		Assert.assertEquals(group.getOrganizations().stream().filter(o -> o.getEntityId().equals(organization.getId())).findFirst().get().getAccessRightIds(), rightIds);
		return get((String)response.getResponseValue());
	}
	

	@Test
	public void testContraintViolationWithNoManagedSystem() {
		final String name = getRandomName();
		Group r1 = newInstance();
		Group r2 = newInstance();
		
		r1.setName(name);
		r2.setName(name);
		Response response = groupServiceClient.saveGroup(r1, getRequestorId());
		assertSuccess(response);
		response = groupServiceClient.saveGroup(r2, getRequestorId());
		assertResponseCode(response, ResponseCode.CONSTRAINT_VIOLATION);
	}
	
	@Test
	public void testContraintViolationWithManagedSystem() {
		final String name = getRandomName();
		final String managedSystemId = getDefaultManagedSystemId();
		Group r1 = newInstance();
		Group r2 = newInstance();
		
		r1.setName(name);
		r1.setManagedSysId(managedSystemId);
		r2.setName(name);
		r2.setManagedSysId(managedSystemId);
		Response response = groupServiceClient.saveGroup(r1, getRequestorId());
		assertSuccess(response);
		response = groupServiceClient.saveGroup(r2, getRequestorId());
		assertResponseCode(response, ResponseCode.CONSTRAINT_VIOLATION);
	}
	
	@Test
	public void testContraintViolationDifferentManagedSystem() {
		final String name = getRandomName();
		Group r1 = newInstance();
		Group r2 = newInstance();
		
		r1.setName(name);
		r1.setManagedSysId(managedSysServiceClient.getManagedSystems(null, 0, 10).get(0).getId());
		r2.setName(name);
		r2.setManagedSysId(managedSysServiceClient.getManagedSystems(null, 0, 10).get(1).getId());
		Response response = groupServiceClient.saveGroup(r1, getRequestorId());
		assertSuccess(response);
		response = groupServiceClient.saveGroup(r2, getRequestorId());
		assertSuccess(response);
	}
}
