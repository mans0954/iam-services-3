/**
 * @author zaporozhec
 */
package org.openiam.srvc.common;

import java.io.File;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.base.request.FileRequest;
import org.openiam.base.response.data.FileResponse;
import org.openiam.base.response.data.StringResponse;
import org.openiam.mq.constants.api.common.FileAPI;
import org.openiam.mq.constants.queue.common.FileQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@WebService(endpointInterface = "org.openiam.srvc.common.FileWebService", targetNamespace = "urn:idm.openiam.org/srvc/file/ws", portName = "FileWebServicePort", serviceName = "FileWebService")
@Service("fileWebService")
public class FileWebServiceImpl extends AbstractApiService implements FileWebService {
    @Autowired
    public FileWebServiceImpl(FileQueue queue) {
        super(queue);
    }

    @Override
    public String getFile(@WebParam(name = "fileName", targetNamespace = "") String fName) {
        FileRequest request = new FileRequest();
        request.setFileName(fName);
        return  getValue(FileAPI.GetFile, request, StringResponse.class);
    }

    @Override
    public File saveFile(@WebParam(name = "fileName", targetNamespace = "") String fName,
                         @WebParam(name = "fileContent", targetNamespace = "") String value) {
        FileRequest request = new FileRequest();
        request.setFileName(fName);
        request.setFileContent(value);
        FileResponse response = this.manageApiRequest(FileAPI.SaveFile, request, FileResponse.class);
        if(response.isFailure()){
            return null;
        }
        return response.getValue();
    }



}
