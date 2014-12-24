package org.openiam.idm.srvc.property.ws;


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
    public Response save(final @WebParam(name = "entityList", targetNamespace = "") List<PropertyValue> entityList, 
    					 final @WebParam(name = "requestorId", targetNamespace = "") String requestorId);
    
    @WebMethod
    public List<PropertyValue> getAll();
    
    @WebMethod
    public String getCachedValue(final String key, final Language language);
    
}
