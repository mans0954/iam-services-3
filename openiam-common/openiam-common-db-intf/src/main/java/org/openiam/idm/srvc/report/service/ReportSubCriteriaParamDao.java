package org.openiam.idm.srvc.report.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.report.domain.ReportSubCriteriaParamEntity;

import java.util.List;
public interface ReportSubCriteriaParamDao extends BaseDao<ReportSubCriteriaParamEntity, String> {

    List<ReportSubCriteriaParamEntity> findByReportInfoId(String reportInfoId);

    List<ReportSubCriteriaParamEntity> findByReportInfoName(String reportInfoName);
}
