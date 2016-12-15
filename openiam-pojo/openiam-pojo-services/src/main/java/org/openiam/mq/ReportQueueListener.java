package org.openiam.mq;

import org.openiam.base.request.*;
import org.openiam.base.response.*;
import org.openiam.base.response.data.IntResponse;
import org.openiam.base.response.data.StringResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.ReportSearchBean;
import org.openiam.idm.srvc.report.dto.ReportCriteriaParamDto;
import org.openiam.idm.srvc.report.dto.ReportInfoDto;
import org.openiam.idm.srvc.report.dto.ReportSubCriteriaParamDto;
import org.openiam.idm.srvc.report.service.ReportDataService;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.common.ReportAPI;
import org.openiam.mq.constants.queue.common.ReportQueue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.openiam.mq.listener.AbstractListener;

/**
 * Created by aduckardt on 2016-12-14.
 */
@Component
@RabbitListener(id = "ReportQueueListener",
        queues = "#{ReportQueue.name}",
        containerFactory = "commonRabbitListenerContainerFactory")
public class ReportQueueListener extends AbstractListener<ReportAPI> {
    @Autowired
    private ReportDataService reportDataService;


    @Autowired
    public ReportQueueListener(ReportQueue queue) {
        super(queue);
    }

    @Override
    protected RequestProcessor<ReportAPI, EmptyServiceRequest> getEmptyRequestProcessor() {
        return new RequestProcessor<ReportAPI, EmptyServiceRequest>() {
            @Override
            public Response doProcess(ReportAPI api, EmptyServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GetReportParameterTypes:
                        response=new GetReportParameterTypesResponse();
                        ((GetReportParameterTypesResponse)response).setTypes(reportDataService.getReportParameterTypes());
                        break;
                    case GetReportParameterMetaTypes:
                        response=new GetReportParameterMetaTypesResponse();
                        ((GetReportParameterMetaTypesResponse)response).setTypes(reportDataService.getReportParamMetaTypes());
                        break;
                    case GetAllReportCriteriaParam:
                        response=new GetReportParametersResponse();
                        ((GetReportParametersResponse)response).setParameters(reportDataService.getAllReportParameters());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    @Override
    protected RequestProcessor<ReportAPI, BaseSearchServiceRequest> getSearchRequestProcessor() {
        return new RequestProcessor<ReportAPI, BaseSearchServiceRequest>() {
            @Override
            public Response doProcess(ReportAPI api, BaseSearchServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GetReports:
                        response=new GetAllReportsResponse();
                        ((GetAllReportsResponse)response).setReports(reportDataService.getReports(request.getFrom(), request.getSize()));
                        break;
                    case GetReportsByUserId:
                        response=new GetAllReportsResponse();
                        ((GetAllReportsResponse)response).setReports(reportDataService.getAllReports(request.getRequesterId()));
                        break;
                    case GetReportCount:
                        response=new IntResponse();
                        ((IntResponse)response).setValue(reportDataService.getReportCount());
                        break;
                    case GetReportParametersByReportId:
                        response = new GetReportParametersResponse();
                        ((GetReportParametersResponse)response).setParameters(reportDataService.getReportParametersByReportId(((ReportSearchBean) request.getSearchBean()).getKey()));
                        break;
                    case GetReportParametersByReportName:
                        response = new GetReportParametersResponse();
                        ((GetReportParametersResponse)response).setParameters(reportDataService.getReportParametersByReportName(((ReportSearchBean) request.getSearchBean()).getReportName()));
                    case GetReportByName:
                        response = new GetReportInfoResponse();
                        ((GetReportInfoResponse)response).setReport(reportDataService.getReportByName(((ReportSearchBean) request.getSearchBean()).getReportName()));
                        break;
                    case GetReport:
                        response = new GetReportInfoResponse();
                        ((GetReportInfoResponse)response).setReport(reportDataService.getReport(((ReportSearchBean) request.getSearchBean()).getKey()));
                        break;
                    case GetSubCriteriaParam:
                        response = new GetAllSubCriteriaParamReportsResponse();
                        ((GetAllSubCriteriaParamReportsResponse)response).setReports(reportDataService.getAllSubCriteriaParamReports(((ReportSearchBean) request.getSearchBean())));
                        break;
                    case CountSubCriteriaParam:
                        response=new IntResponse();
                        ((IntResponse)response).setValue(reportDataService.getSubCriteriaParamReportCount());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    @Override
    protected RequestProcessor<ReportAPI, IdServiceRequest> getGetRequestProcessor() {
        return new RequestProcessor<ReportAPI, IdServiceRequest>() {
            @Override
            public Response doProcess(ReportAPI api, IdServiceRequest request) throws BasicDataServiceException {
                return new Response();
            }
        };
    }

    @Override
    protected RequestProcessor<ReportAPI, BaseCrudServiceRequest> getCrudRequestProcessor() {
        return new RequestProcessor<ReportAPI, BaseCrudServiceRequest>() {
            @Override
            public Response doProcess(ReportAPI api, BaseCrudServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case Save:
                        response=new StringResponse();
                        ((StringResponse)response).setValue(reportDataService.createOrUpdateReportInfo((ReportInfoDto)request.getObject()));
                        break;
                    case Validate:
                        response=new Response();
                        reportDataService.validate((ReportInfoDto)request.getObject());
                        break;
                    case SaveReportParam:
                        response=new StringResponse();
                        ((StringResponse)response).setValue(reportDataService.createOrUpdateReportParamInfo((ReportCriteriaParamDto)request.getObject()));
                        break;
                    case DeleteReportParam:
                        response=new Response();
                        reportDataService.deleteReportParam(request.getObject().getId());
                        break;
                    case DeleteReport:
                        response=new Response();
                        reportDataService.deleteReport(request.getObject().getId());
                        break;
                    case DeleteSubCriteriaParam:
                        response=new Response();
                        reportDataService.deleteSubCriteriaParamReport(((ReportSubCriteriaParamDto)request.getObject()).getReportId());
                        break;
                    case SaveSubCriteriaParam:
                        response=new StringResponse();
                        ((StringResponse)response).setValue(reportDataService.createOrUpdateSubCriteriaParamReport((ReportSubCriteriaParamDto)request.getObject()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) ReportAPI api, ReportRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<ReportAPI, ReportRequest>(){
            @Override
            public Response doProcess(ReportAPI api, ReportRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case ExecuteQueue:
                        response=new GetReportDataResponse();
                        ((GetReportDataResponse)response).setReportDataDto(reportDataService.getReportData(request.getReportQuery()));
                        break;
                    case GetReportUrl:
                        response=new StringResponse();
                        ((StringResponse)response).setValue(reportDataService.getReportUrl(request.getReportQuery(), request.getTaskName(), request.getReportBaseUrl(), request.getLocale()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
}
