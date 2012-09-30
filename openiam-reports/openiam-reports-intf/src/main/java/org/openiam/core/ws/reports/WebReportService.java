package org.openiam.core.ws.reports;

import java.util.HashMap;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.openiam.base.ws.PropertyMapAdapter;

@WebService(targetNamespace = "urn:idm.openiam.org/core/ws/reports/service", name = "ReportService")
public interface WebReportService {

    @WebMethod
    GetReportDataResponse executeQuery(@WebParam(name = "reportName", targetNamespace = "") String reportName, @WebParam(name = "queryParams", targetNamespace = "") @XmlJavaTypeAdapter(PropertyMapAdapter.class) HashMap<String, String> queryParams);

    @WebMethod
    GetAllReportsResponse getReports();

    @WebMethod
    GetReportParametersResponse getParametersByReport(@WebParam(name="reportName", targetNamespace = "") String reportName);
}
