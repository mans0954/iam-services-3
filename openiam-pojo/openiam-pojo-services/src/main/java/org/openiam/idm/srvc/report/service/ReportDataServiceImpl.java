package org.openiam.idm.srvc.report.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.report.domain.ReportCriteriaParamEntity;
import org.openiam.idm.srvc.report.domain.ReportParamMetaTypeEntity;
import org.openiam.idm.srvc.report.domain.ReportParamTypeEntity;
import org.openiam.idm.srvc.report.domain.ReportSubCriteriaParamEntity;
import org.openiam.idm.srvc.report.domain.ReportInfoEntity;
import org.openiam.idm.srvc.report.domain.ReportSubscriptionEntity;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.report.dto.ReportDataDto;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for providing report data
 *
 * @author vitaly.yakunin
 */
@Service
public class ReportDataServiceImpl implements ReportDataService {

    private static final Log log = LogFactory
	.getLog(ReportDataServiceImpl.class);
    @Autowired
    private ReportInfoDao reportDao;
    @Autowired
    private ReportSubscriptionDao reportSubscriptionDao;
    @Autowired
    private ReportCriteriaParamDao criteriaParamDao;
    @Autowired
    private ReportSubCriteriaParamDao subCriteriaParamDao;
    @Autowired
    private ReportParamTypeDao reportParamTypeDao;
    @Autowired
    private ReportParamMetaTypeDao reportParamMetaTypeDao;
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;
    @Value("${org.openiam.upload.root}")
    private String uploadRoot;
    @Override
    @Transactional(readOnly = true)
    public ReportDataDto getReportData(final String reportName, final Map<String, String> reportParams) throws ClassNotFoundException, ScriptEngineException, IOException {
        ReportInfoEntity reportInfo = reportDao.findByName(reportName);
        if (reportInfo == null) {
            throw new IllegalArgumentException("Invalid parameter list: report with name=" + reportName + " was not found in Database");
        }


        ReportDataSetBuilder dataSourceBuilder = (ReportDataSetBuilder) scriptRunner.instantiateClass(Collections.EMPTY_MAP, uploadRoot+"/report/", reportInfo.getReportDataSource());

        return dataSourceBuilder.getReportData(reportParams);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportInfoEntity> getAllReports( final int from, final int size) {
        return reportDao.findAllReports(from, size);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReportInfoEntity> getAllReports() {
        return reportDao.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public ReportInfoEntity getReportByName(String name) {
        return reportDao.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportInfoEntity getReport(String reportId) {
        return reportDao.findById(reportId);
    }

    @Override
    @Transactional
    public void deleteReportParam(String reportParamId) {
    	log.info("In deleteReportParam, reportId=" + reportParamId);
    	ReportCriteriaParamEntity entity = criteriaParamDao.findById(reportParamId);
    	log.info("In deleteReportParam, entity=" + entity);
    	criteriaParamDao.delete(entity);
    	log.info("Deleted");
    }

    @Override
    @Transactional
    public void deleteReport(String reportId) {
    	log.info("In deleteReport, reportId=" + reportId);
    	/*List<ReportCriteriaParamEntity> paramEntitiesSrc = criteriaParamDao.findByReportInfoId(reportId);
    	log.info("In deleteReport, parameters size=" + paramEntitiesSrc.size());
        for(ReportCriteriaParamEntity paramEntity : paramEntitiesSrc) {
        	criteriaParamDao.delete(paramEntity);
        }*/
    	ReportInfoEntity entity = reportDao.findById(reportId);
    	log.info("In deleteReport, entity=" + entity);
    	reportDao.delete(entity);
    	log.info("Deleted");
    }

    @Override
    @Transactional
    public ReportCriteriaParamEntity createOrUpdateReportParamInfo(ReportCriteriaParamEntity reportParam){

        final String paramTypeId = reportParam.getType() != null ? reportParam.getType().getId() : null;
        reportParam.setType(paramTypeId != null ? reportParamTypeDao.findById(paramTypeId) : null);

        final String metaTypeId = reportParam.getMetaType() != null ? reportParam.getMetaType().getId() : null;
        reportParam.setMetaType(metaTypeId != null ? reportParamMetaTypeDao.findById(metaTypeId) : null);

    	if (StringUtils.isBlank(reportParam.getId()))
    		reportParam = criteriaParamDao.add(reportParam);
    	else
    		criteriaParamDao.merge(reportParam);
    	return reportParam;
    	
    }

    @Override
    @Transactional
    public ReportInfoEntity createOrUpdateReportInfo(ReportInfoEntity report) {
    	report = reportDao.merge(report);
    	return report;
    }

    @Override
    @Transactional
    public void updateReportParametersByReportName(final String reportName, final List<ReportCriteriaParamEntity> parameters) {
        criteriaParamDao.save(parameters);
    }
    
    @Override
    @Transactional
    public void updateSubReportParametersByReportName(final String reportName, final List<ReportSubCriteriaParamEntity> parameters) {
    	subCriteriaParamDao.save(parameters);
    }


    @Override
    @Transactional
    public List<ReportCriteriaParamEntity> getReportParametersByReportId(String reportId) {
        return criteriaParamDao.findByReportInfoId(reportId);
    }
    
    @Override
    @Transactional
    public List<ReportCriteriaParamEntity> getAllReportParameters() {
        return criteriaParamDao.findAll();
    }
    
    @Override
    @Transactional
    public ReportCriteriaParamEntity getReportParameterByName(String reportId, String paramName){
    	return criteriaParamDao.getReportParameterByName(reportId, paramName);
    }

    @Override
    @Transactional
    public List<ReportCriteriaParamEntity> getReportParametersByReportName(String reportName) {
        return criteriaParamDao.findByReportInfoName(reportName);
    }

    @Override
    @Transactional
    public List<ReportSubCriteriaParamEntity> getSubReportParametersByReportName(String reportName) {
        return subCriteriaParamDao.findByReportInfoName(reportName);
    }

    @Override
    @Transactional
    public List<ReportSubCriteriaParamEntity> getSubReportParametersByReportId(String reportId) {
        return subCriteriaParamDao.findByReportInfoId(reportId);
    }

    @Override
    @Transactional
    public Integer getReportCount() {
        return reportDao.countAll().intValue();
    }
    
    
    @Override
    @Transactional
    public List<ReportParamTypeEntity> getReportParameterTypes() {
        return reportParamTypeDao.findAll();
    }

    @Override
    @Transactional
    public List<ReportParamMetaTypeEntity> getReportParamMetaTypes() {
        return reportParamMetaTypeDao.findAll();
    }

    @Override
    @Transactional
    public ReportSubscriptionEntity createOrUpdateSubscribedReportInfo(ReportSubscriptionEntity reportSubscriptionEntity){

    	if (StringUtils.isBlank(reportSubscriptionEntity.getReportId()))
    		reportSubscriptionEntity = reportSubscriptionDao.add(reportSubscriptionEntity);
    	else
    		reportSubscriptionDao.update(reportSubscriptionEntity);
    	
        /*List<ReportSubCriteriaParamEntity> paramEntitiesSrc = subCriteriaParamDao.findByReportInfoName(reportSubscriptionEntity.getReportName());
        for(ReportSubCriteriaParamEntity paramEntity : paramEntitiesSrc) {
        	subCriteriaParamDao.delete(paramEntity);
        }*/
    	return reportSubscriptionEntity;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReportSubscriptionEntity> getAllActiveSubscribedReports() {
        return reportSubscriptionDao.getAllActiveSubscribedReports();
    }    
    @Override
    @Transactional(readOnly = true)
    public List<ReportSubscriptionEntity> getAllSubscribedReports() {
        return reportSubscriptionDao.findAll();
    }

	@Override
	public Integer getSubscribedReportCount() {
		// TODO Auto-generated method stub
		return reportSubscriptionDao.countAll().intValue();
	}

	@Override
	public void deleteSubscribedReport(String reportId) {
		log.info("In deleteSubscribedReport, reportId=" + reportId);
    	/*List<ReportCriteriaParamEntity> paramEntitiesSrc = criteriaParamDao.findByReportInfoId(reportId);
    	log.info("In deleteReport, parameters size=" + paramEntitiesSrc.size());
        for(ReportCriteriaParamEntity paramEntity : paramEntitiesSrc) {
        	criteriaParamDao.delete(paramEntity);
        }*/
    	ReportSubscriptionEntity entity = reportSubscriptionDao.findById(reportId);
    	log.info("In deleteSubscribedReport, entity=" + entity);
    	reportSubscriptionDao.delete(entity);
    	log.info("Deleted");
		
	}

	@Override
	public ReportSubscriptionEntity getSubscriptionReportById(
			String reportId) {
		// TODO Auto-generated method stub
		return reportSubscriptionDao.findById(reportId);
	}

	@Override
	public List<ReportSubCriteriaParamEntity> getAllSubCriteriaParamReports() {
		// TODO Auto-generated method stub
		return subCriteriaParamDao.findAll();
	}
	
	@Override
	public List<ReportSubCriteriaParamEntity> getAllSubCriteriaParamReport(String reportId) {
		// TODO Auto-generated method stub
		return subCriteriaParamDao.findByReportInfoId(reportId);
	}

	@Override
	public Integer getSubCriteriaParamReportCount() {
		// TODO Auto-generated method stub
		return subCriteriaParamDao.countAll().intValue();
	}

	@Override
	public ReportCriteriaParamEntity getReportCriteriaParamById(String rcpId) {
		// TODO Auto-generated method stub
		return criteriaParamDao.findById(rcpId);
	}

	@Override
	public void deleteSubCriteriaParamReport(String reportId) {
		log.info("In deleteSubscribedReport, reportId=" + reportId);
    	/*List<ReportCriteriaParamEntity> paramEntitiesSrc = criteriaParamDao.findByReportInfoId(reportId);
    	log.info("In deleteReport, parameters size=" + paramEntitiesSrc.size());
        for(ReportCriteriaParamEntity paramEntity : paramEntitiesSrc) {
        	criteriaParamDao.delete(paramEntity);
        }*/
    	ReportSubCriteriaParamEntity entity = subCriteriaParamDao.findById(reportId);
    	log.info("In deleteSubscribedReport, entity=" + entity);
    	subCriteriaParamDao.delete(entity);
    	log.info("Deleted");
		
	}

	@Override
	public ReportSubCriteriaParamEntity createOrUpdateSubCriteriaParamReport(
			ReportSubCriteriaParamEntity entity) {
		if (StringUtils.isBlank(entity.getRscpId()))
			entity = subCriteriaParamDao.add(entity);
    	else
    		subCriteriaParamDao.update(entity);
		return entity;
	}    
}
