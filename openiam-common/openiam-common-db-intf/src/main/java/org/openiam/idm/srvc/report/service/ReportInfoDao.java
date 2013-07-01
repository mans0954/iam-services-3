package org.openiam.idm.srvc.report.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.report.domain.ReportInfoEntity;

public interface ReportInfoDao extends BaseDao<ReportInfoEntity, String> {
    ReportInfoEntity findByName(String name);
}
