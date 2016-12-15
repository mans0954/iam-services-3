package org.openiam.idm.srvc.report.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.*;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.ReportSearchBean;
import org.openiam.idm.srvc.property.service.PropertyValueSweeper;
import org.openiam.idm.srvc.report.domain.ReportCriteriaParamEntity;
import org.openiam.idm.srvc.report.domain.ReportParamMetaTypeEntity;
import org.openiam.idm.srvc.report.domain.ReportParamTypeEntity;
import org.openiam.idm.srvc.report.domain.ReportSubCriteriaParamEntity;
import org.openiam.idm.srvc.report.domain.ReportInfoEntity;
import org.openiam.idm.srvc.report.dto.*;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.InitializingBean;
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
public class ReportDataServiceImpl implements ReportDataService, InitializingBean {

	private static final Log log = LogFactory.getLog(ReportDataServiceImpl.class);

	@Autowired
	private ReportInfoDao reportDao;
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
	private PropertyValueSweeper propertyValueSweeper;

	@Autowired
	@Qualifier("configurableGroovyScriptEngine")
	protected ScriptIntegration scriptRunner;
	@Value("${org.openiam.upload.root}")
	private String uploadRoot;

	private static final String DEFAULT_REPORT_TASK = "frameset";
	private static final String REPORT_PARAMETER_NAME = "__report";
	private static final String LOCALE_PARAMETER_NAME = "__locale";

	private static Map<String, Object> bindingMap = new HashMap<>(1);

