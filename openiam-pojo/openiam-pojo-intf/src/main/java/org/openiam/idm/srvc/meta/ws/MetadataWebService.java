package org.openiam.idm.srvc.meta.ws;


import java.util.List;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;

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

    /**
     * Gets the Metadata Element based on the Type Id.It also returns the MetadataOptions
     * with the MetadataElements.
     *
     * @param typeId the MetadataType for which the MetadataElements are required.
     * @return the Map which contains MetadataId as Key and MetadataElementValue
     *         objects as Values.
     */
    @WebMethod
    public List<MetadataElement> getMetadataElementByType(
            @WebParam(name = "typeId", targetNamespace = "")
            String typeId);

    @WebMethod
    public List<MetadataType> getAllMetadataTypes();
    
    /**
     * Returns a list of MetadataTypes that are associated with a Category
     *
     * @param categoryId
     */
    @WebMethod
    public List<MetadataType> getTypesInCategory(
            @WebParam(name = "categoryId", targetNamespace = "")
            String categoryId);

    @WebMethod
    public List<MetadataElement> getAllElementsForCategoryType(
            @WebParam(name = "categoryType", targetNamespace = "")
            String categoryType);

    @WebMethod
    public List<MetadataElement> findElementBeans(final @WebParam(name = "searchBean", targetNamespace = "") MetadataElementSearchBean searchBean,
    									   final @WebParam(name = "from", targetNamespace = "") int from,
    									   final @WebParam(name = "size", targetNamespace = "") int size);
    
    @WebMethod
    public List<MetadataType> findTypeBeans(final @WebParam(name = "searchBean", targetNamespace = "") MetadataTypeSearchBean searchBean,
    									    final @WebParam(name = "from", targetNamespace = "") int from,
    									    final @WebParam(name = "size", targetNamespace = "") int size);
    
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
    
    @WebMethod
    public MetadataElement findElementById(final @WebParam(name = "id", targetNamespace = "") String id);
    
    @WebMethod
    public MetadataType findTypeById(final @WebParam(name = "id", targetNamespace = "") String id);
}
