package org.openiam.idm.srvc.meta.service;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.message.processor.AbstractAPIProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by alexander on 14/07/16.
 */
@Service
public class MetadataTypeGetProcessor extends AbstractAPIProcessor<String, Response> {
    @Autowired
    private MetadataService metadataService;

    @Override
    protected void processingApiRequest(String id, String languageId, Response response) throws BasicDataServiceException {
        if(StringUtils.isBlank(id)){
            throw new BasicDataServiceException(ResponseCode.METADATA_TYPE_ID_REQUIRED);
        }
        MetadataType type = metadataService.findById(id);
        response.setResponseValue(type);
    }

}
