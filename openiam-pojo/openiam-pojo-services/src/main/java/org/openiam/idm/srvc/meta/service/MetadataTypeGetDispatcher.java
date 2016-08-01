package org.openiam.idm.srvc.meta.service;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 14/07/16.
 */
@Component
public class MetadataTypeGetDispatcher extends AbstractAPIDispatcher<String, Response> {
    @Autowired
    private MetadataService metadataService;

    public MetadataTypeGetDispatcher() {
        super(Response.class);
    }

    @Override
    protected void processingApiRequest(final  String id, String languageId, Response response) throws BasicDataServiceException {
        if(StringUtils.isBlank(id)){
            throw new BasicDataServiceException(ResponseCode.METADATA_TYPE_ID_REQUIRED);
        }
        MetadataType type = metadataService.findById(id);
        response.setResponseValue(type);
    }

}
