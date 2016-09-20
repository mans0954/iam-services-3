package org.openiam.service.integration.entitlements;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractEntitlementsTest;

public class Resource2ResourceEntitlementsTest extends AbstractEntitlementsTest<Resource, Resource> {

    @Override
    protected Resource createParent() {
        return super.createResource();
    }

    @Override
    protected Resource createChild() {
        return super.createResource();
    }

    @Override
    protected Response addChildToParent(Resource parent, Resource child) {
        return resourceDataService.addChildResource(parent.getId(), child.getId(), null, null);
    }

    @Override
    protected Response removeChildFromParent(Resource parent, Resource child) {
        return resourceDataService.deleteChildResource(parent.getId(), child.getId(), null);
    }

    @Override
    protected Response deleteParent(Resource parent) {
        return resourceDataService.deleteResource(parent.getId(), null);
    }

    @Override
    protected Response deleteChild(Resource child) {
        return resourceDataService.deleteResource(child.getId(), null);
    }

    @Override
    protected boolean isChildInParent(Resource parent, Resource child) {
        ResourceSearchBean searchBean = new ResourceSearchBean();
        searchBean.addChildId(child.getId());
        final List<Resource> resources = resourceDataService.findBeans(searchBean, 0, 100, getDefaultLanguage());
        return (CollectionUtils.isNotEmpty(resources)) ? resources.contains(parent) : false;
    }

    @Override
    protected boolean parentHasChild(Resource parent, Resource child) {
        ResourceSearchBean searchBean = new ResourceSearchBean();
        searchBean.addParentId(parent.getId());
        final List<Resource> resources = resourceDataService.findBeans(searchBean, 0, 100, getDefaultLanguage());
        return (CollectionUtils.isNotEmpty(resources)) ? resources.contains(child) : false;
    }

}
