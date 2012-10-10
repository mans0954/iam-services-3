package org.openiam.idm.srvc.report.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.openiam.core.dao.ReportDataDao;
import org.openiam.core.domain.ReportInfo;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.report.dto.ReportDataDto;
import org.openiam.script.ScriptFactory;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportDataServiceImpl implements ReportDataService {

    private static final String scriptEngine = "org.openiam.script.GroovyScriptEngineIntegration";

    @Autowired
    private ReportDataDao reportDao;

    @Override
    @Transactional(readOnly = true)
    public ReportDataDto getReportData(final String reportName, final Map<String, String> reportParams) throws ClassNotFoundException, ScriptEngineException, IOException {
        ReportInfo reportInfo = reportDao.findByName(reportName);
        if (reportInfo == null) {
            throw new IllegalArgumentException("Invalid parameter list: report with name="+reportName + " was not found in Database");
        }
        if(!validateParams(reportInfo, reportParams)) {
           throw new IllegalArgumentException("Invalid parameter list: required="+reportInfo.getRequiredParams());
        }

        ScriptIntegration se = ScriptFactory.createModule(scriptEngine);
        ReportDataSetBuilder dataSourceBuilder = (ReportDataSetBuilder) se.instantiateClass(Collections.EMPTY_MAP, reportInfo.getGroovyScriptPath());
        ReportDataDto reportData = dataSourceBuilder.getReportData(reportParams);

        return reportData;
    }

    private static boolean validateParams(ReportInfo reportQuery, Map<String, String> queryParams) {
        for(String requiredParam : reportQuery.getRequiredParamsList()) {
            if(!queryParams.containsKey(requiredParam)) {
               return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportInfo> getAllReports() {
        return reportDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ReportInfo getReportByName(String name) {
        return reportDao.findByName(name);
    }
}
