package org.openiam.spml2.spi.csv;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.file.ws.FileWebService;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.csv.command.base.AbstractCSVCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("lookupCSVAttributeNamesCommand")
public class LookupCSVAttributeNamesCommand extends AbstractCSVCommand<LookupAttributeRequestType<? extends GenericProvisionObject>, LookupAttributeResponseType> {
    @Autowired
    private FileWebService fileWebService;

    @Override
    public LookupAttributeResponseType execute(LookupAttributeRequestType<? extends GenericProvisionObject> lookupAttributeRequestType) throws ConnectorDataException {
        LookupAttributeResponseType respType = new LookupAttributeResponseType();
        try {
            String file = fileWebService.getFile(lookupAttributeRequestType.getRequestID()
                    + ".csv");
            if (!StringUtils.isEmpty(file)) {
                respType.setStatus(StatusCodeType.SUCCESS);
                respType.setAttributeList(Arrays.asList(file.split("\n")[0]
                        .split(",")));
            } else {
                fileWebService.saveFile(lookupAttributeRequestType.getRequestID(), "");
            }
        } catch (Exception e) {
            throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, e.getMessage());
        }
        respType.setStatus(StatusCodeType.SUCCESS);
        return respType;
    }
}
