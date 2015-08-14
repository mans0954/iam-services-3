package org.openiam.provision.service;

import org.openiam.base.ws.Response;
import org.openiam.provision.dto.ProvisionActionEvent;
import org.openiam.provision.dto.ProvisionActionTypeEnum;

public interface ProvisionServiceEventProcessor {

    String CONTINUE = "CONTINUE";
    String BREAK = "BREAK";

    Response process(ProvisionActionEvent event, ProvisionActionTypeEnum type);

}
