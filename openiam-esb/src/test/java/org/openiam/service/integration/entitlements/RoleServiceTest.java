package org.openiam.service.integration.entitlements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.service.integration.AbstractAttributeServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
		return new Role();
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

	@Override
	protected String getId(Role bean) {
		return bean.getId();
	}

	@Override
	protected void setId(Role bean, String id) {
		bean.setId(id);
	}

	@Override
	protected void setName(Role bean, String name) {
		bean.setName(name);
	}

	@Override
	protected String getName(Role bean) {
		return bean.getName();
	}

	@Override
	protected void setNameForSearch(RoleSearchBean searchBean, String name) {
		searchBean.setName(name);
	}

}
