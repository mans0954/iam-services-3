package org.openiam.srvc.am;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.am.srvc.dto.AuthLevel;
import org.openiam.am.srvc.dto.AuthLevelAttribute;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternMetaType;
import org.openiam.am.srvc.searchbean.ContentProviderSearchBean;
import org.openiam.am.srvc.searchbean.URIPatternSearchBean;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.mq.constants.api.ContentProviderAPI;
import org.openiam.mq.constants.queue.am.ContentProviderQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("contentProviderWS")
@WebService(endpointInterface = "org.openiam.srvc.am.ContentProviderWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "ContentProviderWebServicePort",
            serviceName = "ContentProviderWebService")
public class ContentProviderWebServiceImpl extends AbstractApiService implements ContentProviderWebService{

    @Autowired
    public ContentProviderWebServiceImpl(ContentProviderQueue queue) {
        super(queue);
    }


    @Override
	public AuthLevelAttribute getAuthLevelAttribute(String id) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(id);
        return getValue(ContentProviderAPI.GetAuthLevelAttribute, request, AuthLevelAttributeResponse.class);
	}

	@Override
	public Response saveAuthLevelAttribute(AuthLevelAttribute attribute) {
        return manageCrudApiRequest(ContentProviderAPI.SaveAuthLevelAttribute, attribute);
	}

	@Override
	public Response deleteAuthLevelAttribute(String id) {
        AuthLevelAttribute obj = new AuthLevelAttribute();
        obj.setId(id);
        return this.manageCrudApiRequest(ContentProviderAPI.DeleteAuthLevelAttribute, obj);
	}
    
    @Override
    public Response saveAuthLevelGrouping(final AuthLevelGrouping grouping) {
        return manageCrudApiRequest(ContentProviderAPI.SaveAuthLevelGrouping, grouping);
    }

	@Override
	public Response deleteAuthLevelGrouping(String id) {
        AuthLevelGrouping obj = new AuthLevelGrouping();
        obj.setId(id);
        return this.manageCrudApiRequest(ContentProviderAPI.DeleteAuthLevelGrouping, obj);
	}

	@Override
	public AuthLevelGrouping getAuthLevelGrouping(String id) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(id);
        return getValue(ContentProviderAPI.GetAuthLevelGrouping, request, AuthLevelGroupingResponse.class);
	}

	@Override
    public List<AuthLevel> getAuthLevelList() {
        return getValueList(ContentProviderAPI.GetAuthLevelList, new EmptyServiceRequest(), AuthLevelListResponse.class);
	}
	
    @Override
    public List<AuthLevelGrouping> getAuthLevelGroupingList() {
        return getValueList(ContentProviderAPI.GetAuthLevelGroupingList, new EmptyServiceRequest(), AuthLevelGroupingListResponse.class);
    }

    @Override
    public ContentProvider getContentProvider(String providerId) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(providerId);
        return getValue(ContentProviderAPI.GetContentProvider, request, ContentProviderResponse.class);
    }

    @Override
    public List<ContentProvider> findBeans(ContentProviderSearchBean searchBean,int from, int size) {
        BaseSearchServiceRequest<ContentProviderSearchBean> request = new BaseSearchServiceRequest<>(searchBean, from, size);
        return getValueList(ContentProviderAPI.FindContentProviderBeans, request, ContentProviderListResponse.class);
    }

    @Override
    public int getNumOfContentProviders(ContentProviderSearchBean searchBean) {
        BaseSearchServiceRequest<ContentProviderSearchBean> request = new BaseSearchServiceRequest<>(searchBean);
        return getIntValue(ContentProviderAPI.GetNumOfContentProviders, request);
    }
    

	@Override
	public Response setupApplication(final ContentProvider provider) {
        return manageCrudApiRequest(ContentProviderAPI.SetupApplication, provider);
	}

    @Override
    public Response saveContentProvider(ContentProvider provider) {
        return manageCrudApiRequest(ContentProviderAPI.SaveContentProvider, provider);
    }

    @Override
    public Response deleteContentProvider(String providerId){
        ContentProvider obj = new ContentProvider();
        obj.setId(providerId);
        return manageCrudApiRequest(ContentProviderAPI.DeleteContentProvider, obj);
    }

    @Override
    @Deprecated
    public List<URIPattern> getUriPatternsForProvider(String providerId, int from, int size) {
        final URIPatternSearchBean sb = new URIPatternSearchBean();
        sb.setContentProviderId(providerId);
        return findUriPatterns(sb, from, size);
    }

    @Override
    @Deprecated
    public Integer getNumOfUriPatternsForProvider(String providerId) {
    	final URIPatternSearchBean sb = new URIPatternSearchBean();
    	sb.setContentProviderId(providerId);
        return getNumOfUriPatterns(sb);
    }

    @Override
    public List<URIPattern> findUriPatterns(URIPatternSearchBean searchBean, int from, int size) {
        BaseSearchServiceRequest<URIPatternSearchBean> request = new BaseSearchServiceRequest<>(searchBean, from, size);
        return this.getValueList(ContentProviderAPI.FindUriPatterns, request, URIPatternListResponse.class);
    }

    @Override
    public int getNumOfUriPatterns(URIPatternSearchBean searchBean) {
        BaseSearchServiceRequest<URIPatternSearchBean> request = new BaseSearchServiceRequest<>(searchBean);
        return getIntValue(ContentProviderAPI.GetNumOfUriPatterns, request);
    }

    @Override
    public URIPattern getURIPattern(String patternId) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(patternId);
        return getValue(ContentProviderAPI.GetURIPattern, request, URIPatternResponse.class);
    }

    @Override
    public Response saveURIPattern(final @WebParam(name = "pattern", targetNamespace = "") URIPattern pattern) {
        return manageCrudApiRequest(ContentProviderAPI.SaveURIPattern, pattern);
    }

    @Override
    public Response deleteProviderPattern(@WebParam(name = "providerId", targetNamespace = "") String providerId) {
        ContentProvider obj = new ContentProvider();
        obj.setId(providerId);
        return this.manageCrudApiRequest(ContentProviderAPI.DeleteProviderPattern, obj);
    }

    @Override
    public List<URIPatternMetaType> getAllMetaType() {
        return getValueList(ContentProviderAPI.GetAllMetaType, new EmptyServiceRequest(), URIPatternMetaTypeListResponse.class);
    }

	@Override
	public Response createDefaultURIPatterns(String providerId) {
        ContentProvider obj = new ContentProvider();
        obj.setId(providerId);
        return this.manageCrudApiRequest(ContentProviderAPI.CreateDefaultURIPatterns, obj);
	}
}
