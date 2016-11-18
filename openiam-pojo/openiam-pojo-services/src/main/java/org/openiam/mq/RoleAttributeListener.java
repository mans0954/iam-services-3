package org.openiam.mq;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.request.UpdateAttributeByMetadataRequest;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.mq.constants.queue.am.RoleAttributeQueue;
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
@RabbitListener(id="roleAttributeListener",
        queues = "#{RoleAttributeQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class RoleAttributeListener extends AbstractAttributeListener {
    @Autowired
    public RoleAttributeListener(RoleAttributeQueue queue) {
        super(queue);
    }

    @Autowired
    private RoleDataService roleDataService;

    @Override
    protected void process(UpdateAttributeByMetadataRequest request) {
        RoleSearchBean searchBean = new RoleSearchBean();
        searchBean.setType(request.getMetadataTypeId());
        List<RoleEntity> roleList = roleDataService.findBeans(searchBean, null, -1, -1);
        if (CollectionUtils.isNotEmpty(roleList)) {
            for (RoleEntity role : roleList) {
                Set<RoleAttributeEntity> roleAttributes = role.getRoleAttributes();
                if (CollectionUtils.isEmpty(roleAttributes)) {
                    try {
                        roleDataService.addAttribute(buildRoleAttribute(role, request));
                    } catch (BasicDataServiceException e) {
                        log.error(e.getCode().name());
                    }
                } else {
                    boolean isFound = false;
                    for (RoleAttributeEntity attr : roleAttributes) {
                        if (request.getMetadataElementId().equals(attr.getMetadataElementId())) {
                            isFound = true;
                            if (StringUtils.isBlank(attr.getValue())
                                    && CollectionUtils.isEmpty(attr.getValues())) {
                                attr.setValue(request.getDefaultValue());
                                attr.setRole(role);
                                attr.setMetadataElementId(request.getMetadataElementId());
                                try {
                                    roleDataService.updateAttribute(attr);
                                } catch (BasicDataServiceException e) {
                                    log.error(e.getCode().name());
                                }
                            }
                        }
                    }
                    if (!isFound) {
                        try {
                            roleDataService.addAttribute(buildRoleAttribute(role, request));
                        } catch (BasicDataServiceException e) {
                            log.error(e.getCode().name());
                        }
                    }
                }
            }
        }
    }

    public RoleAttributeEntity buildRoleAttribute(RoleEntity role, UpdateAttributeByMetadataRequest request) {
        RoleAttributeEntity attribute = new RoleAttributeEntity();
        attribute.setRole(role);
        if (request != null) {
            attribute.setMetadataElementId(request.getMetadataElementId());
            attribute.setName(request.getName());
            attribute.setValue(StringUtils.isNotBlank(request.getDefaultValue()) ? request.getDefaultValue() : null);
        }
        return attribute;
    }
}
