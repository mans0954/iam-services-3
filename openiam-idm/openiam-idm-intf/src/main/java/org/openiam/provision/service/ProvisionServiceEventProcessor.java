package org.openiam.provision.service;

import org.openiam.base.ws.Response;
import org.openiam.provision.dto.ProvisionActionEvent;

public interface ProvisionServiceEventProcessor {

    public static final String CONTINUE = "CONTINUE";
    public static final String BREAK = "BREAK";

    Response process(ProvisionActionEvent event);

}
