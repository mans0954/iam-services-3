package org.openiam.access.review.strategy.entitlements;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.openiam.access.review.model.AccessViewBean;
import org.openiam.authmanager.common.model.AbstractAuthorizationEntity;
import org.openiam.access.review.constant.AccessReviewData;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.xref.AbstractResourceXref;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
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
    	return Collections.EMPTY_SET;
    }

    @Override
    public Set<AccessViewBean> getGroups(AccessViewBean parent) {
        // children groups

        Map<String, Set<String>> childrenGrp = accessReviewData.getMatrix().getGroupToGroupMap().get(parent.getId());
        Set<String> childrenIds = null;
        if(MapUtils.isNotEmpty(childrenGrp)) {
            childrenIds = childrenGrp.keySet();
        }

        Set<String> directGroupIds = (MapUtils.isNotEmpty(accessReviewData.getMatrix().getDirectGroupIds()))?accessReviewData.getMatrix().getDirectGroupIds().keySet():null;

        if(CollectionUtils.isNotEmpty(childrenIds)
                && CollectionUtils.isNotEmpty(directGroupIds)){
            childrenIds.retainAll(directGroupIds);
            return getGroupBeans(childrenIds);
        }
        return Collections.EMPTY_SET;
    }

    @Override
    public Set<AccessViewBean> getResources(AccessViewBean parent) {

        AuthorizationGroup group = accessReviewData.getMatrix().getGroupMap().get(parent.getId());
        Set<AbstractResourceXref> resourcesXref = group.visitResources(new HashSet<AuthorizationGroup>());

        Set<String> resourceIds = new HashSet<>();
        if(CollectionUtils.isNotEmpty(resourcesXref)){
            resourcesXref.forEach(xref ->{
                resourceIds.add(xref.getResource().getId());
            });
        }
        return getResourceBeans(resourceIds);
    }

    @Override
    public boolean isDirectEntitled(AbstractAuthorizationEntity entity){
        if(MapUtils.isNotEmpty(this.accessReviewData.getMatrix().getDirectGroupIds()))
            return this.accessReviewData.getMatrix().getDirectGroupIds().containsKey(entity.getId());
        return false;
    }
}
