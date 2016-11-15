package org.openiam.service.integration.entitlements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;
import org.openiam.srvc.am.RoleDataWebService;
import org.openiam.service.integration.AbstractAttributeServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

public class RoleServiceTest extends AbstractAttributeServiceTest<Role, RoleSearchBean, RoleAttribute> {

	@Autowired
	@Qualifier("roleServiceClient")
	protected RoleDataWebService roleServiceClient;
	
	@Override
	protected RoleAttribute createAttribute(Role t) {
		final RoleAttribute attribute = new RoleAttribute();
		attribute.setRoleId(t.getId());
		return attribute;
	}

	@Override
	protected Set<RoleAttribute> createAttributeSet() {
		return new HashSet<>();
	}

	@Override
	protected void setAttributes(Role t, Set<RoleAttribute> attributes) {
		t.setRoleAttributes(attributes);
	}

	@Override
	protected Set<RoleAttribute> getAttributes(Role t) {
		return t.getRoleAttributes();
	}

	@Override
	protected Role newInstance() {
		final Role dto = new Role();
		dto.setPolicyId(getPasswordPolicy().getId());
		return dto;
	}

	@Override
	protected RoleSearchBean newSearchBean() {
		return new RoleSearchBean();
	}

	@Override
	protected Response save(Role t) {
		return roleServiceClient.saveRole(t, null);
	}

	@Override
	protected Response delete(Role t) {
		return (t != null) ? roleServiceClient.removeRole(t.getId(), null) : null;
	}

	@Override
	protected Role get(String key) {
		return roleServiceClient.getRoleLocalized(key, null, null);
	}

	@Override
	public List<Role> find(RoleSearchBean searchBean, int from, int size) {
		return roleServiceClient.findBeans(searchBean, null, from, size);
	}

	@Test
	public void testContraintViolationWithNoManagedSystem() {
		final String name = getRandomName();
		Role r1 = newInstance();
		Role r2 = newInstance();
		
		r1.setName(name);
		r2.setName(name);
		Response response = roleServiceClient.saveRole(r1, getRequestorId());
		assertSuccess(response);
		response = roleServiceClient.saveRole(r2, getRequestorId());
		assertResponseCode(response, ResponseCode.CONSTRAINT_VIOLATION);
	}
	
	@Test
	public void testContraintViolationWithManagedSystem() {
		final String name = getRandomName();
		final String managedSystemId = getDefaultManagedSystemId();
		Role r1 = newInstance();
		Role r2 = newInstance();
		
		r1.setName(name);
		r1.setManagedSysId(managedSystemId);
		r2.setName(name);
		r2.setManagedSysId(managedSystemId);
		Response response = roleServiceClient.saveRole(r1, getRequestorId());
		assertSuccess(response);
		response = roleServiceClient.saveRole(r2, getRequestorId());
		assertResponseCode(response, ResponseCode.CONSTRAINT_VIOLATION);
	}
	
	@Test
	public void testContraintViolationDifferentManagedSystem() {
		final String name = getRandomName();
		Role r1 = newInstance();
		Role r2 = newInstance();
		
		r1.setName(name);
		r1.setManagedSysId(managedSysServiceClient.getManagedSystems(null, 0, 10).get(0).getId());
		r2.setName(name);
		r2.setManagedSysId(managedSysServiceClient.getManagedSystems(null, 0, 10).get(1).getId());
		Response response = roleServiceClient.saveRole(r1, getRequestorId());
		assertSuccess(response);
		response = roleServiceClient.saveRole(r2, getRequestorId());
		assertSuccess(response);
	}

/*	@Override
	protected String getName(Role bean) {
		return bean.getName();
	}

	@Override
	protected void setNameForSearch(RoleSearchBean searchBean, String name) {
		searchBean.setName(name);
	}*/

}
