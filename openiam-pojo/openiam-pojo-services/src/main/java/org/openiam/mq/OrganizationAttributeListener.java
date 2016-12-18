package org.openiam.mq;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.request.UpdateAttributeByMetadataRequest;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.openiam.mq.constants.queue.am.OrganizationAttributeQueue;
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
@RabbitListener(id="organizationAttributeListener",
        queues = "#{OrganizationAttributeQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class OrganizationAttributeListener extends AbstractAttributeListener {

    @Autowired
    public OrganizationAttributeListener(OrganizationAttributeQueue queue) {
        super(queue);
    }

    @Autowired
    private OrganizationService organizationService;
    @Override
    protected void process(UpdateAttributeByMetadataRequest request) {
        OrganizationSearchBean searchBean = new OrganizationSearchBean();
        searchBean.setMetadataType(request.getMetadataTypeId());

        List<OrganizationEntity> orgList = organizationService.findBeans(searchBean,-1,-1);
        if(CollectionUtils.isNotEmpty(orgList)){
            for(OrganizationEntity org: orgList){
                Set<OrganizationAttributeEntity> orgAttributes = org.getAttributes();
                if(CollectionUtils.isEmpty(orgAttributes)){
                    organizationService.saveAttribute(buildOrgAttribute(org, request));
                } else {
                    boolean isFound = false;
                    for(OrganizationAttributeEntity attr: orgAttributes){
                        if(request.getMetadataElementId().equals(attr.getMetadataElementId())){
                            isFound=true;
                            if(StringUtils.isBlank(attr.getValue())
                                    && CollectionUtils.isEmpty(attr.getValues())){
                                attr.setValue(request.getDefaultValue());
                                attr.setMetadataElementId(request.getMetadataElementId());
                                organizationService.saveAttribute(attr);
                            }
                        }
                    }
                    if(!isFound){
                        organizationService.saveAttribute(buildOrgAttribute(org, request));
                    }
                }
            }
        }
    }

    public OrganizationAttributeEntity buildOrgAttribute(OrganizationEntity organization, UpdateAttributeByMetadataRequest request){
        OrganizationAttributeEntity attribute = new OrganizationAttributeEntity();
        attribute.setOrganization(organization);
        if(request!=null){
            attribute.setMetadataElementId(request.getMetadataElementId());
            attribute.setName(request.getName());
            attribute.setValue(StringUtils.isNotBlank(request.getDefaultValue()) ? request.getDefaultValue():null);
        }
        return attribute;
    }
}
