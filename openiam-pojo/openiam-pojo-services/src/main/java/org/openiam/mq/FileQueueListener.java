package org.openiam.mq;

import org.openiam.base.request.FileRequest;
import org.openiam.base.response.data.FileResponse;
import org.openiam.base.response.data.StringResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.file.FileService;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.common.FileAPI;
import org.openiam.mq.constants.queue.common.FileQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 01/08/16.
 */
@Component
@RabbitListener(id="fileQueueListener",
        queues = "#{FileQueue.name}",
        containerFactory = "commonRabbitListenerContainerFactory")
public class FileQueueListener extends AbstractListener<FileAPI> {
    @Autowired
    private FileService fileService;

    @Autowired
    public FileQueueListener(FileQueue queue) {
        super(queue);
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) FileAPI api, FileRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<FileAPI, FileRequest>(){
            @Override
            public Response doProcess(FileAPI api, FileRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GetFile:
                        response = new StringResponse();
                        ((StringResponse)response).setValue(fileService.getFile(request.getFileName()));
                        break;
                    case SaveFile:
                        response = new FileResponse();
                        ((FileResponse)response).setValue(fileService.saveFile(request.getFileName(), request.getFileContent()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
}
