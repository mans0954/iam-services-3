package org.openiam.mq;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.request.UpdateAttributeByMetadataRequest;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.mq.constants.queue.am.ResourceAttributeQueue;
import org.openiam.mq.listener.AbstractAttributeListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by alexander on 12/07/16.
 */
@Component
@RabbitListener(id="resourceAttributeListener",
        queues = "#{ResourceAttributeQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class ResourceAttributeListener extends AbstractAttributeListener {
    @Autowired
    public ResourceAttributeListener(ResourceAttributeQueue queue) {
        super(queue);
    }

    @Autowired
    private ResourceService resourceService;

    @Override
    protected void process(UpdateAttributeByMetadataRequest request) {
        ResourceSearchBean searchBean = new ResourceSearchBean();
        searchBean.setMetadataType(request.getMetadataTypeId());

        List<ResourceEntity> resList = resourceService.findBeans(searchBean,-1,-1);
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
