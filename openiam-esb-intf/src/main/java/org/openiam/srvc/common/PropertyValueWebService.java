package org.openiam.srvc.common;


import java.util.List;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.dto.PageTempate;
import org.openiam.idm.srvc.meta.dto.TemplateRequest;
import org.openiam.property.dto.PropertyValue;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/property/service", name = "PropertyValueWebService")
public interface PropertyValueWebService {
    
    @WebMethod
    Response save(final @WebParam(name = "entityList", targetNamespace = "") List<PropertyValue> entityList,
                  final @WebParam(name = "requestorId", targetNamespace = "") String requestorId);
    
    @WebMethod
    List<PropertyValue> getAll();
    
    @WebMethod
    String getCachedValue(final String key, final Language language);
    
}
