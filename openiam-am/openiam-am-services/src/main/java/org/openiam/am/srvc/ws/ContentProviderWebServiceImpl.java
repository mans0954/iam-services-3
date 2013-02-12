package org.openiam.am.srvc.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.dozer.converter.AuthLevelDozerConverter;
import org.openiam.am.srvc.dozer.converter.ContentProviderDozerConverter;
import org.openiam.am.srvc.dto.AuthLevel;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.searchbeans.ContentProviderSearchBean;
import org.openiam.am.srvc.searchbeans.converter.ContentProviderSearchBeanConverter;
import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.List;

@Service("contentProviderWS")
@WebService(endpointInterface = "org.openiam.am.srvc.ws.ContentProviderWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "ContentProviderWebServicePort",
            serviceName = "ContentProviderWebService")
public class ContentProviderWebServiceImpl implements ContentProviderWebService{
    private final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private ContentProviderService contentProviderService;
    @Autowired
    private ContentProviderDozerConverter contentProviderDozerConverter;
    @Autowired
    private ContentProviderSearchBeanConverter contentProviderSearchBeanConverter;
    @Autowired
    private AuthLevelDozerConverter authLevelDozerConverter;

    @Override
    public List<AuthLevel> getAuthLevelList(){
         return authLevelDozerConverter.convertToDTOList(contentProviderService.getAuthLevelList(), false);
    }

    @Override
    public ContentProvider getContentProvider(String providerId) {
        return contentProviderDozerConverter.convertToDTO(contentProviderService.getContentProvider(providerId),
                                                                 true);
    }

    @Override
    public List<ContentProvider> findBeans(ContentProviderSearchBean searchBean,Integer from, Integer size) {
        List<ContentProviderEntity> result = contentProviderService.findBeans(contentProviderSearchBeanConverter.convert(searchBean), from, size);
        return contentProviderDozerConverter.convertToDTOList(result, searchBean.isDeepCopy());
    }

    @Override
    public Integer getNumOfContentProviders(ContentProviderSearchBean searchBean) {
        return contentProviderService.getNumOfContentProviders(contentProviderSearchBeanConverter.convert(searchBean));
    }

    @Override
    public Response saveContentProvider(ContentProvider provider) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (provider == null)
                throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            if (provider.getName()==null || provider.getName().trim().isEmpty())
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_NAME_NOT_SET);
            if (provider.getDomainPattern()==null || provider.getDomainPattern().trim().isEmpty())
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_DOMAIN_PATERN_NOT_SET);

            if (provider.getAuthLevel()==null || provider.getAuthLevel().getId().trim().isEmpty())
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_AUTH_LEVEL_NOT_SET);

            ContentProviderEntity entity = contentProviderService.saveContentProvider(contentProviderDozerConverter.convertToEntity(provider,true));

            response.setResponseValue(contentProviderDozerConverter.convertToDTO(entity, true));

        } catch(BasicDataServiceException e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

}
