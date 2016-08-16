package org.openiam.connector.csv.command;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.csv.command.base.AbstractCSVCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.response.LookupAttributeResponse;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.idm.srvc.file.FileService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("lookupCSVAttributeNamesCommand")
public class LookupCSVAttributeNamesCommand extends AbstractCSVCommand<LookupRequest,LookupAttributeResponse>{
    @Autowired
    private FileService fileService;

    @Override
    public LookupAttributeResponse execute(LookupRequest reqType) throws ConnectorDataException{
        LookupAttributeResponse respType = new LookupAttributeResponse();
        try {
            String file = fileService.getFile(reqType.getRequestID() + ".csv");
            if (!StringUtils.isEmpty(file)) {
                respType.setStatus(StatusCodeType.SUCCESS);
                List<ExtensibleAttribute> extensibleAttribute = new LinkedList<ExtensibleAttribute>();
                for(String s : file.split("\n")[0].split(",")) {
                    extensibleAttribute.add(new ExtensibleAttribute(s,""));
                }
                respType.setAttributes(extensibleAttribute);
            } else {
                fileService.saveFile(reqType.getRequestID(), "");
            }
            respType.setStatus(StatusCodeType.SUCCESS);
            return respType;
        } catch (Exception e) {
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION);
            respType.getErrorMessage().add(e.getMessage());
            throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, e.getMessage());
        }
    }
}
