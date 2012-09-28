package org.openiam.srvc.reports.ds.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.openiam.core.domain.reports.ReportQuery;
import org.openiam.exception.ScriptEngineException;
import org.openiam.srvc.reports.ds.dao.ReportDataDao;
import org.openiam.script.ScriptFactory;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportDataServiceImpl implements ReportDataService {
    @Autowired
    private String scriptEngine;

    @Autowired
    private ReportDataDao reportDao;

    @Override
    @Transactional(readOnly = true)
    public List<Object> getReportData(final String reportName, final Map<String, String> reportParams) throws ClassNotFoundException, ScriptEngineException {
        List<Object> resultData = new LinkedList<Object>();

        ReportQuery reportQuery = reportDao.getQueryScriptPath(reportName);
        if (reportQuery == null) {
            throw new IllegalArgumentException("Invalid parameter list: report with name="+reportName + " was not found in DataBase");
        }
        if(!validateParams(reportQuery, reportParams)) {
           throw new IllegalArgumentException("Invalid parameter list: required="+reportQuery.getRequiredParams());
        }
        Map<String, Object> objectMap = new HashMap<String, Object>();
        if (reportParams != null) {
            objectMap.putAll(reportParams);
        }
        ScriptIntegration se = ScriptFactory.createModule(this.scriptEngine);
        String output = (String) se.execute(objectMap, reportQuery.getQueryScriptPath());
        if (!StringUtils.isEmpty(output)) {
            Class clazz = Class.forName(reportQuery.getDtoClass());
            resultData = reportDao.getReportData(output, clazz);
        }
        return resultData;
    }

    private static boolean validateParams(ReportQuery reportQuery, Map<String, String> queryParams) {
        for(String requiredParam : reportQuery.getRequiredParamsList()) {
            if(!queryParams.containsKey(requiredParam)) {
               return false;
            }
        }
        return true;
    }
}
