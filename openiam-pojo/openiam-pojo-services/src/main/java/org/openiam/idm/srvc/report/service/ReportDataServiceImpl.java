package org.openiam.idm.srvc.report.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.dozer.converter.*;
import org.openiam.idm.srvc.report.domain.ReportCriteriaParamEntity;
import org.openiam.idm.srvc.report.domain.ReportParamMetaTypeEntity;
import org.openiam.idm.srvc.report.domain.ReportParamTypeEntity;
import org.openiam.idm.srvc.report.domain.ReportSubCriteriaParamEntity;
import org.openiam.idm.srvc.report.domain.ReportInfoEntity;
import org.openiam.idm.srvc.report.domain.ReportSubscriptionEntity;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.report.dto.*;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
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
	private ReportInfoDozerConverter reportInfoDozerConverter;
	@Autowired
	private ReportSubscriptionDozerConverter reportSubscriptionDozerConverter;
	@Autowired
	private ReportCriteriaParamDozerConverter criteriaParamDozerConverter;
	@Autowired
	private ReportSubCriteriaParamDozerConverter criteriaSubParamDozerConverter;
	@Autowired
	private ReportParamTypeDozerConverter paramTypeDozerConverter;
	@Autowired
	private ReportParamMetaTypeDozerConverter paramMetaTypeDozerConverter;
	@Autowired
	private AuthorizationManagerService authManager;
	@Autowired
	private ResourceService resourceService;
	@Autowired
	private ResourceTypeDAO resourceTypeDAO;
	@Value("${org.openiam.resource.type.report}")
	protected String resourceTypeId;

	@Autowired
	@Qualifier("configurableGroovyScriptEngine")
	protected ScriptIntegration scriptRunner;
	@Value("${org.openiam.upload.root}")
	private String uploadRoot;
	@Override
	@Transactional(readOnly = true)
	public ReportDataDto getReportData(final ReportQueryDto reportQuery) throws ClassNotFoundException, ScriptEngineException, IOException {
		ReportInfoEntity reportInfo = reportDao.findByName(reportQuery.getReportName());
		if (reportInfo == null) {
			throw new IllegalArgumentException("Invalid parameter list: report with name=" + reportQuery.getReportName() + " was not found in Database");
		}
		ReportDataSetBuilder dataSourceBuilder = (ReportDataSetBuilder) scriptRunner.instantiateClass(Collections.EMPTY_MAP, uploadRoot+"/report/", reportInfo.getReportDataSource());
		return dataSourceBuilder.getReportData(reportQuery);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReportInfoDto> getReports(final int from, final int size) {
		final List<ReportInfoEntity> entities = reportDao.findAllReports(from, size);
		return reportInfoDozerConverter.convertToDTOList(entities, false);
	}

	@Override
	@Transactional(readOnly = true)
	public Integer getReportCount() {
		return reportDao.countAll().intValue();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReportInfoDto> getAllReports(String requestorId) {
		List<ReportInfoEntity> entities = reportDao.findAll();
		List<ReportInfoEntity> results = new ArrayList<>(entities.size());
		for (ReportInfoEntity report : entities) {
			if (hasAccess(report.getResourceId(), requestorId)) {
				results.add(report);
			}
		}
		return reportInfoDozerConverter.convertToDTOList(results, false);
	}

	@Override
	@Transactional(readOnly = true)
	public ReportInfoDto getReportByName(String name) {
		final ReportInfoEntity reportInfoEntity = reportDao.findByName(name);
		return reportInfoDozerConverter.convertToDTO(reportInfoEntity, true);
	}

	@Override
	@Transactional(readOnly = true)
	public ReportInfoDto getReport(String reportId) {
		ReportInfoEntity entity = reportDao.findById(reportId);
		return reportInfoDozerConverter.convertToDTO(entity, true);
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
		ReportInfoEntity entity = reportDao.findById(reportId);
		log.info("In deleteReport, entity=" + entity);
		reportDao.delete(entity);
		log.info("Deleted");
	}

	@Override
	@Transactional
	public String createOrUpdateReportParamInfo(ReportCriteriaParamDto reportParam){

		ReportCriteriaParamEntity entity = criteriaParamDozerConverter.convertToEntity(reportParam, true);
		log.debug("In createOrUpdateReportParamInfo, converted entity:" + entity);

		final String paramTypeId = entity.getType() != null ? entity.getType().getId() : null;
		entity.setType(paramTypeId != null ? reportParamTypeDao.findById(paramTypeId) : null);

		final String metaTypeId = entity.getMetaType() != null ? entity.getMetaType().getId() : null;
		entity.setMetaType(metaTypeId != null ? reportParamMetaTypeDao.findById(metaTypeId) : null);

		if (StringUtils.isBlank(entity.getId()))
			entity = criteriaParamDao.add(entity);
		else
			entity = criteriaParamDao.merge(entity);
		return entity.getId();

	}

	@Override
	@Transactional
	public String createOrUpdateReportInfo(ReportInfoDto report) {

		ReportInfoEntity entity = reportInfoDozerConverter.convertToEntity(report, true);

		ResourceEntity resource = null;
		if(StringUtils.isEmpty(entity.getResourceId())) {
			resource = new ResourceEntity();
			resource.setName(String.format("%s_%S", entity.getReportName(), System.currentTimeMillis()));
			resource.setResourceType(resourceTypeDAO.findById(resourceTypeId));
			resource.setIsPublic(false);
			resource.setCoorelatedName(entity.getReportName());
			resourceService.save(resource, null);
			entity.setResourceId(resource.getId());
		} else {
			resource = resourceService.findResourceById(entity.getResourceId());
			if(resource != null) {
				resource.setCoorelatedName(entity.getReportName());
				resourceService.save(resource, null);
			}
		}

		entity = reportDao.merge(entity);
		return entity.getReportId();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReportCriteriaParamDto> getReportParametersByReportId(String reportId) {
		final List<ReportCriteriaParamEntity> params = criteriaParamDao.findByReportInfoId(reportId);
		return criteriaParamDozerConverter.convertToDTOList(params, false);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReportCriteriaParamDto> getAllReportParameters() {
		final List<ReportCriteriaParamEntity> entities = criteriaParamDao.findAll();
		return criteriaParamDozerConverter.convertToDTOList(entities, false);
	}

	@Override
	@Transactional(readOnly = true)
	public ReportCriteriaParamDto getReportParameterByName(String reportId, String paramName){
		final ReportCriteriaParamEntity entity = criteriaParamDao.getReportParameterByName(reportId, paramName);
		return criteriaParamDozerConverter.convertToDTO(entity, false);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReportCriteriaParamDto> getReportParametersByReportName(String reportName) {
		final List<ReportCriteriaParamEntity> params = criteriaParamDao.findByReportInfoName(reportName);
		return criteriaParamDozerConverter.convertToDTOList(params, false);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReportSubCriteriaParamDto> getSubReportParametersByReportId(String reportId) {
		final List<ReportSubCriteriaParamEntity> entities = subCriteriaParamDao.findByReportInfoId(reportId);
		return criteriaSubParamDozerConverter.convertToDTOList(entities, false);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReportParamTypeDto> getReportParameterTypes() {
		List<ReportParamTypeEntity> entities = reportParamTypeDao.findAll();
		return paramTypeDozerConverter.convertToDTOList(entities, false);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReportParamMetaTypeDto> getReportParamMetaTypes() {
		List<ReportParamMetaTypeEntity> entities = reportParamMetaTypeDao.findAll();
		return paramMetaTypeDozerConverter.convertToDTOList(entities, false);
	}

	@Override
	@Transactional
	public String createOrUpdateSubscribedReportInfo(ReportSubscriptionDto reportSubscriptionDto){

		ReportSubscriptionEntity entity = reportSubscriptionDozerConverter
				.convertToEntity(reportSubscriptionDto, true);
		if (StringUtils.isBlank(entity.getReportId())) {
			entity = reportSubscriptionDao.add(entity);
		} else {
			entity = reportSubscriptionDao.merge(entity);
		}
		return entity.getReportId();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReportSubscriptionDto> getAllActiveSubscribedReports() {
		final List<ReportSubscriptionEntity> entities = reportSubscriptionDao.getAllActiveSubscribedReports();
		return reportSubscriptionDozerConverter.convertToDTOList(entities, true);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReportSubscriptionDto> getAllSubscribedReports() {
		final List<ReportSubscriptionEntity> entities = reportSubscriptionDao.findAll();
		return reportSubscriptionDozerConverter.convertToDTOList(entities, false);
	}

	@Override
	@Transactional(readOnly = true)
	public Integer getSubscribedReportCount() {
		return reportSubscriptionDao.countAll().intValue();
	}

	@Override
	@Transactional
	public void deleteSubscribedReport(String reportId) {
		log.info("In deleteSubscribedReport, reportId=" + reportId);
		ReportSubscriptionEntity entity = reportSubscriptionDao.findById(reportId);
		log.info("In deleteSubscribedReport, entity=" + entity);
		reportSubscriptionDao.delete(entity);
		log.info("Deleted");
	}

	@Override
	@Transactional(readOnly = true)
	public ReportSubscriptionDto getSubscriptionReportById(String reportId) {
		final ReportSubscriptionEntity entity = reportSubscriptionDao.findById(reportId);
		return reportSubscriptionDozerConverter.convertToDTO(entity, true);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReportSubCriteriaParamDto> getAllSubCriteriaParamReports() {
		final List<ReportSubCriteriaParamEntity> entities = subCriteriaParamDao.findAll();
		return criteriaSubParamDozerConverter.convertToDTOList(entities, false);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReportSubCriteriaParamDto> getAllSubCriteriaParamReport(String reportId) {
		final List<ReportSubCriteriaParamEntity> entities = subCriteriaParamDao.findByReportInfoId(reportId);
		return criteriaSubParamDozerConverter.convertToDTOList(entities, false);
	}

	@Override
	@Transactional(readOnly = true)
	public Integer getSubCriteriaParamReportCount() {
		return subCriteriaParamDao.countAll().intValue();
	}

	@Override
	@Transactional(readOnly = true)
	public ReportCriteriaParamDto getReportCriteriaParamById(String rcpId) {
		ReportCriteriaParamEntity entity = criteriaParamDao.findById(rcpId);
		return criteriaParamDozerConverter.convertToDTO(entity, false);
	}

	@Override
	@Transactional
	public void deleteSubCriteriaParamReport(String reportId) {
		log.info("In deleteSubscribedReport, reportId=" + reportId);
		ReportSubCriteriaParamEntity entity = subCriteriaParamDao.findById(reportId);
		log.info("In deleteSubscribedReport, entity=" + entity);
		subCriteriaParamDao.delete(entity);
		log.info("Deleted");
	}

	@Override
	@Transactional
	public String createOrUpdateSubCriteriaParamReport(ReportSubCriteriaParamDto subCriteriaParamReport) {

		ReportSubCriteriaParamEntity entity = criteriaSubParamDozerConverter.convertToEntity(
				subCriteriaParamReport, true);
		log.debug("In createOrUpdateReportParamInfo, converted entity:" + entity);

		if (StringUtils.isBlank(entity.getRscpId())) {
			entity = subCriteriaParamDao.add(entity);
		} else {
			entity = subCriteriaParamDao.merge(entity);
		}
		return entity.getRscpId();
	}

	private boolean hasAccess(final String resourceId, final String userId) {
		ResourceEntity res = resourceService.findResourceById(resourceId);
		return /*res.getIsPublic() || */authManager.isEntitled(userId, resourceId);
	}
}
