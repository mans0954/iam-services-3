package org.openiam.spml2.spi.csv;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.file.ws.FileWebService;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.LookupAttributeRequestType;
import org.openiam.spml2.msg.LookupAttributeResponseType;
import org.openiam.spml2.msg.StatusCodeType;
import org.openiam.spml2.spi.common.LookupAttributeNamesCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("lookupCSVAttributeNamesCommand")
public class LookupCSVAttributeNamesCommand extends AbstractCSVCommand
        implements LookupAttributeNamesCommand {
    @Autowired
    private FileWebService fileWebService;

    @Override
    public LookupAttributeResponseType lookupAttributeNames(
            LookupAttributeRequestType reqType) {
        LookupAttributeResponseType respType = new LookupAttributeResponseType();
        try {
            String file = fileWebService.getFile(reqType.getRequestID()
                    + ".csv");
            if (!StringUtils.isEmpty(file)) {
                respType.setStatus(StatusCodeType.SUCCESS);
                respType.setAttributeList(Arrays.asList(file.split("\n")[0]
                        .split(",")));
            } else {
                fileWebService.saveFile(reqType.getRequestID(), "");
            }
        } catch (Exception e) {
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION);
            respType.setErrorMessage(e.getMessage());
        }
        respType.setStatus(StatusCodeType.SUCCESS);
        return respType;
    }
}
