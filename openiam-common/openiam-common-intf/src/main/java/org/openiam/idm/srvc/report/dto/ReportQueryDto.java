package org.openiam.idm.srvc.report.dto;

import org.openiam.base.ws.PropertyMapAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportQueryDto", propOrder = {
        "reportName",
        "queryParams"
})
public class ReportQueryDto {

	private String reportName;
    @XmlJavaTypeAdapter(PropertyMapAdapter.class)
    private Map<String, List<String>> queryParams;

    public void setReportName(String reportName){
        this.reportName = reportName;
    }

    public String getReportName(){
        return reportName;
    }

    public void setQueryParams(Map<String, List<String>> parameters) {
        this.queryParams = parameters;
    }

    public Map<String, List<String>> getQueryParams() {
        if (queryParams == null) {
            queryParams = new HashMap<>(1);
        }
        return queryParams;
    }

    public void addParameter(String name, List<String> values) {
        getQueryParams().put(name, values);
    }

    public void addParameterValue(String name, String value) {
        if (queryParams == null) {
            queryParams = new HashMap<>(1);
        }
        if (queryParams.containsKey(name)) {
            queryParams.get(name).add(value);
        } else {
            List<String> values = new ArrayList<>(1);
            values.add(value);
            queryParams.put(name, values);
        }
    }

    public List<String> getParameterValues(String name) {
        if (queryParams != null && queryParams.containsKey(name)) {
            return queryParams.get(name);
        }
        return null;
    }

    public String getParameterValue(String name) {
        List<String> values = getParameterValues(name);
        if (values != null && values.size() > 0) {
            return values.get(0);
        }
        return null;
    }

}

