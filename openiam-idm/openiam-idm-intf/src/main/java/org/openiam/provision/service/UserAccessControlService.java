package org.openiam.provision.service;

import org.openiam.authmanager.model.UserEntitlementsMatrix;
import org.openiam.provision.dto.accessmodel.UserAccessControlRequest;
import org.openiam.provision.dto.accessmodel.UserAccessControlResponse;
import org.openiam.provision.dto.srcadapter.SourceAdapterInfoResponse;
import org.openiam.provision.dto.srcadapter.SourceAdapterRequest;
import org.openiam.provision.dto.srcadapter.SourceAdapterResponse;

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
