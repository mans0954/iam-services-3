package org.openiam.idm.srvc.res.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.request.UpdateAttributeByMetadataRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
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
public class UpdateResourceAttributeByMetadataDispatcher extends UpdateAttributeByMetadataDispatcher {
    @Autowired
    private ResourceService resourceService;

    @Override
    protected void process(UpdateAttributeByMetadataRequest request) {
        ResourceSearchBean searchBean = new ResourceSearchBean();
        searchBean.setMetadataType(request.getMetadataTypeId());

        List<ResourceEntity> resList = resourceService.findBeansLocalized(searchBean,-1,-1, null);
        if(CollectionUtils.isNotEmpty(resList)){
            for(ResourceEntity res: resList){
                Set<ResourcePropEntity> resAttributes = res.getResourceProps();
                if(CollectionUtils.isEmpty(resAttributes)){
                    resourceService.saveAttribute(buildResAttribute(res, request));
                } else {
                    boolean isFound = false;
                    for(ResourcePropEntity attr: resAttributes){
                        if(request.getMetadataElementId().equals(attr.getMetadataElementId())){
                            isFound=true;
                            if(StringUtils.isBlank(attr.getValue())){
                                attr.setValue(request.getDefaultValue());
                                attr.setMetadataElementId(request.getMetadataElementId());
                                resourceService.saveAttribute(attr);
                            }
                        }
                    }
                    if(!isFound){
                        resourceService.saveAttribute(buildResAttribute(res, request));
                    }
                }
            }
        }
    }

    public ResourcePropEntity buildResAttribute(ResourceEntity resource, UpdateAttributeByMetadataRequest request){
        ResourcePropEntity attribute = new ResourcePropEntity();
        attribute.setResource(resource);
        if(request!=null){
            attribute.setMetadataElementId(request.getMetadataElementId());
            attribute.setName(request.getName());
            attribute.setValue(StringUtils.isNotBlank(request.getDefaultValue()) ? request.getDefaultValue():null);
        }
        return attribute;
    }
}
