package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractResourceOrderProcessor implements ProvisionServiceResourceOrderProcessor {

    protected ApplicationContext context;

    @Override
    public List<Resource> orderDeprovisionResources(ProvisionUser pUser, Set<Resource> resources, Map<String, Object> bindingMap) {
        if (CollectionUtils.isNotEmpty(resources)) {
            return new ArrayList<Resource>(resources);
        }
        return null;
    }

    @Override
    public List<Resource> orderProvisionResources(ProvisionUser pUser, Set<Resource> resources, Map<String, Object> bindingMap) {
        if (CollectionUtils.isNotEmpty(resources)) {
            return new ArrayList<Resource>(resources);
        }
        return null;
    }
}
