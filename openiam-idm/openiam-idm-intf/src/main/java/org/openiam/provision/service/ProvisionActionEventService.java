package org.openiam.provision.service;


import org.openiam.provision.dto.ProvisionActionEvent;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(targetNamespace = "http://www.openiam.org/service/provision", name = "ProvisionActionEventService")
public interface ProvisionActionEventService {

    @WebMethod
    void add(@WebParam(name = "event", targetNamespace = "") ProvisionActionEvent event);

}
