package org.openiam.provision.service;

import org.openiam.provision.dto.accessmodel.UserAccessControlRequest;
import org.openiam.provision.dto.accessmodel.UserAccessControlResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Created by zaporozhec on 10/29/15.
 */
@WebService(targetNamespace = "http://www.openiam.org/service/provision", name = "UserAccessControlService")
public interface UserAccessControlService {
    @WebMethod
    public UserAccessControlResponse getAccessControl(@WebParam(name = "userAccessControlRequest") UserAccessControlRequest request);

}
