package org.openiam.idm.srvc.report.service;

import java.util.List;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.report.domain.ReportSubscriptionEntity;

public interface ReportSubscriptionDao extends BaseDao<ReportSubscriptionEntity, String> {
    ReportSubscriptionEntity findByName(String name);
    void createOrUpdateSubscribedReportInfo(ReportSubscriptionEntity reportSubscriptionEntity);
    List<ReportSubscriptionEntity> getAllActiveSubscribedReports();
}
