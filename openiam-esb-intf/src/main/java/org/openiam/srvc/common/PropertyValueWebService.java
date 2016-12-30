package org.openiam.srvc.common;


import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.property.dto.PropertyValue;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/property/service", name = "PropertyValueWebService")
public interface PropertyValueWebService {
    
    @WebMethod
    Response save(final @WebParam(name = "entityList", targetNamespace = "") List<PropertyValue> entityList);
    
    @WebMethod
    List<PropertyValue> getAll();
    
    @WebMethod
    String getCachedValue(final String key);
    
}
