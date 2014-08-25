package org.openiam.provision.service;

import org.openiam.provision.dto.ProvisionActionEvent;

public interface ProvisionServiceEventProcessor {

    void process(ProvisionActionEvent event);

}
