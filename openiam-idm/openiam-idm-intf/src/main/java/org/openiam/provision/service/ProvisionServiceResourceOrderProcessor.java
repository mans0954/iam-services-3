package org.openiam.provision.service;

import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.provision.dto.ProvisionUser;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for scripts that define the order for resources that need to be
 * deprovisioned/provisioned for specific User
 */
public interface ProvisionServiceResourceOrderProcessor {

    List<Resource> orderDeprovisionResources(ProvisionUser pUser, Set<Resource> resources, Map<String, Object> bindingMap);
    List<Resource> orderProvisionResources(ProvisionUser pUser, Set<Resource> resources, Map<String, Object> bindingMap);

}
