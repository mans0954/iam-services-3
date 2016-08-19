package org.openiam.provision.service;

import org.openiam.base.ws.Response;
import org.openiam.provision.dto.ProvisionActionEvent;
import org.openiam.constants.ProvisionActionTypeEnum;

public interface ProvisionServiceEventProcessor {

    String CONTINUE = "CONTINUE";
    String BREAK = "BREAK";

    Response process(ProvisionActionEvent event, ProvisionActionTypeEnum type);

}
