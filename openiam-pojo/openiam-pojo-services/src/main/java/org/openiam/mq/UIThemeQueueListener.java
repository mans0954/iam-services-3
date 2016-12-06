package org.openiam.mq;

import org.openiam.base.request.*;
import org.openiam.base.response.data.StringResponse;
import org.openiam.base.response.data.UIThemeResponse;
import org.openiam.base.response.list.PropertyValueListResponse;
import org.openiam.base.response.list.UIThemeListResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.concurrent.AuditLogHolder;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.UIThemeSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.property.service.PropertyValueService;
import org.openiam.idm.srvc.property.service.PropertyValueSweeper;
import org.openiam.idm.srvc.ui.theme.UIThemeService;
import org.openiam.idm.srvc.ui.theme.dto.UITheme;
import org.openiam.mq.constants.api.common.PropertyValueAPI;
import org.openiam.mq.constants.api.common.UIThemeAPI;
import org.openiam.mq.constants.queue.common.PropertyValueQueue;
import org.openiam.mq.constants.queue.common.UIThemeQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 06/12/16.
 */
@RabbitListener(id="uiThemeQueueListener",
        queues = "#{UIThemeQueue.name}",
        containerFactory = "commonRabbitListenerContainerFactory")
@Component
public class UIThemeQueueListener extends AbstractListener<UIThemeAPI> {

    @Autowired
    private UIThemeService uiThemeService;

    @Autowired
    public UIThemeQueueListener(UIThemeQueue queue) {
        super(queue);
    }


    protected RequestProcessor<UIThemeAPI, BaseSearchServiceRequest> getSearchRequestProcessor(){
        return new RequestProcessor<UIThemeAPI, BaseSearchServiceRequest>(){
            @Override
            public Response doProcess(UIThemeAPI api, BaseSearchServiceRequest request) throws BasicDataServiceException {
                UIThemeListResponse response = new UIThemeListResponse();
                response.setList(uiThemeService.findBeans(((UIThemeSearchBean)request.getSearchBean()), request.getFrom(), request.getSize()));
                return response;
            }
        };
    }

    protected RequestProcessor<UIThemeAPI, IdServiceRequest> getGetRequestProcessor(){
        return new RequestProcessor<UIThemeAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(UIThemeAPI api, IdServiceRequest request) throws BasicDataServiceException {
                UIThemeResponse response = new UIThemeResponse();
                response.setValue(uiThemeService.get(request.getId()));
                return response;
            }
        };

    }
    protected RequestProcessor<UIThemeAPI, BaseCrudServiceRequest> getCrudRequestProcessor(){
        return new RequestProcessor<UIThemeAPI, BaseCrudServiceRequest>(){
            @Override
            public Response doProcess(UIThemeAPI api, BaseCrudServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case Save:
                        response = new StringResponse();
                        ((StringResponse)response).setValue(uiThemeService.save((UITheme)request.getObject()));
                        break;
                    case Delete:
                        response = new Response();
                        uiThemeService.delete(request.getObject().getId());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
}
