package org.openiam.provision.service;

import org.openiam.provision.dto.srcadapter.SourceAdapterInfoResponse;
import org.openiam.provision.dto.srcadapter.SourceAdapterRequest;
import org.openiam.provision.dto.srcadapter.SourceAdapterResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Created by zaporozhec on 10/29/15.
 */
@WebService(targetNamespace = "http://www.openiam.org/service/provision", name = "SourceAdapterService")
public interface SourceAdapter {
    @WebMethod
    public SourceAdapterResponse perform(@WebParam(name = "user", targetNamespace = "") SourceAdapterRequest request);

    @WebMethod
    public SourceAdapterInfoResponse info();
}
