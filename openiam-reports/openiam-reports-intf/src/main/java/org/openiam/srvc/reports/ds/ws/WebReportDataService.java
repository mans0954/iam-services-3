package org.openiam.srvc.reports.ds.ws;

import java.util.HashMap;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.openiam.base.ws.PropertyMapAdapter;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/reports/service", name = "ReportService")
public interface WebReportDataService {

    @WebMethod
    GetReportByNameResponse executeQuery(@WebParam(name = "reportName", targetNamespace = "") String reportName, @WebParam(name = "queryParams", targetNamespace = "") @XmlJavaTypeAdapter(PropertyMapAdapter.class) HashMap<String, String> queryParams);

}
