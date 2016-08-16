package org.openiam.access.review.strategy.entitlements;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.model.AccessViewBean;
import org.openiam.am.srvc.dto.jdbc.AbstractAuthorizationEntity;
import org.openiam.am.srvc.dto.jdbc.AuthorizationGroup;
import org.openiam.am.srvc.dto.jdbc.AuthorizationRole;
import org.openiam.access.review.constant.AccessReviewData;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 5/29/14.
 */
public class ResourceEntitlementStrategy extends EntitlementsStrategy {
    public ResourceEntitlementStrategy(AccessReviewData accessReviewData) {
        super(accessReviewData);
    }

    @Override
    public Set<AccessViewBean> getRoles(AccessViewBean parent) {
        Set<AccessViewBean> retVal = new HashSet<>();
        ManagedSysEntity mngsys = accessReviewData.getMngsysMap().get(parent.getId());
        if(mngsys!=null){
            Set<String> rolesIds = accessReviewData.getMatrix().getCompiledRoleIds().keySet();
            if(CollectionUtils.isNotEmpty(rolesIds)){
                for (String roleId : rolesIds) {
                    AuthorizationRole role = accessReviewData.getMatrix().getRoleMap().get(roleId);
                    if(StringUtils.isNotBlank(role.getManagedSysId())
                       && mngsys.getId().equals(role.getManagedSysId())){
                        AccessViewBean bean = createBean(role, accessReviewData.getMatrix().getCompiledRoleIds().get(roleId));
                        retVal.add(bean);
                    }
                }
            }
        }
        return retVal;
    }

    @Override
    public Set<AccessViewBean> getGroups(AccessViewBean parent) {
        Set<AccessViewBean> retVal = new HashSet<>();
        ManagedSysEntity mngsys = accessReviewData.getMngsysMap().get(parent.getId());
        if(mngsys!=null){
            Set<String> groupsIds = accessReviewData.getMatrix().getCompiledGroupIds().keySet();
            //groupIds.addAll(getCompiledGroupsForResource(parent.getId()));
            if(CollectionUtils.isNotEmpty(groupsIds)){
                for (String groupId : groupsIds) {
                    AuthorizationGroup group = accessReviewData.getMatrix().getGroupMap().get(groupId);
                    if(StringUtils.isNotBlank(group.getManagedSysId())
                       && mngsys.getId().equals(group.getManagedSysId())){
                        AccessViewBean bean = createBean(group, accessReviewData.getMatrix().getCompiledGroupIds().get(groupId));
                        retVal.add(bean);
                    }
                }
            }
        }
        return retVal;
    }



    @Override
    public Set<AccessViewBean> getResources(AccessViewBean parent) {
        if(parent==null){
            return getResourceBeans((MapUtils.isNotEmpty(accessReviewData.getMatrix().getCompiledResourceIds()))?accessReviewData.getMatrix().getCompiledResourceIds().keySet():null);
        }
        Map<String, Set<String>> childrenResources = accessReviewData.getMatrix().getResourceToResourceMap().get(parent.getId());
        if(MapUtils.isNotEmpty(childrenResources)) {
            return getResourceBeans(accessReviewData.getMatrix().getResourceToResourceMap().get(parent.getId()).keySet());
        }
        return null;
    }

    @Override
    public boolean isDirectEntitled(AbstractAuthorizationEntity entity){
        if(MapUtils.isNotEmpty(this.accessReviewData.getMatrix().getDirectResourceIds()))
            return this.accessReviewData.getMatrix().getDirectResourceIds().containsKey(entity.getId());
        return false;
    }
}
