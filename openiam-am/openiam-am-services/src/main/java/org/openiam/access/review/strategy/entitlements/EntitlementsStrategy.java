package org.openiam.access.review.strategy.entitlements;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.model.AccessViewBean;
import org.openiam.am.srvc.dto.jdbc.AbstractAuthorizationEntity;
import org.openiam.am.srvc.dto.jdbc.AuthorizationGroup;
import org.openiam.am.srvc.dto.jdbc.AuthorizationResource;
import org.openiam.am.srvc.dto.jdbc.AuthorizationRole;
import org.openiam.constants.AccessReviewConstant;
import org.openiam.access.review.constant.AccessReviewData;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;

import java.util.*;

/**
 * Created by: Alexander Duckardt
 * Date: 5/29/14.
 */
public abstract class EntitlementsStrategy {
    protected AccessReviewData accessReviewData;

    public EntitlementsStrategy(AccessReviewData accessReviewData){
        this.accessReviewData=accessReviewData;
    }

    protected void setIdentityForResource(AccessViewBean bean, ManagedSysDto mngsys, List<LoginEntity> loginList){
        if(CollectionUtils.isNotEmpty(loginList) && mngsys!=null) {
            for(final LoginEntity login : loginList) {
                if(mngsys.getId().equals(login.getManagedSysId())){
                    bean.setIdentity(login.getLogin());
                    bean.setLoginId(login.getId());
                    break;
                }
            }
        }
    }

    protected Set<AccessViewBean> getRoleBeans(Set<String> roleIds){
        Set<AccessViewBean> retVal=new HashSet<>();
        if(CollectionUtils.isNotEmpty(roleIds)){
            for(String roleId : roleIds){
                AuthorizationRole role = accessReviewData.getMatrix().getRoleMap().get(roleId);
                if(role!=null){
                    AccessViewBean bean = createBean(role, accessReviewData.getMatrix().getCompiledRoleIds().get(roleId));
                    retVal.add(bean);
                }
            }
        }
        return retVal;
    }


    protected Set<AccessViewBean> getGroupBeans(Set<String> groupIds){
        Set<AccessViewBean> retVal=new HashSet<AccessViewBean>();
        if(CollectionUtils.isNotEmpty(groupIds)){
            for(String groupId : groupIds){
                AuthorizationGroup group = accessReviewData.getMatrix().getGroupMap().get(groupId);
                if(group!=null){
                    AccessViewBean bean = createBean(group, accessReviewData.getMatrix().getCompiledGroupIds().get(groupId));
                    retVal.add(bean);
                }
            }
        }
        return retVal;
    }

    protected Set<AccessViewBean> getResourceBeans(Set<String> resourceIds){
        Set<AccessViewBean> retVal=new HashSet<AccessViewBean>();
        if(CollectionUtils.isNotEmpty(resourceIds)){
            for(String resourceId : resourceIds){
                AuthorizationResource resource = accessReviewData.getMatrix().getResourceMap().get(resourceId);

                if(resource!=null){
                    AccessViewBean bean = createBean(resource, accessReviewData.getMatrix().getCompiledResourceIds().get(resourceId));

                    if("MANAGED_SYS".equals(resource.getResourceTypeId())){
                        bean.setManagedSys(accessReviewData.getMngsysMap().get(resource.getId()).getId());
                        setIdentityForResource(bean, accessReviewData.getMngsysMap().get(resource.getId()),
                                               accessReviewData.getLoginList());
                    }
                    retVal.add(bean);
                }

            }
        }
        return retVal;
    }

    public static AccessViewBean createBean(AuthorizationRole entity, Set<String> rightSet){
//        Map.Entry<AuthorizationRole, Set<AuthorizationAccessRight>> data = entity.entrySet().iterator().next();
        return createBean(entity.getId(), entity.getName(), entity.getDescription(), entity.getStatus(), entity.getManagedSysId(),
                          AccessReviewConstant.ROLE_TYPE, null, null, rightSet);
    }

    public static  AccessViewBean createBean(AuthorizationGroup entity, Set<String> rightSet){
//        Map.Entry<AuthorizationGroup, Set<AuthorizationAccessRight>> data = entity.entrySet().iterator().next();
        return createBean(entity.getId(), entity.getName(), entity.getDescription(), entity.getStatus(), entity.getManagedSysId(),
                          AccessReviewConstant.GROUP_TYPE, null, null, rightSet);
    }

    public static  AccessViewBean createBean(AuthorizationResource entity, Set<String> rightSet){
        return createBean(entity.getId(), entity.getName(), entity.getDescription(), entity.getStatus(), entity.getManagedSysId(),
                AccessReviewConstant.RESOURCE_TYPE, entity.getResourceTypeId(), entity.getRisk(), rightSet);
    }

    public static  AccessViewBean createBean(String id, String name, String description, String status, String managedSys, String type, String resourceTypeId, String risk, Set<String> rightSet){
        AccessViewBean bean =  new AccessViewBean(id, name, description);
        bean.setStatus(status);
        bean.setManagedSys(managedSys);
        bean.setBeanType(type);
        bean.setRisk(risk);
        bean.setResourceTypeId(resourceTypeId);
        bean.addAccessRights(rightSet);
        return bean;
    }

    public abstract boolean isDirectEntitled(AbstractAuthorizationEntity entity);


    public abstract Set<AccessViewBean> getRoles(AccessViewBean parent);
    public abstract Set<AccessViewBean> getGroups(AccessViewBean parent);
    public abstract Set<AccessViewBean> getResources(AccessViewBean parent);
}
