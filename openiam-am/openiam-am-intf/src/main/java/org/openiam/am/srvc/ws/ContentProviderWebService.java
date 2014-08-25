package org.openiam.am.srvc.ws;

import org.openiam.am.srvc.dto.*;
import org.openiam.am.srvc.searchbeans.ContentProviderSearchBean;
import org.openiam.am.srvc.searchbeans.URIPatternSearchBean;
import org.openiam.base.ws.Response;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/am/service", name = "ContentProviderWebService")
public interface ContentProviderWebService {
   
	@WebMethod
	public AuthLevelAttribute getAuthLevelAttribute(final String id);
	
	@WebMethod
	public Response saveAuthLevelAttribute(final AuthLevelAttribute attribute);
	
	@WebMethod
	public Response deleteAuthLevelAttribute(final String id);
	
	@WebMethod
	public Response saveAuthLevelGrouping(final AuthLevelGrouping grouping);
	
	@WebMethod
	public Response deleteAuthLevelGrouping(final String id);
	
	@WebMethod
	public AuthLevelGrouping getAuthLevelGrouping(final String id);
	
	@WebMethod
	public List<AuthLevel> getAuthLevelList();
	
    @WebMethod
    List<AuthLevelGrouping> getAuthLevelGroupingList();
    
    @WebMethod
    public Response createDefaultURIPatterns(@WebParam(name = "providerId", targetNamespace = "") String providerId);

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

    @WebMethod
    public Response deleteContentProvider(@WebParam(name = "providerId", targetNamespace = "") String providerId);

    /**
     * Returns the list for given provider Id
     * @param providerId
     * @param from
     * @param size
     * @return
     */
    @WebMethod
    public List<ContentProviderServer> getServersForProvider(@WebParam(name = "providerId", targetNamespace = "") String providerId,
                                                             @WebParam(name = "from", targetNamespace = "") Integer from,
                                                             @WebParam(name = "size", targetNamespace = "") Integer size);
    @WebMethod
    public Integer getNumOfServersForProvider(@WebParam(name = "providerId", targetNamespace = "") String providerId);

    @WebMethod
    public Response saveProviderServer(@WebParam(name = "contentProviderServer", targetNamespace = "") ContentProviderServer contentProviderServer);

    @WebMethod
    public Response deleteProviderServer(@WebParam(name = "contentProviderServerId", targetNamespace = "") String contentProviderServerId);


    @WebMethod
    @Deprecated
    public  List<URIPattern> getUriPatternsForProvider(@WebParam(name = "providerId", targetNamespace = "") String providerId,
                                                       @WebParam(name = "from", targetNamespace = "") Integer from,
                                                       @WebParam(name = "size", targetNamespace = "") Integer size);

    @WebMethod
    @Deprecated
    public  Integer getNumOfUriPatternsForProvider(@WebParam(name = "providerId", targetNamespace = "") String providerId);
    @WebMethod
    public List<URIPattern> findUriPatterns(@WebParam(name = "searchBean", targetNamespace = "") URIPatternSearchBean searchBean,
                                            @WebParam(name = "from", targetNamespace = "") Integer from,
                                            @WebParam(name = "size", targetNamespace = "") Integer size);
    @WebMethod
    public Integer getNumOfUriPatterns(@WebParam(name = "searchBean", targetNamespace = "") URIPatternSearchBean searchBean);

    @WebMethod
    public URIPattern getURIPattern(@WebParam(name = "patternId", targetNamespace = "") String patternId);

    @WebMethod
    public Response saveURIPattern(@WebParam(name = "pattern", targetNamespace = "") URIPattern pattern);

    @WebMethod
    public Response deleteProviderPattern(@WebParam(name = "providerId", targetNamespace = "") String providerId);


    @WebMethod
    public  List<URIPatternMeta> getMetaDataForPattern(@WebParam(name = "patternId", targetNamespace = "") String patternId,
                                                       @WebParam(name = "from", targetNamespace = "") Integer from,
                                                       @WebParam(name = "size", targetNamespace = "") Integer size);

    @WebMethod
    public  Integer getNumOfMetaDataForPattern(@WebParam(name = "patternId", targetNamespace = "") String patternId);

    @WebMethod
    public URIPatternMeta getURIPatternMeta(@WebParam(name = "metaId", targetNamespace = "") String metaId);

    @WebMethod
    public Response saveMetaDataForPattern(@WebParam(name = "uriPatternMeta", targetNamespace = "") URIPatternMeta uriPatternMeta);

    @WebMethod
    public Response deleteMetaDataForPattern(@WebParam(name = "metaId", targetNamespace = "") String metaId);

    @WebMethod
    public List<URIPatternMetaType> getAllMetaType();
}
