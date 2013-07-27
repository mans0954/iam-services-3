package org.openiam.idm.srvc.report.ws;

import java.util.HashMap;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.openiam.base.ws.PropertyMapAdapter;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.report.dto.ReportCriteriaParamDto;
import org.openiam.idm.srvc.report.dto.ReportInfoDto;
import org.openiam.idm.srvc.report.dto.ReportSubCriteriaParamDto;
import org.openiam.idm.srvc.report.dto.ReportSubscriptionDto;

@WebService(targetNamespace = "urn:idm.openiam.org/idm/srvc/report/ws/service", name = "ReportService")
public interface WebReportService {

    @WebMethod
    GetReportDataResponse executeQuery(@WebParam(name = "reportName", targetNamespace = "") String reportName, @WebParam(name = "queryParams", targetNamespace = "") @XmlJavaTypeAdapter(PropertyMapAdapter.class) HashMap<String, String> queryParams);

    @WebMethod
    GetAllReportsResponse getReports(@WebParam(name = "from", targetNamespace = "") int from, 
				@WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    Integer getReportCount();
    
    @WebMethod
    Response createOrUpdateReportInfo(@WebParam(name = "report", targetNamespace = "") final ReportInfoDto report);

    @WebMethod
    Response createOrUpdateReportInfoParam(@WebParam(name = "reportParam", targetNamespace = "") final ReportCriteriaParamDto reportParam);

    @WebMethod
    Response deleteReportParam(@WebParam(name = "reportParamId", targetNamespace = "") String reportParamId) ;

    @WebMethod
    GetReportParametersResponse getReportParametersByReportId(@WebParam(name = "reportId", targetNamespace = "") String reportId);
    
    @WebMethod
    GetReportParametersResponse getReportParametersByReportName(@WebParam(name = "reportName", targetNamespace = "") String reportName);

    @WebMethod
    GetReportParameterTypesResponse getReportParameterTypes();

    @WebMethod
    GetAllSubscribedReportsResponse getSubscribedReports();
    
    @WebMethod
    GetAllSubCriteriaParamReportsResponse getSubCriteriaParamReports();

    @WebMethod
    Response createOrUpdateSubscribedReportInfo(@WebParam(name = "reportSubscriptionDto", targetNamespace = "") ReportSubscriptionDto reportSubscriptionDto, @WebParam(name = "parameters", targetNamespace = "") List<ReportSubCriteriaParamDto> parameters);
    
    @WebMethod
    GetReportInfoResponse getReportByName(@WebParam(name = "reportName", targetNamespace = "") String reportName) ;

    @WebMethod
    GetReportInfoResponse getReport(@WebParam(name = "reportId", targetNamespace = "") String reportId) ;

    @WebMethod
    Response deleteReport(@WebParam(name = "reportId", targetNamespace = "") String reportId) ;
    
    @WebMethod
    Response deleteSubscribedReport(@WebParam(name = "reportId", targetNamespace = "") String reportId) ;
    
    @WebMethod
    Integer getSubscribedReportCount();
    
    
    @WebMethod
    Integer getSubCriteriaParamReportCount();
    
    @WebMethod
    GetSubscribedReportResponse getSubscribedReportById(@WebParam(name = "reportId", targetNamespace = "") String reportId) ;
    
    @WebMethod
    GetSubCriteriaParamReportResponse getSubCriteriaParamReportById(@WebParam(name = "Id", targetNamespace = "") String reportId) ;
    
    @WebMethod
    Response deleteSubCriteriaParamReport(@WebParam(name = "Id", targetNamespace = "") String reportId) ;
    
    @WebMethod
   GetAllSubCriteriaParamReportsResponse getAllSubCriteriaParamReport(@WebParam(name = "reportId", targetNamespace = "") String reportId);
    
    @WebMethod
    Response createOrUpdateSubCriteriaParam(
			@WebParam(name = "subCriteriaParamReport", targetNamespace = "") final ReportSubCriteriaParamDto subCriteriaParamReport);
    

}
