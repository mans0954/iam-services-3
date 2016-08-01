package org.openiam.idm.srvc.grp.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.request.UpdateAttributeByMetadataRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.mq.dispatcher.UpdateAttributeByMetadataDispatcher;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by alexander on 29/07/16.
 */
@Component
public class UpdateGroupAttributeByMetadataDispatcher extends UpdateAttributeByMetadataDispatcher {
    @Autowired
    private GroupDataService groupManager;
    @Override
    protected void process(UpdateAttributeByMetadataRequest request) {
        GroupSearchBean searchBean = new GroupSearchBean();
        searchBean.setType(request.getMetadataTypeId());

        List<GroupEntity> groupList = groupManager.findBeans(searchBean,null,-1,-1);
        if(CollectionUtils.isNotEmpty(groupList)){
            for(GroupEntity group: groupList){
                Set<GroupAttributeEntity> groupAttributes = group.getAttributes();
                if(CollectionUtils.isEmpty(groupAttributes)){
                    groupManager.saveAttribute(buildGroupAttribute(group, request));
                } else {
                    boolean isFound = false;
                    for(GroupAttributeEntity attr: groupAttributes){
                        if(request.getMetadataElementId().equals(attr.getMetadataElementId())){
                            isFound=true;
                            if(StringUtils.isBlank(attr.getValue())
                                    && CollectionUtils.isEmpty(attr.getValues())){
                                attr.setValue(request.getDefaultValue());
                                attr.setMetadataElementId(request.getMetadataElementId());
                                groupManager.saveAttribute(attr);
                            }
                        }
                    }
                    if(!isFound){
                        groupManager.saveAttribute(buildGroupAttribute(group, request));
                    }
                }
            }
        }
    }

    public GroupAttributeEntity buildGroupAttribute(GroupEntity group, UpdateAttributeByMetadataRequest request){
        GroupAttributeEntity attribute = new GroupAttributeEntity();
        attribute.setGroup(group);
        if(request!=null){
            attribute.setMetadataElementId(request.getMetadataElementId());
            attribute.setName(request.getName());
            attribute.setValue(StringUtils.isNotBlank(request.getDefaultValue()) ? request.getDefaultValue():null);
        }
        return attribute;
    }
}
