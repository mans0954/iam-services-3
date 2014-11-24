package org.openiam.access.review.strategy.entitlements;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.access.review.model.AccessViewBean;
import org.openiam.authmanager.common.model.AbstractAuthorizationEntity;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.access.review.constant.AccessReviewData;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;

import java.util.HashSet;
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
            Set<String> rolesIds = this.getCompiledRoles();
           // rolesIds.addAll(getCompiledRolesForResource(parent.getId()));
            if(CollectionUtils.isNotEmpty(rolesIds)){
                for (String roleId : rolesIds) {
                    AuthorizationRole role = accessReviewData.getMatrix().getRoleMap().get(roleId);
                    if(StringUtils.isNotBlank(role.getManagedSysId())
                       && mngsys.getId().equals(role.getManagedSysId())){
                        AccessViewBean bean = this.createBean(role);
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
            Set<String> groupIds = this.getCompiledGroups();
            //groupIds.addAll(getCompiledGroupsForResource(parent.getId()));
            if(CollectionUtils.isNotEmpty(groupIds)){
                for (String groupId : groupIds) {
                    AuthorizationGroup group = accessReviewData.getMatrix().getGroupMap().get(groupId);
                    if(StringUtils.isNotBlank(group.getManagedSysId())
                       && mngsys.getId().equals(group.getManagedSysId())){

                        AccessViewBean bean = this.createBean(group);
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
            return getResourceBeans(this.getUserEntitlements());
        }
        return getResourceBeans(accessReviewData.getMatrix().getResourceToResourceMap().get(parent.getId()));
    }

    @Override
    public boolean isDirectEntitled(AbstractAuthorizationEntity entity){
        if(CollectionUtils.isNotEmpty(this.accessReviewData.getMatrix().getResourceIds()))
            return this.accessReviewData.getMatrix().getResourceIds().contains(entity.getId());
        return false;
    }
}
