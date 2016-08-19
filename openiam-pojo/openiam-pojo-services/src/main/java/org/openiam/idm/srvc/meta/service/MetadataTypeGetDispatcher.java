package org.openiam.idm.srvc.meta.service;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 14/07/16.
 */
@Component
public class MetadataTypeGetDispatcher extends AbstractAPIDispatcher<IdServiceRequest, Response> {
    @Autowired
    private MetadataService metadataService;

    public MetadataTypeGetDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(final OpenIAMAPI openIAMAPI, final  IdServiceRequest idServiceRequest) throws BasicDataServiceException {
        Response response = new Response();
        if(StringUtils.isBlank(idServiceRequest.getId())){
            throw new BasicDataServiceException(ResponseCode.METADATA_TYPE_ID_REQUIRED);
        }
        MetadataType type = metadataService.findById(idServiceRequest.getId());
        response.setResponseValue(type);
        return response;
    }

}