	@Override
	@Transactional(readOnly = true)
	public ReportDataDto getReportData(final ReportQueryDto reportQuery) throws BasicDataServiceException {
		if (StringUtils.isNotBlank(reportQuery.getReportName())) {
			ReportInfoEntity reportInfo = reportDao.findByName(reportQuery.getReportName());
			if (reportInfo == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Invalid parameter list: report with name=" + reportQuery.getReportName() + " was not found in Database");
			}

			final String dateFormatProp = propertyValueSweeper.getString("org.openiam.date.format");
			bindingMap.put("dateFormat", new SimpleDateFormat(
					StringUtils.isNotBlank(dateFormatProp) ? dateFormatProp : "MM/dd/yyyy")
			);

			final String dateTimeFormatProp = propertyValueSweeper.getString("org.openiam.date.time.format");
			bindingMap.put("dateTimeFormat", new SimpleDateFormat(
					StringUtils.isNotBlank(dateTimeFormatProp) ? dateTimeFormatProp : "MM/dd/yyyy HH:mm:ss")
			);

			try {
				ReportDataSetBuilder dataSourceBuilder =  (ReportDataSetBuilder) scriptRunner.instantiateClass(bindingMap,
                        uploadRoot + "/report/", reportInfo.getReportDataSource());
				return dataSourceBuilder.getReportData(reportQuery);

			} catch (IOException e) {
				log.error("Error during instantiate of groovy class: " + e.getMessage(), e);
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, e.getMessage());
			}

		}
		throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Invalid parameter list: reportName=" + reportQuery.getReportName());
	}

	@Override
	@Transactional(readOnly = true)
	public String getReportUrl(ReportQueryDto reportQuery, String taskName, String reportBaseUrl, String locale) throws BasicDataServiceException{
		try {

			ReportInfoDto report = this.getReportByName(reportQuery.getReportName());
			if (report == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Invalid parameter list: report with name=" + reportQuery.getReportName() + " was not found in Database");
			}

			String taskPath = StringUtils.isNotBlank(taskName) ? taskName : DEFAULT_REPORT_TASK;
			String reportDesignName = report.getReportUrl();
			URIBuilder uriBuilder = new URIBuilder(reportBaseUrl);
			uriBuilder.setPath(uriBuilder.getPath() + taskPath);
			uriBuilder.setParameter(REPORT_PARAMETER_NAME, reportDesignName);
			if (reportQuery.getQueryParams() != null) {
				for (Map.Entry<String, List<String>> entry : reportQuery.getQueryParams().entrySet()  ) {
					if (CollectionUtils.isNotEmpty(entry.getValue())) {
						for(String value : entry.getValue()) {
							uriBuilder.addParameter(entry.getKey(), value);
						}
					}
				}
			}
			if (StringUtils.isNotBlank(locale)) {
				uriBuilder.setParameter(LOCALE_PARAMETER_NAME, locale);
			}
			return uriBuilder.toString();
		} catch (URISyntaxException ex) {
			throw new BasicDataServiceException(ResponseCode.REPORT_URL_GENERATION_FAIL, ex.getMessage());
		}
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
	public ReportInfoDto getReportByName(String name) throws BasicDataServiceException {
		if (StringUtils.isEmpty(name)) {
			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Invalid parameter list: reportName=" + name);
		}
		final ReportInfoEntity reportInfoEntity = reportDao.findByName(name);
		return reportInfoDozerConverter.convertToDTO(reportInfoEntity, true);
	}

	@Override
	@Transactional(readOnly = true)
	public ReportInfoDto getReport(String reportId) throws BasicDataServiceException{
		if (StringUtils.isEmpty(reportId)) {
			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Invalid parameter list: reportId=" + reportId);
		}
		ReportInfoEntity entity = reportDao.findById(reportId);
		return reportInfoDozerConverter.convertToDTO(entity, true);
	}

	@Override
	@Transactional
	public void deleteReportParam(String reportParamId) throws BasicDataServiceException{
		if (StringUtils.isEmpty(reportParamId)) {
			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Invalid parameter list: reportParamId=" + reportParamId);
		}
		log.info("In deleteReportParam, reportId=" + reportParamId);
		ReportCriteriaParamEntity entity = criteriaParamDao.findById(reportParamId);
		log.info("In deleteReportParam, entity=" + entity);
		criteriaParamDao.delete(entity);
		log.info("Deleted");
	}

	@Override
	@Transactional
	public void deleteReport(String reportId) throws BasicDataServiceException{
		validateDelete(reportId);

		log.info("In deleteReport, reportId=" + reportId);
		ReportInfoEntity entity = reportDao.findById(reportId);
		log.info("In deleteReport, entity=" + entity);
		reportDao.delete(entity);
		log.info("Deleted");
	}

	@Transactional
	private void validateDelete(String reportId) throws BasicDataServiceException{
		ReportInfoDto report = this.getReport(reportId);
		if (report == null) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, "Report does not exist");
		}
		if (report.getIsBuiltIn()) {
			throw new BasicDataServiceException(ResponseCode.PERMISSION_EXCEPTION, "Built-in report can not be deleted");
		}
	}

	@Override
	@Transactional
	public String createOrUpdateReportParamInfo(ReportCriteriaParamDto reportParam) throws BasicDataServiceException{
		if (StringUtils.isBlank(reportParam.getName())) {
			throw new BasicDataServiceException(ResponseCode.REPORT_PARAM_NAME_NOT_SET, ResponseCode.REPORT_PARAM_NAME_NOT_SET.toString());
		}

		if (StringUtils.isBlank(reportParam.getTypeId())) {
			throw new BasicDataServiceException(ResponseCode.REPORT_PARAM_TYPE_NOT_SET, ResponseCode.REPORT_PARAM_TYPE_NOT_SET.toString());
		}

		final ReportCriteriaParamDto found = this.getReportParameterByName(reportParam.getReportId(), reportParam.getName());
		if (found != null) {
			if (StringUtils.isBlank(reportParam.getId())) {
				throw new BasicDataServiceException(ResponseCode.NAME_TAKEN, ResponseCode.NAME_TAKEN.toString());
			}

			if (StringUtils.isNotBlank(reportParam.getId()) && !reportParam.getId().equals(found.getId())) {
				throw new BasicDataServiceException(ResponseCode.NAME_TAKEN, ResponseCode.NAME_TAKEN.toString());
			}
		}
		ReportCriteriaParamEntity entity = criteriaParamDozerConverter.convertToEntity(reportParam, true);
		if(log.isDebugEnabled()) {
			log.debug("In createOrUpdateReportParamInfo, converted entity:" + entity);
		}

		// put new param to the end of the parameters list
		if (entity.getDisplayOrder() == null) {
			final String reportId = entity.getReport().getReportId();
			List<ReportCriteriaParamEntity> params = criteriaParamDao.findByReportInfoId(reportId);
			int order = 1;
			for(ReportCriteriaParamEntity param : params) {
				if (param.getDisplayOrder() >= order) {
					order = param.getDisplayOrder() + 1;
				}
			}
			entity.setDisplayOrder(order);
		}

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
	public String createOrUpdateReportInfo(ReportInfoDto report) throws BasicDataServiceException {
		validateInternal(report);

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
	@Transactional
	public void validate(ReportInfoDto report) throws BasicDataServiceException{
		validateInternal(report);
	}

	@Transactional
	private void validateInternal(ReportInfoDto report) throws BasicDataServiceException {

		if (report == null) {
			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Parameter 'report' is not defined");
		}

		if (StringUtils.isBlank(report.getReportName())) {
			throw new BasicDataServiceException(ResponseCode.REPORT_NAME_NOT_SET);
		}
		if (StringUtils.isBlank(report.getReportDataSource())) {
			throw new BasicDataServiceException(ResponseCode.REPORT_DATASOURCE_NOT_SET);
		}
		if (StringUtils.isBlank(report.getReportUrl())) {
			throw new BasicDataServiceException(ResponseCode.REPORT_URL_NOT_SET);
		}

		// validate unique name
		final ReportInfoDto found = this.getReportByName( report.getReportName() );
		if (found != null && !found.getId().equals( report.getId() ) ) {
			throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
		}

		// validate built-in report name
		if ( report.getId() != null ) {
			final ReportInfoDto reportDto = this.getReport( report.getId() );
			if (reportDto.getIsBuiltIn() && !reportDto.getReportName().equals(report.getReportName())) {
				throw new BasicDataServiceException(ResponseCode.READONLY);
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReportCriteriaParamDto> getReportParametersByReportId(String reportId) throws BasicDataServiceException {
		if (StringUtils.isEmpty(reportId)) {
			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Invalid parameter list: reportId=" + reportId);
		}
		final List<ReportCriteriaParamEntity> params = criteriaParamDao.findByReportInfoId(reportId);
		return criteriaParamDozerConverter.convertToDTOList(params, false);
	}
	@Override
	@Transactional(readOnly = true)
	public List<ReportCriteriaParamDto> getReportParametersByReportName(String reportName)throws BasicDataServiceException {
		if (StringUtils.isEmpty(reportName)) {
			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Invalid parameter list: reportId=" + reportName);
		}
		final List<ReportCriteriaParamEntity> params = criteriaParamDao.findByReportInfoName(reportName);
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
	@Transactional(readOnly = true)
	public List<ReportSubCriteriaParamDto> getAllSubCriteriaParamReports(ReportSearchBean searchBean){
		List<ReportSubCriteriaParamEntity> entities =null;
		if(StringUtils.isNotBlank(searchBean.getKey())){
			entities = subCriteriaParamDao.findByReportInfoId(searchBean.getKey());
		} else {
			entities = subCriteriaParamDao.findAll();
		}
		return criteriaSubParamDozerConverter.convertToDTOList(entities, false);
	}

	@Override
	@Transactional(readOnly = true)
	public Integer getSubCriteriaParamReportCount() {
		return subCriteriaParamDao.countAll().intValue();
	}


	@Override
	@Transactional
	public void deleteSubCriteriaParamReport(String reportId)  throws BasicDataServiceException{
		if (StringUtils.isEmpty(reportId)) {
			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Invalid parameter list: reportId=" + reportId);
		}
		log.info("In deleteSubscribedReport, reportId=" + reportId);
		ReportSubCriteriaParamEntity entity = subCriteriaParamDao.findById(reportId);
		log.info("In deleteSubscribedReport, entity=" + entity);
		subCriteriaParamDao.delete(entity);
		log.info("Deleted");
	}

	@Override
	@Transactional
	public String createOrUpdateSubCriteriaParamReport(ReportSubCriteriaParamDto subCriteriaParamReport) throws BasicDataServiceException{
		if (subCriteriaParamReport == null) {
			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Parameter 'subCriteriaParamReport' is not defined");
		}
		if(StringUtils.isBlank(subCriteriaParamReport.getId())){
			throw new BasicDataServiceException(ResponseCode.SUBSCRIBED_ID_NOT_SET, ResponseCode.SUBSCRIBED_ID_NOT_SET.toString());
		}

		if(StringUtils.isBlank(subCriteriaParamReport.getValue())){
			throw new BasicDataServiceException(ResponseCode.SUBSCRIBED_VALUE_NOT_SET, ResponseCode.SUBSCRIBED_VALUE_NOT_SET.toString());
		}


		ReportSubCriteriaParamEntity entity = criteriaSubParamDozerConverter.convertToEntity(subCriteriaParamReport, true);
		if(log.isDebugEnabled()) {
			log.debug("In createOrUpdateReportParamInfo, converted entity:" + entity);
		}

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

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
}
