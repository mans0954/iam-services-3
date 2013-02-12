package org.openiam.am.srvc.ws;

import org.openiam.am.srvc.dto.AuthLevel;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.searchbeans.ContentProviderSearchBean;
import org.openiam.base.ws.Response;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/am/service", name = "ContentProviderWebService")
public interface ContentProviderWebService {
    @WebMethod
    List<AuthLevel> getAuthLevelList();

    /**
     * Add or update ContentProvider object.
     * @param provider
     * @return
     */
    @WebMethod
    public Response saveContentProvider(@WebParam(name = "provider", targetNamespace = "") ContentProvider provider);

    /**
     * Returns ContentProvider object by its' identity
     * @param providerId
     * @return
     */
    @WebMethod
    public ContentProvider getContentProvider(@WebParam(name = "providerId", targetNamespace = "") String providerId);

    /**
     * Searches and returns list of ContentProvider objects, using different search criteria
     * @param searchBean - determines search criteria
     * @param from - page index to start
     * @param size - page size
     * @return
     */
    @WebMethod
    public List<ContentProvider> findBeans(@WebParam(name = "searchBean", targetNamespace = "") ContentProviderSearchBean searchBean,
                                           @WebParam(name = "from", targetNamespace = "") Integer from,
                                           @WebParam(name = "size", targetNamespace = "") Integer size);

    /**
     * Returns number of ContentProviders objects which are suitable for passed search criteria
     * @param searchBean
     * @return
     */
    @WebMethod
    public Integer getNumOfContentProviders(@WebParam(name = "searchBean", targetNamespace = "") ContentProviderSearchBean searchBean);
}
