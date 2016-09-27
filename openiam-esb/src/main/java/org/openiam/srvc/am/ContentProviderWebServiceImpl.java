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
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.mq.constants.ContentProviderAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("contentProviderWS")
@WebService(endpointInterface = "org.openiam.srvc.am.ContentProviderWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "ContentProviderWebServicePort",
            serviceName = "ContentProviderWebService")
public class ContentProviderWebServiceImpl extends AbstractApiService implements ContentProviderWebService{

    public ContentProviderWebServiceImpl() {
        super(OpenIAMQueue.ContentProviderQueue);
    }


    @Override
	public AuthLevelAttribute getAuthLevelAttribute(String id) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(id);
        return getValue(ContentProviderAPI.GetAuthLevelAttribute, request, AuthLevelAttributeResponse.class);
	}

	@Override
	public Response saveAuthLevelAttribute(AuthLevelAttribute attribute) {
        return manageGrudApiRequest(ContentProviderAPI.SaveAuthLevelAttribute, attribute);
	}

	@Override
	public Response deleteAuthLevelAttribute(String id) {
        return this.manageGrudApiRequest(ContentProviderAPI.DeleteAuthLevelAttribute, id);
	}
    
    @Override
    public Response saveAuthLevelGrouping(final AuthLevelGrouping grouping) {
        return manageGrudApiRequest(ContentProviderAPI.SaveAuthLevelGrouping, grouping);
    }

	@Override
	public Response deleteAuthLevelGrouping(String id) {
        return this.manageGrudApiRequest(ContentProviderAPI.DeleteAuthLevelGrouping, id);
	}

	@Override
	public AuthLevelGrouping getAuthLevelGrouping(String id) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(id);
        return getValue(ContentProviderAPI.GetAuthLevelGrouping, request, AuthLevelGroupingResponse.class);
	}

	@Override
    public List<AuthLevel> getAuthLevelList() {
        return getValueList(ContentProviderAPI.GetAuthLevelList, new BaseServiceRequest(), AuthLevelListResponse.class);
	}
	
    @Override
    public List<AuthLevelGrouping> getAuthLevelGroupingList() {
        return getValueList(ContentProviderAPI.GetAuthLevelGroupingList, new BaseServiceRequest(), AuthLevelGroupingListResponse.class);
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
        return manageGrudApiRequest(ContentProviderAPI.SetupApplication, provider);
	}

    @Override
    public Response saveContentProvider(ContentProvider provider) {
        return manageGrudApiRequest(ContentProviderAPI.SaveContentProvider, provider);
    }

    @Override
    public Response deleteContentProvider(String providerId){
        return manageGrudApiRequest(ContentProviderAPI.DeleteContentProvider, providerId);
    }

    @Override
    @Deprecated
    public List<URIPattern> getUriPatternsForProvider(String providerId, Integer from, Integer size) {
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
        return manageGrudApiRequest(ContentProviderAPI.SaveURIPattern, pattern);
    }

    @Override
    public Response deleteProviderPattern(@WebParam(name = "providerId", targetNamespace = "") String providerId) {
        return this.manageGrudApiRequest(ContentProviderAPI.DeleteProviderPattern, providerId);
    }

    @Override
    public List<URIPatternMetaType> getAllMetaType() {
        return getValueList(ContentProviderAPI.GetAllMetaType, new BaseServiceRequest(), URIPatternMetaTypeListResponse.class);
    }

	@Override
	public Response createDefaultURIPatterns(String providerId) {
        return this.manageGrudApiRequest(ContentProviderAPI.CreateDefaultURIPatterns, providerId);
	}
}
