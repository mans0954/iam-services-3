package org.openiam.service.integration.entitlements;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.service.integration.AbstractEntitlementsTest;

import java.util.List;

public class Role2ResourceEntitlementsTest extends AbstractEntitlementsTest<Role, Resource> {

    @Override
    protected Role createParent() {
        return super.createRole();
    }

    @Override
    protected Resource createChild() {
        return super.createResource();
    }

    @Override
    protected Response addChildToParent(Role parent, Resource child) {
        return resourceDataService.addRoleToResource(child.getId(), parent.getId(), null, null);
    }

    @Override
    protected Response removeChildFromParent(Role parent, Resource child) {
        return resourceDataService.removeRoleToResource(child.getId(), parent.getId(), null);
    }

    @Override
    protected Response deleteParent(Role parent) {
        return roleServiceClient.removeRole(parent.getId(), null);
    }

    @Override
    protected Response deleteChild(Resource child) {
        return resourceDataService.deleteResource(child.getId(), null);
    }

    @Override
    protected boolean isChildInParent(Role parent, Resource child) {
        final ResourceSearchBean searchBean = new ResourceSearchBean();
        searchBean.addRoleId(parent.getId());
        final List<Resource> resources = resourceDataService.findBeans(searchBean, 0, 100, getDefaultLanguage());
        return (CollectionUtils.isNotEmpty(resources)) ? resources.contains(child) : false;
    }

    @Override
    protected boolean parentHasChild(Role parent, Resource child) {
        final RoleSearchBean searchBean = new RoleSearchBean();
        searchBean.addResourceId(child.getId());
        final List<Role> roles = roleServiceClient.findBeans(searchBean, null, 0, 100);
        return (CollectionUtils.isNotEmpty(roles)) ? roles.contains(parent) : false;
    }

}
