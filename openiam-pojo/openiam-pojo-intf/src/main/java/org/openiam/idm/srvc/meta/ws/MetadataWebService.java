package org.openiam.idm.srvc.meta.ws;


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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Web service interface for Metadata. Metadata is used in OpenIAM to create
 * extend the capabilities of commonly used objects such as Users, Group,Role,
 * Organizations, and Resources.
 *
 * @author suneet
 * @version 2.1
 */
@WebService
public interface MetadataWebService {
    
    @WebMethod
    public List<MetadataElement> findElementBeans(final @WebParam(name = "searchBean", targetNamespace = "") MetadataElementSearchBean searchBean,
    									   final @WebParam(name = "from", targetNamespace = "") int from,
    									   final @WebParam(name = "size", targetNamespace = "") int size,
    									   final @WebParam(name = "language", targetNamespace = "") Language language);
    
    @WebMethod
    public List<MetadataType> findTypeBeans(final @WebParam(name = "searchBean", targetNamespace = "") MetadataTypeSearchBean searchBean,
    									    final @WebParam(name = "from", targetNamespace = "") int from,
    									    final @WebParam(name = "size", targetNamespace = "") int size,
    									    final @WebParam(name = "language", targetNamespace = "") Language language);
    
    @WebMethod
    public int countElementBeans(final @WebParam(name = "searchBean", targetNamespace = "") MetadataElementSearchBean searchBean);
    
    @WebMethod
    public int countTypeBeans(final @WebParam(name = "searchBean", targetNamespace = "") MetadataTypeSearchBean searchBean);
    
    @WebMethod
    public Response saveMetadataType(final @WebParam(name = "dto", targetNamespace = "") MetadataType dto);
    
    @WebMethod
    public Response saveMetadataEntity(final @WebParam(name = "dto", targetNamespace = "") MetadataElement dto);
    
    @WebMethod
    public Response deleteMetadataType(final @WebParam(name = "id", targetNamespace = "") String id);
    
    @WebMethod
    public Response deleteMetadataElement(final @WebParam(name = "id", targetNamespace = "") String id);
    
}
