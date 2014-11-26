package org.openiam.access.review.strategy.entitlements;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.access.review.model.AccessViewBean;
import org.openiam.authmanager.common.model.AbstractAuthorizationEntity;
import org.openiam.access.review.constant.AccessReviewData;

import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 5/29/14.
 */
public class RoleEntitlementStrategy extends EntitlementsStrategy {
    public RoleEntitlementStrategy(AccessReviewData accessReviewData) {
        super(accessReviewData);
    }

    @Override
    public Set<AccessViewBean> getRoles(AccessViewBean parent) {
        if(parent==null){
            return getRoleBeans(accessReviewData.getMatrix().getRoleIds());
        }
        return getRoleBeans(accessReviewData.getMatrix().getRoleToRoleMap().get(parent.getId()));
    }

    @Override
    public Set<AccessViewBean> getGroups(AccessViewBean parent) {
        return getGroupBeans(accessReviewData.getMatrix().getRoleToGroupMap().get(parent.getId()));
    }



    @Override
    public Set<AccessViewBean> getResources(AccessViewBean parent) {
        return getResourceBeans(getCompiledResourcesForRole(parent.getId()));
    }

    @Override
    public boolean isDirectEntitled(AbstractAuthorizationEntity entity){
        if(CollectionUtils.isNotEmpty(this.accessReviewData.getMatrix().getRoleIds()))
            return this.accessReviewData.getMatrix().getRoleIds().contains(entity.getId());
        return false;
    }
}
