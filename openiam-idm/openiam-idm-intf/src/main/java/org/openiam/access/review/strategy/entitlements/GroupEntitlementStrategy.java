package org.openiam.access.review.strategy.entitlements;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.access.review.model.AccessViewBean;
import org.openiam.authmanager.common.model.AbstractAuthorizationEntity;
import org.openiam.access.review.constant.AccessReviewData;

import java.util.Collections;
import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 5/29/14.
 */
public class GroupEntitlementStrategy extends EntitlementsStrategy {
    public GroupEntitlementStrategy(AccessReviewData accessReviewData) {
        super(accessReviewData);
    }

    @Override
    public Set<AccessViewBean> getRoles(AccessViewBean parent) {
        Set<String> childIds = accessReviewData.getMatrix().getGroupToRoleMap().get(parent.getId());
        Set<String> entitledIds =accessReviewData.getMatrix().getRoleIds();

        if(CollectionUtils.isNotEmpty(childIds)
           && CollectionUtils.isNotEmpty(entitledIds)){
            childIds.retainAll(entitledIds);
            return getRoleBeans(childIds);
        }
        return Collections.EMPTY_SET;
    }

    @Override
    public Set<AccessViewBean> getGroups(AccessViewBean parent) {
        Set<String> childIds = accessReviewData.getMatrix().getGroupToGroupMap().get(parent.getId());
        Set<String> entitledIds =accessReviewData.getMatrix().getGroupIds();

        if(CollectionUtils.isNotEmpty(childIds)
                && CollectionUtils.isNotEmpty(entitledIds)){
            childIds.retainAll(entitledIds);
            return getGroupBeans(childIds);
        }
        return Collections.EMPTY_SET;
    }

    @Override
    public Set<AccessViewBean> getResources(AccessViewBean parent) {
        return getResourceBeans(getCompiledResourcesForGroup(parent.getId()));
    }

    @Override
    public boolean isDirectEntitled(AbstractAuthorizationEntity entity){
        if(CollectionUtils.isNotEmpty(this.accessReviewData.getMatrix().getResourceIds()))
            return this.accessReviewData.getMatrix().getGroupIds().contains(entity.getId());
        return false;
    }
}
