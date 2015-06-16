package org.openiam.service.integration.entitlements;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.service.integration.AbstractEntitlementsTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class Role2RoleEntitlementsTest extends AbstractEntitlementsTest<Role, Role> {

	@Override
	protected Role createParent() {
		return createRole();
	}

	@Override
	protected Role createChild() {
		return createRole();
	}

	@Override
	protected Response addChildToParent(Role parent, Role child) {
		return roleServiceClient.addChildRole(parent.getId(), child.getId(), null);
	}

	@Override
	protected Response removeChildFromParent(Role parent, Role child) {
		return roleServiceClient.removeChildRole(parent.getId(), child.getId(), null);
	}

	@Override
	protected Response deleteParent(Role parent) {
		return roleServiceClient.removeRole(parent.getId(), null);
	}

	@Override
	protected Response deleteChild(Role child) {
		return roleServiceClient.removeRole(child.getId(), null);
	}

	@Override
	protected boolean isChildInParent(Role parent, Role child) {
		final RoleSearchBean searchBean = new RoleSearchBean();
        searchBean.addParentId(parent.getId());
		
		final List<Role> roles = roleServiceClient.findBeans(searchBean, null, 0, 100);
		boolean retVal = false;
		if(CollectionUtils.isNotEmpty(roles)) {
			retVal = roles.contains(child);
		}
		return retVal;
	}

	@Override
	protected boolean parentHasChild(final Role parent, final Role child) {
		final RoleSearchBean searchBean = new RoleSearchBean();
		searchBean.addChildId(child.getId());
		
		final List<Role> roles = roleServiceClient.findBeans(searchBean, null, 0, 100);
		boolean retVal = false;
		if(CollectionUtils.isNotEmpty(roles)) {
			retVal = roles.contains(parent);
		}
		return retVal;
	}
}
