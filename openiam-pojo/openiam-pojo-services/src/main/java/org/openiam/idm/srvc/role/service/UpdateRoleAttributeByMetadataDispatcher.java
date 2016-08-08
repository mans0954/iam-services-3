package org.openiam.idm.srvc.role.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.request.UpdateAttributeByMetadataRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.mq.dispatcher.UpdateAttributeByMetadataDispatcher;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by alexander on 29/07/16.
 */
@Component
public class UpdateRoleAttributeByMetadataDispatcher extends UpdateAttributeByMetadataDispatcher {
    @Autowired
    private RoleDataService roleDataService;

    @Override
    protected void process(UpdateAttributeByMetadataRequest request) {
        RoleSearchBean searchBean = new RoleSearchBean();
        searchBean.setType(request.getMetadataTypeId());
        List<RoleEntity> roleList = roleDataService.findBeans(searchBean,null, -1,-1);
        if(CollectionUtils.isNotEmpty(roleList)){
            for(RoleEntity role: roleList){
                Set<RoleAttributeEntity> roleAttributes = role.getRoleAttributes();
                if(CollectionUtils.isEmpty(roleAttributes)){
                    roleDataService.addAttribute(buildRoleAttribute(role, request));
                } else {
                    boolean isFound = false;
                    for(RoleAttributeEntity attr: roleAttributes){
                        if(request.getMetadataElementId().equals(attr.getMetadataElementId())){
                            isFound=true;
                            if(StringUtils.isBlank(attr.getValue())
                                    && CollectionUtils.isEmpty(attr.getValues())){
                                attr.setValue(request.getDefaultValue());
                                attr.setRole(role);
                                attr.setMetadataElementId(request.getMetadataElementId());
                                roleDataService.updateAttribute(attr);
                            }
                        }
                    }
                    if(!isFound){
                        roleDataService.addAttribute(buildRoleAttribute(role, request));
                    }
                }
            }
        }
    }

    public RoleAttributeEntity buildRoleAttribute(RoleEntity role, UpdateAttributeByMetadataRequest request){
        RoleAttributeEntity attribute = new RoleAttributeEntity();
        attribute.setRole(role);
        if(request!=null){
            attribute.setMetadataElementId(request.getMetadataElementId());
            attribute.setName(request.getName());
            attribute.setValue(StringUtils.isNotBlank(request.getDefaultValue()) ? request.getDefaultValue():null);
        }
        return attribute;
    }
}
