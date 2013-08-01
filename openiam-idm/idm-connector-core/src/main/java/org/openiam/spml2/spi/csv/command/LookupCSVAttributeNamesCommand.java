package org.openiam.spml2.spi.csv.command;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.type.ErrorCode;
import org.openiam.connector.type.LookupAttributeResponse;
import org.openiam.connector.type.LookupRequest;
import org.openiam.connector.type.StatusCodeType;
import org.openiam.idm.srvc.file.ws.FileWebService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.spml2.spi.common.LookupAttributeNamesCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("lookupCSVAttributeNamesCommand")
public class LookupCSVAttributeNamesCommand extends AbstractCSVCommand
        implements LookupAttributeNamesCommand {
    @Autowired
    private FileWebService fileWebService;

    @Override
    public LookupAttributeResponse lookupAttributeNames(
            LookupRequest reqType) {
        LookupAttributeResponse respType = new LookupAttributeResponse();
        try {
            String file = fileWebService.getFile(reqType.getRequestID()
                    + ".csv");
            if (!StringUtils.isEmpty(file)) {
                respType.setStatus(StatusCodeType.SUCCESS);
                List<ExtensibleAttribute> extensibleAttribute = new LinkedList<ExtensibleAttribute>();
                for(String s : file.split("\n")[0].split(",")) {
                    extensibleAttribute.add(new ExtensibleAttribute(s,""));
                }
                respType.setAttributes(extensibleAttribute);
            } else {
                fileWebService.saveFile(reqType.getRequestID(), "");
            }
        } catch (Exception e) {
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION);
            respType.getErrorMessage().add(e.getMessage());
            throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, e.getMessage());
        }
        respType.setStatus(StatusCodeType.SUCCESS);
        return respType;
    }
}
