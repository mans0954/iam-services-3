package org.openiam.access.review.strategy.entitlements;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.openiam.access.review.model.AccessViewBean;
import org.openiam.authmanager.common.model.AbstractAuthorizationEntity;
import org.openiam.access.review.constant.AccessReviewData;
import org.openiam.authmanager.common.model.AuthorizationAccessRight;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.common.xref.AbstractGroupXref;
import org.openiam.authmanager.common.xref.AbstractResourceXref;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
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
            return getRoleBeans(accessReviewData.getMatrix().getDirectRoleIds().keySet());
        }
        // children roles
        Set<String> childrenIds = accessReviewData.getMatrix().getRoleToRoleMap().get(parent.getId()).keySet();
        Set<String> roleIds = accessReviewData.getMatrix().getDirectRoleIds().keySet();

        if(CollectionUtils.isNotEmpty(childrenIds)
                && CollectionUtils.isNotEmpty(roleIds)){
            childrenIds.retainAll(roleIds);
            return getGroupBeans(childrenIds);
        }

        return Collections.EMPTY_SET;
    }

    @Override
    public Set<AccessViewBean> getGroups(AccessViewBean parent) {
        AuthorizationRole role = accessReviewData.getMatrix().getRoleMap().get(parent.getId());
        // compiled groups
        Set<AbstractGroupXref> groupXref = role.visitGroups(new HashSet<AuthorizationRole>());
        Set<String> groupsIds = new HashSet<>();
        if(CollectionUtils.isNotEmpty(groupXref)){
            groupXref.forEach(xref ->{
                groupsIds.add(xref.getGroup().getId());
            });
        }
        // direct groups
        Set<String> directGroupsIds = accessReviewData.getMatrix().getDirectGroupIds().keySet();

        if(CollectionUtils.isNotEmpty(groupsIds)
                && CollectionUtils.isNotEmpty(directGroupsIds)){

            groupsIds.retainAll(directGroupsIds);
            return getGroupBeans(groupsIds);
        }
        return Collections.EMPTY_SET;
    }



    @Override
    public Set<AccessViewBean> getResources(AccessViewBean parent) {
        AuthorizationRole role = accessReviewData.getMatrix().getRoleMap().get(parent.getId());
        Set<AbstractResourceXref> resourcesXref = role.visitResources(new HashSet<AuthorizationRole>());

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
        if(MapUtils.isNotEmpty(this.accessReviewData.getMatrix().getDirectRoleIds()))
            return this.accessReviewData.getMatrix().getDirectRoleIds().containsKey(entity.getId());
        return false;
    }
}
