package org.openiam.srvc.am;

import org.openiam.am.srvc.dto.*;
import org.openiam.am.srvc.searchbean.ContentProviderSearchBean;
import org.openiam.am.srvc.searchbean.URIPatternSearchBean;
import org.openiam.base.ws.Response;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/am/service", name = "ContentProviderWebService")
public interface ContentProviderWebService {
   
	@WebMethod
    AuthLevelAttribute getAuthLevelAttribute(final String id);
	
	@WebMethod
    Response saveAuthLevelAttribute(final AuthLevelAttribute attribute);
	
	@WebMethod
    Response deleteAuthLevelAttribute(final String id);
	
	@WebMethod
    Response saveAuthLevelGrouping(final AuthLevelGrouping grouping);
	
	@WebMethod
    Response deleteAuthLevelGrouping(final String id);
	
	@WebMethod
    AuthLevelGrouping getAuthLevelGrouping(final String id);
	
	@WebMethod
    List<AuthLevel> getAuthLevelList();
	
    @WebMethod
    List<AuthLevelGrouping> getAuthLevelGroupingList();
    
    /**
     * This method is called upon initialization of a Tenant / OpenIAM instance
     * @param cp
     * @return
     */
    @WebMethod
    Response setupApplication(final ContentProvider cp);
    
    @WebMethod
    Response createDefaultURIPatterns(@WebParam(name = "providerId", targetNamespace = "") String providerId);

    /**
     * Add or update ContentProvider object.
     * @param provider
     * @return
     */
    @WebMethod
    Response saveContentProvider(@WebParam(name = "provider", targetNamespace = "") ContentProvider provider);

    /**
     * Returns ContentProvider object by its' identity
     * @param providerId
     * @return
     */
    @WebMethod
    ContentProvider getContentProvider(@WebParam(name = "providerId", targetNamespace = "") String providerId);

    /**
     * Searches and returns list of ContentProvider objects, using different search criteria
     * @param searchBean - determines search criteria
     * @param from - page index to start
     * @param size - page size
     * @return
     */
    @WebMethod
    List<ContentProvider> findBeans(@WebParam(name = "searchBean", targetNamespace = "") ContentProviderSearchBean searchBean,
                                    @WebParam(name = "from", targetNamespace = "") int from,
                                    @WebParam(name = "size", targetNamespace = "") int size);

    /**
     * Returns number of ContentProviders objects which are suitable for passed search criteria
     * @param searchBean
     * @return
     */
    @WebMethod
    int getNumOfContentProviders(@WebParam(name = "searchBean", targetNamespace = "") ContentProviderSearchBean searchBean);

    @WebMethod
    Response deleteContentProvider(@WebParam(name = "providerId", targetNamespace = "") String providerId);

    @WebMethod
    @Deprecated
    List<URIPattern> getUriPatternsForProvider(@WebParam(name = "providerId", targetNamespace = "") String providerId,
                                               @WebParam(name = "from", targetNamespace = "") int from,
                                               @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    @Deprecated
    Integer getNumOfUriPatternsForProvider(@WebParam(name = "providerId", targetNamespace = "") String providerId);
    @WebMethod
    List<URIPattern> findUriPatterns(@WebParam(name = "searchBean", targetNamespace = "") URIPatternSearchBean searchBean,
                                     @WebParam(name = "from", targetNamespace = "") int from,
                                     @WebParam(name = "size", targetNamespace = "") int size);
    @WebMethod
    int getNumOfUriPatterns(@WebParam(name = "searchBean", targetNamespace = "") URIPatternSearchBean searchBean);

    @WebMethod
    URIPattern getURIPattern(@WebParam(name = "patternId", targetNamespace = "") String patternId);

    @WebMethod
    Response saveURIPattern(@WebParam(name = "pattern", targetNamespace = "") URIPattern pattern);

    @WebMethod
    Response deleteProviderPattern(@WebParam(name = "providerId", targetNamespace = "") String providerId);

    @WebMethod
    List<URIPatternMetaType> getAllMetaType();
}
