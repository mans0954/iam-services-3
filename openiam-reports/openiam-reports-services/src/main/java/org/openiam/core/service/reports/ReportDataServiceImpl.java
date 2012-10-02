package org.openiam.core.service.reports;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.openiam.core.dao.reports.ReportDataDao;
import org.openiam.core.domain.reports.ReportQuery;
import org.openiam.core.dto.reports.ReportDataDto;
import org.openiam.exception.ScriptEngineException;
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
    public ReportDataDto getReportData(final String reportName, final Map<String, String> reportParams) throws ClassNotFoundException, ScriptEngineException {
        ReportQuery reportQuery = reportDao.findByName(reportName);
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
        ReportDataDto reportData = (ReportDataDto) se.execute(objectMap, reportQuery.getQueryScriptPath());

        return reportData;
    }

    private static boolean validateParams(ReportQuery reportQuery, Map<String, String> queryParams) {
        for(String requiredParam : reportQuery.getRequiredParamsList()) {
            if(!queryParams.containsKey(requiredParam)) {
               return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportQuery> getAllReports() {
        return reportDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ReportQuery getReportByName(String name) {
        return reportDao.findByName(name);
    }
}
